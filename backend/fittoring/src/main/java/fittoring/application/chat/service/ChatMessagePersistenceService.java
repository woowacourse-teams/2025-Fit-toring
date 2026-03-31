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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatMessagePersistenceService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final NotificationService notificationService;
    private final MemberRepository memberRepository;
    private final ImageService imageService;

    @Transactional
    public void persist(ChatMessagePersistEventDto event) {
        if (chatMessageRepository.existsByMessageId(event.messageId())) {
            log.info("이미 저장된 채팅 메시지입니다. messageId={}", event.messageId());
            return;
        }

        ChatRoom chatRoom = getChatRoom(event.chatRoomId());

        if (event.messageType() == ChatMessageType.IMAGE) {
            persistImageMessage(chatRoom, event);
            return;
        }
        persistTextMessage(chatRoom, event);
    }

    private void persistTextMessage(ChatRoom chatRoom, ChatMessagePersistEventDto event) {
        ChatMessage chatMessage = new ChatMessage(
                event.messageId(),
                chatRoom.getId(),
                event.senderId(),
                event.content()
        );
        ChatMessage savedChatMessage = chatMessageRepository.save(chatMessage);
        chatRoom.updateLastMessage(savedChatMessage);

        log.info("채팅을 보낸 사람 id: {}", event.senderId());
        Long opponentId = chatRoom.getOpponentIdOf(event.senderId());
        sendNewMessageNotification(chatRoom.getId(), event.senderId(), opponentId, savedChatMessage);
    }

    private void persistImageMessage(ChatRoom chatRoom, ChatMessagePersistEventDto event) {
        ChatMessage chatMessage = new ChatMessage(
                event.messageId(),
                chatRoom.getId(),
                event.senderId(),
                event.content(),
                ChatMessageType.IMAGE
        );
        ChatMessage savedChatMessage = chatMessageRepository.save(chatMessage);
        chatRoom.updateLastMessage(savedChatMessage);
        imageService.save(ImageType.CHAT, savedChatMessage.getId(), event.content());

        Long opponentId = chatRoom.getOpponentIdOf(event.senderId());
        sendNewMessageNotification(chatRoom.getId(), event.senderId(), opponentId, savedChatMessage);
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
}
