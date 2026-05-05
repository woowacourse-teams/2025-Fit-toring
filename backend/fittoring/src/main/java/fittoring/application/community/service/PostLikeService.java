package fittoring.application.community.service;

import fittoring.application.community.presentation.dto.response.PostLikeResponse;
import fittoring.application.community.repository.PostLikeRepository;
import fittoring.application.community.repository.PostRepository;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.PostNotFoundException;
import fittoring.domain.model.LikeActorKeyHash;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PostLikeService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;

    @Transactional
    public PostLikeResponse like(Long postId, LikeActorKeyHash actorKeyHash) {
        Objects.requireNonNull(actorKeyHash, "actorKeyHash는 null일 수 없습니다.");
        validatePostExists(postId);
        int inserted = postLikeRepository.insertIgnore(postId, actorKeyHash.getValue());
        if (inserted > 0) {
            postRepository.increaseLikeCount(postId);
        }
        return PostLikeResponse.ofLike(postId, findLikeCount(postId));
    }

    @Transactional
    public PostLikeResponse unlike(Long postId, LikeActorKeyHash actorKeyHash) {
        validatePostExists(postId);
        if (actorKeyHash == null) {
            return new PostLikeResponse(postId, false, findLikeCount(postId));
        }
        long deleted = postLikeRepository.deleteByPostIdAndActorKeyHashValue(postId, actorKeyHash.getValue());
        if (deleted > 0) {
            postRepository.decreaseLikeCount(postId);
        }
        return PostLikeResponse.ofUnlike(postId, findLikeCount(postId));
    }

    private void validatePostExists(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new PostNotFoundException(BusinessErrorMessage.POST_NOT_FOUND.getMessage());
        }
    }

    private int findLikeCount(Long postId) {
        return postRepository.findLikeCountById(postId)
                .orElseThrow(() -> new PostNotFoundException(BusinessErrorMessage.POST_NOT_FOUND.getMessage()));
    }
}
