package fittoring.application.community.service.dto;

import fittoring.application.community.presentation.dto.request.GuestPasswordRequest;

public record PostDeleteDto(
        Long memberId,
        Long postId,
        String guestPassword
) {

    public static PostDeleteDto of(Long memberId, Long postId, GuestPasswordRequest request) {
        return new PostDeleteDto(memberId, postId, request == null ? null : request.guestPassword());
    }
}
