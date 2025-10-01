package fittoring.mentoring.presentation.dto;

import fittoring.mentoring.business.model.Image;

public record ChatRoomResponse(
        String participant,
        String mentorName,
        String mentorProfileImageUrl,
        int mentoringPrice,
        Long senderId
) {
    public static ChatRoomResponse of(
            String participant,
            String mentorName,
            Image mentoringProfileImage,
            int price,
            Long memberId
    ) {
        String mentoringProfileImageUrl = getMentoringProfileImageUrl(mentoringProfileImage);
        return new ChatRoomResponse(
                participant,
                mentorName,
                mentoringProfileImageUrl,
                price,
                memberId
        );
    }

    private static String getMentoringProfileImageUrl(Image mentoringProfileImage) {
        if (mentoringProfileImage == null) {
            return "";
        }
        return mentoringProfileImage.getUrl();
    }
}
