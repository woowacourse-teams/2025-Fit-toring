package fittoring.application.chat.service;

import fittoring.application.chat.repository.ChatMessageRepository;
import fittoring.application.chat.repository.ChatRoomRepository;
import fittoring.application.chat.service.dto.ChatMessagePersistEventDto;
import fittoring.application.chat.service.dto.ChatPersistLatencyLog;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.ChatRoomNotFoundException;
import fittoring.application.exception.MemberNotFoundException;
import fittoring.application.image.service.ImageService;
import fittoring.application.member.repository.MemberRepository;
import fittoring.application.notification.service.NotificationService;
import fittoring.domain.model.ChatMessage;
import fittoring.domain.model.ChatMessageType;
import fittoring.domain.model.ChatRoom;
import fittoring.domain.model.ImageType;
import fittoring.domain.model.Notification;
import fittoring.logging.AppJsonLogger;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ChatMessagePersistenceService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final NotificationService notificationService;
    private final MemberRepository memberRepository;
    private final ImageService imageService;
    private final AppJsonLogger appJsonLogger;
    private final MeterRegistry meterRegistry;
    private final Timer chatPersistTotalTimer;
    private final Timer chatPersistDbTimer;
    private final Timer chatPersistNotificationTimer;
    private final Timer chatPersistImageTimer;
    private final Counter chatPersistDuplicateCounter;

    public ChatMessagePersistenceService(
            ChatMessageRepository chatMessageRepository,
            ChatRoomRepository chatRoomRepository,
            NotificationService notificationService,
            MemberRepository memberRepository,
            ImageService imageService,
            AppJsonLogger appJsonLogger,
            MeterRegistry meterRegistry
    ) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.notificationService = notificationService;
        this.memberRepository = memberRepository;
        this.imageService = imageService;
        this.appJsonLogger = appJsonLogger;
        this.meterRegistry = meterRegistry;
        this.chatPersistTotalTimer = createTimer(
                meterRegistry,
                "chat_persist_total_seconds",
                "End-to-end latency of chat persistence flow"
        );
        this.chatPersistDbTimer = createTimer(
                meterRegistry,
                "chat_persist_db_seconds",
                "Latency of database work in chat persistence flow"
        );
        this.chatPersistNotificationTimer = createTimer(
                meterRegistry,
                "chat_persist_notification_seconds",
                "Latency of notification preparation and dispatch in chat persistence flow"
        );
        this.chatPersistImageTimer = createTimer(
                meterRegistry,
                "chat_persist_image_seconds",
                "Latency of image metadata persistence in chat persistence flow"
        );
        this.chatPersistDuplicateCounter = meterRegistry.counter("chat_persist_duplicate_total");
    }

    @Transactional
    public boolean persist(ChatMessagePersistEventDto event) {
        long startedAtNanos = System.nanoTime();
        Timer.Sample totalSample = Timer.start(meterRegistry);
        try {
            PersistedMessage persistedMessage = persistMessage(event);
            if (persistedMessage == null) {
                chatPersistDuplicateCounter.increment();
                return false;
            }

            long imageElapsedNanos = 0;
            if (persistedMessage.chatMessage().getMessageType() == ChatMessageType.IMAGE) {
                Timer.Sample imageSample = Timer.start(meterRegistry);
                long imageStartedAtNanos = System.nanoTime();
                try {
                    imageService.save(ImageType.CHAT, persistedMessage.chatMessage().getId(), event.content());
                } finally {
                    imageSample.stop(chatPersistImageTimer);
                    imageElapsedNanos = System.nanoTime() - imageStartedAtNanos;
                }
            }
            long notificationElapsedNanos = sendNewMessageNotification(
                    event.chatRoomId(),
                    event.senderId(),
                    persistedMessage.opponentId(),
                    persistedMessage.chatMessage()
            );
            long totalElapsedNanos = System.nanoTime() - startedAtNanos;
            appJsonLogger.info("채팅 저장 레이턴시", new ChatPersistLatencyLog(
                    "chat_persist_latency",
                    persistedMessage.chatMessage().getMessageId(),
                    event.chatRoomId(),
                    event.senderId(),
                    persistedMessage.opponentId(),
                    persistedMessage.chatMessage().getMessageType().name(),
                    TimeUnit.NANOSECONDS.toMillis(persistedMessage.dbElapsedNanos()),
                    TimeUnit.NANOSECONDS.toMillis(imageElapsedNanos),
                    TimeUnit.NANOSECONDS.toMillis(notificationElapsedNanos),
                    TimeUnit.NANOSECONDS.toMillis(totalElapsedNanos)
            ));
            return true;
        } finally {
            totalSample.stop(chatPersistTotalTimer);
        }
    }

    private PersistedMessage persistMessage(ChatMessagePersistEventDto event) {
        Timer.Sample dbSample = Timer.start(meterRegistry);
        long dbStartedAtNanos = System.nanoTime();
        try {
            if (chatMessageRepository.existsByMessageId(event.messageId())) {
                log.info("이미 저장된 채팅 메시지입니다. messageId={}", event.messageId());
                return null;
            }

            ChatRoom chatRoom = getChatRoom(event.chatRoomId());
            ChatMessage savedChatMessage = saveChatMessage(chatRoom, event);
            chatRoom.updateLastMessage(savedChatMessage);

            log.info("채팅을 보낸 사람 id: {}", event.senderId());
            Long opponentId = chatRoom.getOpponentIdOf(event.senderId());
            long dbElapsedNanos = System.nanoTime() - dbStartedAtNanos;
            return new PersistedMessage(savedChatMessage, opponentId, dbElapsedNanos);
        } finally {
            dbSample.stop(chatPersistDbTimer);
        }
    }

    private ChatMessage saveChatMessage(ChatRoom chatRoom, ChatMessagePersistEventDto event) {
        ChatMessage chatMessage;
        if (event.messageType() == ChatMessageType.IMAGE) {
            chatMessage = new ChatMessage(
                    event.messageId(),
                    chatRoom.getId(),
                    event.senderId(),
                    event.content(),
                    ChatMessageType.IMAGE
            );
        } else {
            chatMessage = new ChatMessage(
                    event.messageId(),
                    chatRoom.getId(),
                    event.senderId(),
                    event.content()
            );
        }
        return chatMessageRepository.save(chatMessage);
    }

    private ChatRoom getChatRoom(Long chatRoomId) {
        return chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ChatRoomNotFoundException(
                        BusinessErrorMessage.CHAT_ROOM_NOT_FOUND.getMessage()
                ));
    }

    private Timer createTimer(MeterRegistry meterRegistry, String name, String description) {
        return Timer.builder(name)
                .description(description)
                .publishPercentileHistogram(true)
                .publishPercentiles(0.5, 0.95, 0.99)
                .register(meterRegistry);
    }

    private long sendNewMessageNotification(Long chatRoomId, Long senderId, Long opponentId, ChatMessage chatMessage) {
        Timer.Sample notificationSample = Timer.start(meterRegistry);
        long notificationStartedAtNanos = System.nanoTime();
        try {
            String senderName = memberRepository.findNameById(senderId)
                    .orElseThrow(() -> new MemberNotFoundException(BusinessErrorMessage.MEMBER_NOT_FOUND.getMessage()));
            Notification notification = new Notification(senderName, chatMessage.getContent());
            if (chatMessage.getMessageType() == ChatMessageType.IMAGE) {
                notification.setImageNotificationBody();
            }
            notification.putData("chatRoomId", String.valueOf(chatRoomId));
            notificationService.sendNotification(opponentId, notification);
            return System.nanoTime() - notificationStartedAtNanos;
        } finally {
            notificationSample.stop(chatPersistNotificationTimer);
        }
    }

    private record PersistedMessage(ChatMessage chatMessage, Long opponentId, long dbElapsedNanos) {
    }
}
