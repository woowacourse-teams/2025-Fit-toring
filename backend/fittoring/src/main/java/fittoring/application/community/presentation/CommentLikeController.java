package fittoring.application.community.presentation;

import fittoring.application.community.presentation.dto.response.CommentLikeResponse;
import fittoring.application.community.service.CommentLikeService;
import fittoring.application.community.service.LikeActorResolver;
import fittoring.domain.model.LikeActorKeyHash;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class CommentLikeController {

    private final CommentLikeService commentLikeService;
    private final LikeActorResolver likeActorResolver;

    @PostMapping("/posts/{postId}/comments/{commentId}/like")
    public ResponseEntity<CommentLikeResponse> like(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @CookieValue(name = LikeActorResolver.COOKIE_NAME, required = false) String actorId,
            HttpServletResponse httpResponse
    ) {
        LikeActorKeyHash actorKeyHash = likeActorResolver.resolveOrCreate(actorId, httpResponse);
        CommentLikeResponse response = commentLikeService.like(postId, commentId, actorKeyHash);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/posts/{postId}/comments/{commentId}/like")
    public ResponseEntity<CommentLikeResponse> unlike(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @CookieValue(name = LikeActorResolver.COOKIE_NAME, required = false) String actorId
    ) {
        LikeActorKeyHash actorKeyHash = likeActorResolver.resolve(actorId);
        CommentLikeResponse response = commentLikeService.unlike(postId, commentId, actorKeyHash);
        return ResponseEntity.ok(response);
    }
}
