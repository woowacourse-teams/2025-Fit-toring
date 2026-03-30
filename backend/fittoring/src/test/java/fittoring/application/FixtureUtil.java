package fittoring.application;

import fittoring.domain.model.Certificate;
import fittoring.domain.model.CertificateType;
import fittoring.domain.model.ChatMessage;
import fittoring.domain.model.ChatMessageType;
import fittoring.domain.model.ChatRoom;
import fittoring.domain.model.Device;
import fittoring.domain.model.Gender;
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

    public static void saveVerifiedPhoneVerification(
            fittoring.application.auth.repository.PhoneVerificationRepository repository,
            Phone phone
    ) {
        repository.save(phone.getNumber(), "123456", 900);
        repository.markVerified(phone.getNumber());
    }

    public static Device testDevices(Member member) {
        return new Device(member, "pushToken");
    }

    public static Device testDevices(Member member, String pushTokenPrefix) {
        return new Device(member, pushTokenPrefix + "pushToken");
    }

    public static ChatRoom testChatRoom(Reservation reservation, Member mentor, Member mentee) {
        return new ChatRoom(reservation.getId(), mentee.getId(), mentor.getId());
    }

    public static ChatMessage testChatMessage(ChatRoom chatRoom, Member sender) {
        return new ChatMessage(chatRoom.getId(), sender.getId(), "테스트 메시지입니다.");
    }

    public static ChatMessage testImageChatMessage(ChatRoom chatRoom, Long senderId) {
        return new ChatMessage(chatRoom.getId(), senderId, "fittoring/dev/chat-image/default/test.jpg",
                ChatMessageType.IMAGE);
    }

    public static Image testChatImageDefault(ChatMessage chatMessage) {
        return new Image("originalUrl", ImageType.CHAT, ImageVariant.DEFAULT, chatMessage.getId(), "test.jpg");
    }

    public static Image testChatImageThumbnail(ChatMessage chatMessage) {
        return new Image("thumbnailUrl", ImageType.CHAT, ImageVariant.THUMBNAIL, chatMessage.getId(), "test.jpg");
    }
}
