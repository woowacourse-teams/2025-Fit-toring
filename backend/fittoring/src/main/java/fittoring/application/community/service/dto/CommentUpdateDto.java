package fittoring.application.community.service.dto;

import fittoring.application.community.presentation.dto.request.CommentUpdateRequest;

public record CommentUpdateDto(
        Long memberId,
        Long commentId,
        String content,
        String guestPassword
) {

    public static CommentUpdateDto of(Long memberId, Long commentId, CommentUpdateRequest request) {
        return new CommentUpdateDto(
                memberId,
                commentId,
                request.content(),
                request.guestPassword()
        );
    }
}
