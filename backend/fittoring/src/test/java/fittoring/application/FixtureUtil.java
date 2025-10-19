package fittoring.application;

import fittoring.domain.model.Certificate;
import fittoring.domain.model.CertificateType;
import fittoring.domain.model.ChatRoom;
import fittoring.domain.model.Image;
import fittoring.domain.model.ImageType;
import fittoring.domain.model.ImageVariant;
import fittoring.domain.model.Member;
import fittoring.domain.model.MemberRole;
import fittoring.domain.model.Mentoring;
import fittoring.domain.model.Phone;
import fittoring.domain.model.Reservation;
import fittoring.domain.model.Review;
import fittoring.domain.model.Status;
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
        String phoneSuffix = String.format("%02d", i);
        return new Member(
                "menteeId" + i,
                "MALE",
                "이름",
                new Phone("010-1234-56" + phoneSuffix),
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
        String phoneSuffix = String.format("%02d", i);
        return new Member(
                "mentorId" + i,
                "MALE",
                "멘토이름",
                new Phone("010-1234-56" + phoneSuffix),
                Password.from("password"),
                MemberRole.MENTEE
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

    public static Reservation getTestPendingReservation(Mentoring mentoring, Member mentee) {
        return new Reservation("예약 내용", Status.PENDING, mentoring, mentee);
    }

    public static Reservation getTestCompletedReservation(Mentoring mentoring, Member mentee) {
        return new Reservation("예약 내용", Status.COMPLETE, mentoring, mentee);
    }

    public static Reservation getTestApprovedReservation(Mentoring mentoring, Member mentee) {
        return new Reservation("예약 내용", Status.APPROVED, mentoring, mentee);
    }

    public static Review getTestReview(Reservation reservation, Member reviewer) {
        return new Review(5, "좋았습니다.", reservation, reviewer);
    }

    public static Image getTestImageForMentoringProfile(Mentoring mentoring) {
        return new Image("멘토링이미지1url", ImageType.MENTORING_PROFILE, ImageVariant.THUMBNAIL, mentoring.getId(),
                "baseName");
    }

    public static ChatRoom getTestChatRoom(Long reservationId, Long menteeId, Long mentorId) {
        return new ChatRoom(
                reservationId,
                menteeId,
                mentorId
        );
    }
}
