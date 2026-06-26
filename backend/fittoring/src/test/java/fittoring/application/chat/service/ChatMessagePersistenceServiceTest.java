package fittoring.application.chat.service;

import fittoring.IntegrationTestSupport;
import fittoring.application.FixtureUtil;
import fittoring.application.chat.repository.ChatMessageRepository;
import fittoring.application.chat.repository.ChatRoomRepository;
import fittoring.application.chat.service.dto.ChatMessagePersistEventDto;
import fittoring.application.image.repository.ImageRepository;
import fittoring.application.member.repository.MemberRepository;
import fittoring.application.notification.service.NotificationSender;
import fittoring.domain.model.ChatMessage;
import fittoring.domain.model.ChatMessageType;
import fittoring.domain.model.ChatRoom;
import fittoring.domain.model.Image;
import fittoring.domain.model.Member;
import java.time.LocalDateTime;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

class ChatMessagePersistenceServiceTest extends IntegrationTestSupport {

    @Autowired
    private ChatMessagePersistenceService chatMessagePersistenceService;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ImageRepository imageRepository;

    @MockitoBean
    private NotificationSender notificationSender;

    @DisplayName("텍스트 메시지를 저장하면 채팅방 마지막 메시지 snapshot이 함께 갱신된다.")
    @Test
    void persistTextMessageUpdatesChatRoomSnapshot() {
        // given
        Member mentor = memberRepository.save(FixtureUtil.testMentor());
        Member mentee = memberRepository.save(FixtureUtil.testMentee());
        ChatRoom chatRoom = chatRoomRepository.save(FixtureUtil.testChatRoom(1L, mentee.getId(), mentor.getId()));

        ChatMessagePersistEventDto event = new ChatMessagePersistEventDto(
                "message-id-1",
                chatRoom.getId(),
                mentee.getId(),
                1L,
                "텍스트 메시지입니다.",
                ChatMessageType.TEXT,
                LocalDateTime.now()
        );

        // when
        chatMessagePersistenceService.persist(event);

        // then
        ChatRoom persistedChatRoom = chatRoomRepository.findById(chatRoom.getId()).orElseThrow();
        ChatMessage persistedChatMessage = chatMessageRepository.findById(persistedChatRoom.getLastMessageId())
                .orElseThrow();

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(persistedChatRoom.getLastMessageId()).isEqualTo(persistedChatMessage.getId());
            softly.assertThat(persistedChatRoom.getLastMessageContent()).isEqualTo(event.content());
            softly.assertThat(persistedChatRoom.getLastMessageType()).isEqualTo(ChatMessageType.TEXT);
            softly.assertThat(persistedChatRoom.getLastMessageSenderId()).isEqualTo(mentee.getId());
            softly.assertThat(persistedChatRoom.getLastMessageCreatedAt()).isNotNull();
        });
    }

    @DisplayName("이미지 메시지를 저장하면 채팅방 마지막 메시지 snapshot이 함께 갱신된다.")
    @Test
    void persistImageMessageUpdatesChatRoomSnapshot() {
        // given
        Member mentor = memberRepository.save(FixtureUtil.testMentor());
        Member mentee = memberRepository.save(FixtureUtil.testMentee());
        ChatRoom chatRoom = chatRoomRepository.save(FixtureUtil.testChatRoom(1L, mentee.getId(), mentor.getId()));

        ChatMessagePersistEventDto event = new ChatMessagePersistEventDto(
                "message-id-2",
                chatRoom.getId(),
                mentee.getId(),
                1L,
                "fittoring/dev/chat-image/default/test.jpg",
                ChatMessageType.IMAGE,
                LocalDateTime.now()
        );

        // when
        chatMessagePersistenceService.persist(event);

        // then
        ChatRoom persistedChatRoom = chatRoomRepository.findById(chatRoom.getId()).orElseThrow();
        ChatMessage persistedChatMessage = chatMessageRepository.findById(persistedChatRoom.getLastMessageId())
                .orElseThrow();

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(persistedChatRoom.getLastMessageId()).isEqualTo(persistedChatMessage.getId());
            softly.assertThat(persistedChatRoom.getLastMessageContent()).isEqualTo(event.content());
            softly.assertThat(persistedChatRoom.getLastMessageType()).isEqualTo(ChatMessageType.IMAGE);
            softly.assertThat(persistedChatRoom.getLastMessageSenderId()).isEqualTo(mentee.getId());
            softly.assertThat(persistedChatRoom.getLastMessageCreatedAt()).isNotNull();
            softly.assertThat(imageRepository.findAll()).hasSize(1);
        });
    }

    @DisplayName("이미지 메시지를 저장하면 image 테이블에 원본 s3Key가 key 필드로 저장되고 baseName이 추출된다.")
    @Test
    void persistImageMessageStoresOriginalKey() {
        // given
        Member mentor = memberRepository.save(FixtureUtil.testMentor());
        Member mentee = memberRepository.save(FixtureUtil.testMentee());
        ChatRoom chatRoom = chatRoomRepository.save(FixtureUtil.testChatRoom(1L, mentee.getId(), mentor.getId()));

        String s3Key = "fittoring/dev/chat-image/default/test.jpg";
        ChatMessagePersistEventDto event = new ChatMessagePersistEventDto(
                "message-id-3",
                chatRoom.getId(),
                mentee.getId(),
                1L,
                s3Key,
                ChatMessageType.IMAGE,
                LocalDateTime.now()
        );

        // when
        chatMessagePersistenceService.persist(event);

        // then
        ChatRoom persistedChatRoom = chatRoomRepository.findById(chatRoom.getId()).orElseThrow();
        Image image = imageRepository.findAll().getFirst();

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(image.getKey()).isEqualTo(s3Key);
            softly.assertThat(image.getUrl()).isNull();
            softly.assertThat(image.getBaseName()).isEqualTo("test");
            softly.assertThat(image.getRelationId()).isEqualTo(persistedChatRoom.getLastMessageId());
        });
    }

    @DisplayName("같은 messageId 이벤트를 재처리하면 메시지가 중복 저장되지 않는다.")
    @Test
    void persistSkipsDuplicateMessageId() {
        // given
        Member mentor = memberRepository.save(FixtureUtil.testMentor());
        Member mentee = memberRepository.save(FixtureUtil.testMentee());
        ChatRoom chatRoom = chatRoomRepository.save(FixtureUtil.testChatRoom(1L, mentee.getId(), mentor.getId()));

        ChatMessagePersistEventDto event = new ChatMessagePersistEventDto(
                "duplicate-message-id",
                chatRoom.getId(),
                mentee.getId(),
                1L,
                "중복 처리 테스트",
                ChatMessageType.TEXT,
                LocalDateTime.now()
        );

        // when
        chatMessagePersistenceService.persist(event);
        chatMessagePersistenceService.persist(event);

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(chatMessageRepository.findAll()).hasSize(1);
            softly.assertThat(chatMessageRepository.existsByMessageId("duplicate-message-id")).isTrue();
        });
    }
}
