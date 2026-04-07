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
import fittoring.domain.model.Member;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
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

    @Autowired
    private MeterRegistry meterRegistry;

    @MockitoBean
    private NotificationSender notificationSender;

    @DisplayName("텍스트 메시지를 저장하면 채팅방 마지막 메시지 snapshot이 함께 갱신된다.")
    @Test
    void persistTextMessageUpdatesChatRoomSnapshot() {
        // given
        double initialTotalCount = timerCount("chat_persist_total_seconds");
        double initialDbCount = timerCount("chat_persist_db_seconds");
        double initialNotificationCount = timerCount("chat_persist_notification_seconds");
        double initialImageCount = timerCount("chat_persist_image_seconds");

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
            softly.assertThat(timerCount("chat_persist_total_seconds")).isEqualTo(initialTotalCount + 1);
            softly.assertThat(timerCount("chat_persist_db_seconds")).isEqualTo(initialDbCount + 1);
            softly.assertThat(timerCount("chat_persist_notification_seconds"))
                    .isEqualTo(initialNotificationCount + 1);
            softly.assertThat(timerCount("chat_persist_image_seconds")).isEqualTo(initialImageCount);
        });
    }

    @DisplayName("이미지 메시지를 저장하면 채팅방 마지막 메시지 snapshot이 함께 갱신된다.")
    @Test
    void persistImageMessageUpdatesChatRoomSnapshot() {
        // given
        double initialTotalCount = timerCount("chat_persist_total_seconds");
        double initialDbCount = timerCount("chat_persist_db_seconds");
        double initialNotificationCount = timerCount("chat_persist_notification_seconds");
        double initialImageCount = timerCount("chat_persist_image_seconds");

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
            softly.assertThat(timerCount("chat_persist_total_seconds")).isEqualTo(initialTotalCount + 1);
            softly.assertThat(timerCount("chat_persist_db_seconds")).isEqualTo(initialDbCount + 1);
            softly.assertThat(timerCount("chat_persist_notification_seconds"))
                    .isEqualTo(initialNotificationCount + 1);
            softly.assertThat(timerCount("chat_persist_image_seconds")).isEqualTo(initialImageCount + 1);
        });
    }

    private double timerCount(String metricName) {
        Timer timer = meterRegistry.find(metricName).timer();
        if (timer == null) {
            return 0;
        }
        return timer.count();
    }
}
