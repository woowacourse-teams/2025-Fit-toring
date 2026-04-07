package fittoring.application.chat.service;

import fittoring.application.chat.repository.ChatMessageRepository;
import fittoring.application.chat.repository.ChatRoomRepository;
import fittoring.application.chat.service.dto.ChatMessagePersistEventDto;
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
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
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
    private final MeterRegistry meterRegistry;
    private final Timer chatPersistTotalTimer;
    private final Timer chatPersistDbTimer;
    private final Timer chatPersistNotificationTimer;
    private final Timer chatPersistImageTimer;

    public ChatMessagePersistenceService(
            ChatMessageRepository chatMessageRepository,
            ChatRoomRepository chatRoomRepository,
            NotificationService notificationService,
            MemberRepository memberRepository,
            ImageService imageService,
            MeterRegistry meterRegistry
    ) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.notificationService = notificationService;
        this.memberRepository = memberRepository;
        this.imageService = imageService;
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
    }

    @Transactional
    public void persist(ChatMessagePersistEventDto event) {
        Timer.Sample totalSample = Timer.start(meterRegistry);
        try {
            PersistedMessage persistedMessage = persistMessage(event);
            if (persistedMessage == null) {
                return;
            }

            if (persistedMessage.chatMessage().getMessageType() == ChatMessageType.IMAGE) {
                Timer.Sample imageSample = Timer.start(meterRegistry);
                try {
                    imageService.save(ImageType.CHAT, persistedMessage.chatMessage().getId(), event.content());
                } finally {
                    imageSample.stop(chatPersistImageTimer);
                }
            }

            Timer.Sample notificationSample = Timer.start(meterRegistry);
            try {
                sendNewMessageNotification(
                        event.chatRoomId(),
                        event.senderId(),
                        persistedMessage.opponentId(),
                        persistedMessage.chatMessage()
                );
            } finally {
                notificationSample.stop(chatPersistNotificationTimer);
            }
        } finally {
            totalSample.stop(chatPersistTotalTimer);
        }
    }

    private PersistedMessage persistMessage(ChatMessagePersistEventDto event) {
        Timer.Sample dbSample = Timer.start(meterRegistry);
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
            return new PersistedMessage(savedChatMessage, opponentId);
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

    private void sendNewMessageNotification(Long chatRoomId, Long senderId, Long opponentId, ChatMessage chatMessage) {
        String senderName = memberRepository.findNameById(senderId)
                .orElseThrow(() -> new MemberNotFoundException(BusinessErrorMessage.MEMBER_NOT_FOUND.getMessage()));

        Notification notification = new Notification(senderName, chatMessage.getContent());
        if (chatMessage.getMessageType() == ChatMessageType.IMAGE) {
            notification.setImageNotificationBody();
        }
        notification.putData("chatRoomId", String.valueOf(chatRoomId));
        notificationService.sendNotification(opponentId, notification);
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

    private record PersistedMessage(ChatMessage chatMessage, Long opponentId) {
    }
}
