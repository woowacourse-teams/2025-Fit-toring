package fittoring.application;

import fittoring.domain.model.*;
import fittoring.domain.model.password.Password;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class FixtureUtil {

    public static Member testMentee() {
        return new Member(
                "menteeId",
                Gender.MALE,
                "이름",
                new Phone("010-1234-5670"),
                Password.from("password"));
    }

    public static Member testMentee(int i) {
        String phoneSuffix = String.format("%02d", i);
        return new Member(
                "menteeId" + i,
                Gender.MALE,
                "이름",
                new Phone("010-1234-" + String.format("%04d", i)),
                Password.from("password"));
    }

    public static Member testMentor() {
        return new Member(
                "mentorId",
                Gender.MALE,
                "멘토이름",
                new Phone("010-1234-5680"),
                Password.from("password"),
                MemberRole.MENTOR
        );
    }

    public static Member testMentor(int i) {
        return new Member(
                "mentorId" + i,
                Gender.MALE,
                "멘토이름",
                new Phone("010-1234-" + String.format("%04d", i)),
                Password.from("password"),
                MemberRole.MENTEE
        );
    }

    public static Member testAdmin() {
        return new Member(
                "adminId",
                Gender.FEMALE,
                "관리자",
                new Phone("010-9876-5432"),
                Password.from("password"),
                MemberRole.ADMIN
        );
    }

    public static Mentoring testMentoring(Member mentor) {
        mentor.registerAsMentor();
        return new Mentoring(
                mentor,
                5000,
                5,
                "content",
                "introduction"
        );
    }

    public static Certificate testCertificate(Mentoring mentoring) {
        return new Certificate(
                CertificateType.LICENSE,
                "자격증",
                mentoring
        );
    }

    public static Reservation testPendingReservation(Mentoring mentoring, Member mentee) {
        return new Reservation("예약 내용", Status.PENDING, mentoring, mentee);
    }

    public static Reservation testCompletedReservation(Mentoring mentoring, Member mentee) {
        return new Reservation("예약 내용", Status.COMPLETE, mentoring, mentee);
    }

    public static Reservation testApprovedReservation(Mentoring mentoring, Member mentee) {
        return new Reservation("예약 내용", Status.APPROVED, mentoring, mentee);
    }

    public static Review testReview(Reservation reservation, Member reviewer) {
        return new Review(5, "좋았습니다.", reservation, reviewer);
    }

    public static Image testImageForMentoringProfileDefault(Mentoring mentoring) {
        return new Image("멘토링이미지1url", ImageType.MENTORING_PROFILE, ImageVariant.DEFAULT, mentoring.getId(),
                "baseName");
    }

    public static Image testImageForMentoringProfileThumbnail(Mentoring mentoring) {
        return new Image("멘토링이미지1url", ImageType.MENTORING_PROFILE, ImageVariant.THUMBNAIL, mentoring.getId(),
                "baseName");
    }

    public static ChatRoom testChatRoom(Long reservationId, Long menteeId, Long mentorId) {
        return new ChatRoom(
                reservationId,
                menteeId,
                mentorId
        );
    }

    public static PhoneVerification testVerifiedPhoneVerification(Phone phone){
        PhoneVerification phoneVerification = new PhoneVerification(
                phone,
                "123456",
                LocalDateTime.now(ZoneId.of("Asia/Seoul"))
                        .plusMinutes(15)
        );
        phoneVerification.verify();
        return phoneVerification;
    }

    public static Device testDevices(Member member){
        return new Device(member, "pushToken");
    }

    public static Device testDevices(Member member, String pushTokenPrefix){
        return new Device(member, pushTokenPrefix + "pushToken");
    }
}
