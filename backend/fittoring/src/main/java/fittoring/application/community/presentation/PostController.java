package fittoring.application.community.presentation;

import fittoring.application.community.presentation.dto.request.GuestPasswordRequest;
import fittoring.application.community.presentation.dto.request.PostCreateRequest;
import fittoring.application.community.presentation.dto.request.PostUpdateRequest;
import fittoring.application.community.presentation.dto.response.PostDetailResponse;
import fittoring.application.community.presentation.dto.response.PostListResponse;
import fittoring.application.community.service.LikeActorResolver;
import fittoring.application.community.service.PostService;
import fittoring.application.community.service.dto.PostCreateDto;
import fittoring.application.community.service.dto.PostDeleteDto;
import fittoring.application.community.service.dto.PostUpdateDto;
import fittoring.config.auth.AuthRequired;
import fittoring.config.auth.Login;
import fittoring.config.auth.LoginInfo;
import fittoring.domain.model.LikeActorKeyHash;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class PostController {

    private final PostService postService;
    private final LikeActorResolver likeActorResolver;

    @AuthRequired
    @PostMapping("/posts")
    public ResponseEntity<PostDetailResponse> createPost(
            @Login LoginInfo loginInfo,
            @Valid @RequestBody PostCreateRequest request
    ) {
        PostDetailResponse response = postService.createPost(PostCreateDto.of(loginInfo.memberId(), request));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/posts")
    public ResponseEntity<PostListResponse> findPosts(@RequestParam(required = false) String cursorCode) {
        return ResponseEntity.ok(postService.findPosts(cursorCode));
    }

    @AuthRequired
    @GetMapping("/posts/{postId}")
    public ResponseEntity<PostDetailResponse> findPost(
            @Login LoginInfo loginInfo,
            @PathVariable Long postId,
            @CookieValue(name = LikeActorResolver.COOKIE_NAME, required = false) String actorId
    ) {
        LikeActorKeyHash actorKeyHash = likeActorResolver.resolve(actorId);
        PostDetailResponse response = postService.findPost(postId, loginInfo.memberId(), actorKeyHash);
        return ResponseEntity.ok(response);
    }

    @AuthRequired
    @PatchMapping("/posts/{postId}")
    public ResponseEntity<Void> modifyPost(
            @Login LoginInfo loginInfo,
            @PathVariable Long postId,
            @RequestBody PostUpdateRequest request
    ) {
        postService.modifyPost(PostUpdateDto.of(loginInfo.memberId(), postId, request));
        return ResponseEntity.ok().build();
    }

    @AuthRequired
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deletePost(
            @Login LoginInfo loginInfo,
            @PathVariable Long postId,
            @RequestBody(required = false) GuestPasswordRequest request
    ) {
        postService.deletePost(PostDeleteDto.of(loginInfo.memberId(), postId, request));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/posts/{postId}/guest-check")
    public ResponseEntity<Void> validateGuestPassword(
            @PathVariable Long postId,
            @Valid @RequestBody GuestPasswordRequest request
    ) {
        postService.validateGuestPassword(postId, request.guestPassword());
        return ResponseEntity.ok().build();
    }
}
