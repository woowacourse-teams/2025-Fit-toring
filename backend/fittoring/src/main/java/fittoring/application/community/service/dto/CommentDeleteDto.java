package fittoring.application.community.service.dto;

import fittoring.application.community.presentation.dto.request.GuestPasswordRequest;

public record CommentDeleteDto(
        Long memberId,
        Long commentId,
        String guestPassword
) {

    public static CommentDeleteDto of(Long memberId, Long commentId, GuestPasswordRequest request) {
        return new CommentDeleteDto(memberId, commentId, request == null ? null : request.guestPassword());
    }
}
