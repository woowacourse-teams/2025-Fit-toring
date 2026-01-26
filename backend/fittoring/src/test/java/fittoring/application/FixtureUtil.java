package fittoring.application;

import fittoring.domain.model.*;
import fittoring.domain.model.password.Password;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class FixtureUtil {

    public static Member getTestMentee() {
        return new Member(
                "menteeId",
                Gender.MALE,
                "이름",
                new Phone("010-1234-5670"),
                Password.from("password"));
    }

    public static Member getTestMentee(int i) {
        String phoneSuffix = String.format("%02d", i);
        return new Member(
                "menteeId" + i,
                Gender.MALE,
                "이름",
                new Phone("010-1234-" + String.format("%04d", i)),
                Password.from("password"));
    }

    public static Member getTestMentor() {
        return new Member(
                "mentorId",
                Gender.MALE,
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
                Gender.MALE,
                "멘토이름",
                new Phone("010-1234-" + String.format("%04d", i)),
                Password.from("password"),
                MemberRole.MENTEE
        );
    }

    public static Member getTestAdmin() {
        return new Member(
                "adminId",
                Gender.FEMALE,
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
                "introduction"
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

    public static Image getTestImageForMentoringProfileDefault(Mentoring mentoring) {
        return new Image("멘토링이미지1url", ImageType.MENTORING_PROFILE, ImageVariant.DEFAULT, mentoring.getId(),
                "baseName");
    }

    public static Image getTestImageForMentoringProfileThumbnail(Mentoring mentoring) {
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

    public static PhoneVerification getVerifiedPhoneVerification(Phone phone){
        PhoneVerification phoneVerification = new PhoneVerification(
                phone,
                "123456",
                LocalDateTime.now(ZoneId.of("Asia/Seoul"))
                        .plusMinutes(15)
        );
        phoneVerification.verify();
        return phoneVerification;
    }

    public static Device getTestDevices(Member member){
        return new Device(member, "pushToken");
    }

    public static Device getTestDevices(Member member, String pushTokenPrefix){
        return new Device(member, pushTokenPrefix + "pushToken");
    }
}
