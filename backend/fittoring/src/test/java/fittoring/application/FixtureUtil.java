package fittoring.application;

import fittoring.domain.model.*;
import fittoring.domain.model.password.Password;

public class FixtureUtil {
    public static Member getTestMentee() {
        return new Member(
                "menteeId",
                "MALE",
                "이름",
                new Phone("010-1234-5670"),
                Password.from("password"));
    }

    public static Member getTestMentee(int i) {
        return new Member(
                "menteeId"+i,
                "MALE",
                "이름",
                new Phone("010-1234-567"+((i%9)+1)),
                Password.from("password"));
    }

    public static Member getTestMentor() {
        return new Member(
                "mentorId",
                "MALE",
                "멘토이름",
                new Phone("010-1234-5680"),
                Password.from("password"),
                MemberRole.MENTOR
        );
    }

    public static Member getTestMentor(int i) {
        return new Member(
                "mentorId"+i,
                "MALE",
                "멘토이름",
                new Phone("010-1234-568"+((i%9)+1)),
                Password.from("password"),
                MemberRole.MENTOR
        );
    }

    public static Member getTestAdmin() {
        return new Member(
                "adminId",
                "FEMALE",
                "관리자",
                new Phone("010-9876-5432"),
                Password.from("password"),
                MemberRole.ADMIN
        );
    }

    public static Mentoring getTestMentoring(Member member) {
        member.registerAsMentor();
        return new Mentoring(
                member,
                5000,
                5,
                "content",
                "introduction",
                "https://chatRoomUrl"
        );
    }

    public static Certificate getTestCertificate(Mentoring mentoring) {
        return new Certificate(
                CertificateType.LICENSE,
                "자격증",
                mentoring
        );
    }

    public static Reservation getTestReservation(Mentoring mentoring, Member mentee) {
        return new Reservation("예약 내용", Status.PENDING, mentoring, mentee);
    }

    public static Review getTestReview(Reservation reservation, Member reviewer) {
        return new Review(5, "좋았습니다.", reservation, reviewer);
    }

}
