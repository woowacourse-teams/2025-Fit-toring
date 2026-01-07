package fittoring.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.InvalidMemberRoleException;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MemberRoleTest {

    @DisplayName("사용자가 mentee라면 true 반환한다.")
    @Test
    void isMenteeTrue() {
        // given
        MemberRole mentee = MemberRole.MENTEE;

        // when
        boolean actual = MemberRole.isMentee(mentee);

        // then
        assertThat(actual).isTrue();
    }

    @DisplayName("사용자가 mentor 혹은 admin 이라면 false 반환한다.")
    @Test
    void isMenteeFalse() {
        // given
        MemberRole mentor = MemberRole.MENTOR;
        MemberRole admin = MemberRole.ADMIN;

        // when
        boolean actual1 = MemberRole.isMentee(mentor);
        boolean actual2 = MemberRole.isMentee(admin);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            assertThat(actual1).isFalse();
            assertThat(actual2).isFalse();
        });
    }

    @DisplayName("존재하지 않는 권한으로 생성하는 경우 예외가 발생한다.")
    @Test
    void invalidRole() {
        //given
        String invalidRole = "INVALID";

        //when //then
        assertThatThrownBy(() -> MemberRole.of(invalidRole))
                .isInstanceOf(InvalidMemberRoleException.class)
                .hasMessage(BusinessErrorMessage.ROLE_NOT_FOUND.getMessage());
    }

    @DisplayName("존재하는 권한으로 정상 생성된다.")
    @Test
    void create() {
        //given
        String invalidRole = "ADMIN";

        //when //then
        assertThatCode(() -> MemberRole.of(invalidRole))
                .doesNotThrowAnyException();
    }
}
