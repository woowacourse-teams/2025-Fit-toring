package fittoring.application.community.presentation.dto.response;

import fittoring.domain.model.Comment;
import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        String content,
        String nickname,
        boolean isAnonymous,
        boolean isGuestComment,
        Long rootId,
        Long parentId,
        boolean isDeleted,
        LocalDateTime createdAt
) {

    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getNickname(),
                comment.isAnonymous(),
                comment.isGuestComment(),
                comment.getRootId(),
                comment.getParentId(),
                comment.isDeleted(),
                comment.getCreatedAt()
        );
    }
}
