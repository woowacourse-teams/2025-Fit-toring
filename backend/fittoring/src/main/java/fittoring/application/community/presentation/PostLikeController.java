package fittoring.application.community.presentation;

import fittoring.application.community.presentation.dto.response.PostLikeResponse;
import fittoring.application.community.service.LikeActorResolver;
import fittoring.application.community.service.PostLikeService;
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
public class PostLikeController {

    private final PostLikeService postLikeService;
    private final LikeActorResolver likeActorResolver;

    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<PostLikeResponse> like(
            @PathVariable Long postId,
            @CookieValue(name = LikeActorResolver.COOKIE_NAME, required = false) String actorId,
            HttpServletResponse httpResponse
    ) {
        LikeActorKeyHash actorKeyHash = likeActorResolver.resolveOrCreate(actorId, httpResponse);
        PostLikeResponse response = postLikeService.like(postId, actorKeyHash);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/posts/{postId}/like")
    public ResponseEntity<PostLikeResponse> unlike(
            @PathVariable Long postId,
            @CookieValue(name = LikeActorResolver.COOKIE_NAME, required = false) String actorId
    ) {
        LikeActorKeyHash actorKeyHash = likeActorResolver.resolve(actorId);
        PostLikeResponse response = postLikeService.unlike(postId, actorKeyHash);
        return ResponseEntity.ok(response);
    }


}
