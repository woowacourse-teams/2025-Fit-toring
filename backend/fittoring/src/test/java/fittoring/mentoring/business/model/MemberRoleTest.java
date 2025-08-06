package fittoring.mentoring.business.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MemberRoleTest {

    @DisplayName("사용자가 mentee라면 false를 반환한다.")
    @Test
    void isMenteeTrue() {
        // given
        MemberRole mentee = MemberRole.MENTEE;

        // when
        boolean actual = MemberRole.isMentorOrHigher(mentee);

        // then
        assertThat(actual).isFalse();
    }

    @DisplayName("사용자가 mentor 혹은 admin 이라면 true를 반환한다.")
    @Test
    void isMenteeFalse() {
        // given
        MemberRole mentor = MemberRole.MENTOR;
        MemberRole admin = MemberRole.ADMIN;

        // when
        boolean actual1 = MemberRole.isMentorOrHigher(mentor);
        boolean actual2 = MemberRole.isMentorOrHigher(admin);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            assertThat(actual1).isTrue();
            assertThat(actual2).isTrue();
        });
    }

}