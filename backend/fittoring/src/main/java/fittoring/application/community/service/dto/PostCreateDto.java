package fittoring.application.community.service.dto;

import fittoring.application.community.presentation.dto.request.PostCreateRequest;

public record PostCreateDto(
        Long memberId,
        String title,
        String content,
        boolean isAnonymous,
        String nickname,
        String guestPassword
) {

    public static PostCreateDto of(Long memberId, PostCreateRequest request) {
        return new PostCreateDto(
                memberId,
                request.title(),
                request.content(),
                request.isAnonymous(),
                request.nickname(),
                request.guestPassword()
        );
    }
}
