package fittoring.application.community.service.dto;

import fittoring.application.community.presentation.dto.request.PostUpdateRequest;

public record PostUpdateDto(
        Long memberId,
        Long postId,
        String title,
        String content,
        String guestPassword
) {

    public static PostUpdateDto of(Long memberId, Long postId, PostUpdateRequest request) {
        return new PostUpdateDto(
                memberId,
                postId,
                request.title(),
                request.content(),
                request.guestPassword()
        );
    }
}
