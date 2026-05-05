package fittoring.application.community.service;

import fittoring.application.community.presentation.dto.response.CommentLikeResponse;
import fittoring.application.community.repository.CommentLikeRepository;
import fittoring.application.community.repository.CommentRepository;
import fittoring.application.community.repository.PostRepository;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.CommentNotFoundException;
import fittoring.application.exception.ForbiddenException;
import fittoring.application.exception.PostNotFoundException;
import fittoring.domain.model.Comment;
import fittoring.domain.model.LikeActorKeyHash;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CommentLikeService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final PostRepository postRepository;

    @Transactional
    public CommentLikeResponse like(Long postId, Long commentId, LikeActorKeyHash actorKeyHash) {
        Objects.requireNonNull(actorKeyHash, "actorKeyHash는 null일 수 없습니다.");
        validateCommentBelongsToPost(postId, commentId);
        int inserted = commentLikeRepository.insertIgnore(commentId, actorKeyHash.getValue());
        if (inserted > 0) {
            commentRepository.increaseLikeCount(commentId);
        }
        return CommentLikeResponse.ofLike(commentId, findLikeCount(commentId));
    }

    @Transactional
    public CommentLikeResponse unlike(Long postId, Long commentId, LikeActorKeyHash actorKeyHash) {
        validateCommentBelongsToPost(postId, commentId);
        if (actorKeyHash == null) {
            return CommentLikeResponse.ofUnlike(commentId, findLikeCount(commentId));
        }
        long deleted = commentLikeRepository.deleteByCommentIdAndActorKeyHashValue(commentId, actorKeyHash.getValue());
        if (deleted > 0) {
            commentRepository.decreaseLikeCount(commentId);
        }
        return CommentLikeResponse.ofUnlike(commentId, findLikeCount(commentId));
    }

    private void validateCommentBelongsToPost(Long postId, Long commentId) {
        validatePostExists(postId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(BusinessErrorMessage.COMMENT_NOT_FOUND.getMessage()));
        if (!comment.belongsTo(postId)) {
            throw new ForbiddenException(BusinessErrorMessage.COMMENT_NOT_BELONG_TO_POST.getMessage());
        }
    }

    private void validatePostExists(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new PostNotFoundException(BusinessErrorMessage.POST_NOT_FOUND.getMessage());
        }
    }

    private int findLikeCount(Long commentId) {
        return commentRepository.findLikeCountById(commentId)
                .orElseThrow(() -> new CommentNotFoundException(BusinessErrorMessage.COMMENT_NOT_FOUND.getMessage()));
    }
}
