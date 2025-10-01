package fittoring.mentoring.business.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ChatRoomTest {

    @DisplayName("채팅방이 생성될 때 활성화(ACTIVATE) 상태로 생성된다.")
    @Test
    void create() {
        //given
        Long reservationId = 1L;
        Long menteeId = 1L;
        Long mentorId = 1L;

        //when
        ChatRoom actual = new ChatRoom(reservationId, menteeId, mentorId);

        //then
        assertThat(actual.getStatus()).isEqualTo(ChatStatus.ACTIVATE);
    }
}
