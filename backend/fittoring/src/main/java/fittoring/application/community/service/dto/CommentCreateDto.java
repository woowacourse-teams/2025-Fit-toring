package fittoring.application.community.service.dto;

import fittoring.application.community.presentation.dto.request.CommentCreateRequest;

public record CommentCreateDto(
        Long memberId,
        Long postId,
        String content,
        boolean isAnonymous,
        String nickname,
        String guestPassword,
        Long rootId,
        Long parentId
) {

    public static CommentCreateDto of(Long memberId, Long postId, CommentCreateRequest request) {
        return new CommentCreateDto(
                memberId,
                postId,
                request.content(),
                request.isAnonymous(),
                request.nickname(),
                request.guestPassword(),
                request.rootId(),
                request.parentId()
        );
    }
}
