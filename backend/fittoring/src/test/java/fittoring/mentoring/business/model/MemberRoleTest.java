package fittoring.mentoring.business.model;

import static org.assertj.core.api.Assertions.assertThat;

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

}