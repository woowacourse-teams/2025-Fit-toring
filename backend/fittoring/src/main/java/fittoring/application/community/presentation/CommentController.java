package fittoring.application.community.presentation;

import fittoring.application.community.presentation.dto.request.CommentCreateRequest;
import fittoring.application.community.presentation.dto.request.CommentUpdateRequest;
import fittoring.application.community.presentation.dto.request.GuestPasswordRequest;
import fittoring.application.community.presentation.dto.response.CommentOwnershipResponse;
import fittoring.application.community.presentation.dto.response.CommentResponse;
import fittoring.application.community.service.CommentService;
import fittoring.application.community.service.LikeActorResolver;
import fittoring.application.community.service.dto.CommentCreateDto;
import fittoring.application.community.service.dto.CommentDeleteDto;
import fittoring.application.community.service.dto.CommentUpdateDto;
import fittoring.config.auth.AuthRequired;
import fittoring.config.auth.Login;
import fittoring.config.auth.LoginInfo;
import fittoring.domain.model.LikeActorKeyHash;
import jakarta.validation.Valid;
import java.util.List;
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
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class CommentController {

    private final CommentService commentService;
    private final LikeActorResolver likeActorResolver;

    @AuthRequired
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponse> createComment(
            @Login LoginInfo loginInfo,
            @PathVariable Long postId,
            @Valid @RequestBody CommentCreateRequest request
    ) {
        CommentResponse response = commentService.createComment(CommentCreateDto.of(loginInfo.memberId(), postId, request));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> findComments(
            @PathVariable Long postId,
            @CookieValue(name = LikeActorResolver.COOKIE_NAME, required = false) String actorId
    ) {
        LikeActorKeyHash actorKeyHash = likeActorResolver.resolve(actorId);
        return ResponseEntity.ok(commentService.findComments(postId, actorKeyHash));
    }

    @AuthRequired
    @GetMapping("/posts/{postId}/comments/mine")
    public ResponseEntity<CommentOwnershipResponse> findOwnedCommentIds(
            @Login LoginInfo loginInfo,
            @PathVariable Long postId
    ) {
        List<Long> mineCommentIds = commentService.findOwnedCommentIds(postId, loginInfo.memberId());
        return ResponseEntity.ok(new CommentOwnershipResponse(mineCommentIds));
    }

    @AuthRequired
    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<Void> modifyComment(
            @Login LoginInfo loginInfo,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentUpdateRequest request
    ) {
        commentService.modifyComment(CommentUpdateDto.of(loginInfo.memberId(), commentId, request));
        return ResponseEntity.ok().build();
    }

    @AuthRequired
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @Login LoginInfo loginInfo,
            @PathVariable Long commentId,
            @Valid @RequestBody(required = false) GuestPasswordRequest request
    ) {
        commentService.deleteComment(CommentDeleteDto.of(loginInfo.memberId(), commentId, request));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/comments/{commentId}/pw-check")
    public ResponseEntity<Void> validateGuestPassword(
            @PathVariable Long commentId,
            @Valid @RequestBody GuestPasswordRequest request
    ) {
        commentService.validateGuestPassword(commentId, request.guestPassword());
        return ResponseEntity.ok().build();
    }
}
