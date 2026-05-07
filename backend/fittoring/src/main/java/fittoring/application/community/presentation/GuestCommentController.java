package fittoring.application.community.presentation;

import fittoring.application.community.presentation.dto.request.CommentCreateRequest;
import fittoring.application.community.presentation.dto.request.CommentUpdateRequest;
import fittoring.application.community.presentation.dto.request.GuestPasswordRequest;
import fittoring.application.community.presentation.dto.response.CommentResponse;
import fittoring.application.community.service.CommentService;
import fittoring.application.community.service.dto.CommentCreateDto;
import fittoring.application.community.service.dto.CommentDeleteDto;
import fittoring.application.community.service.dto.CommentUpdateDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class GuestCommentController {

    private final CommentService commentService;

    @PostMapping("/guest/posts/{postId}/comments")
    public ResponseEntity<CommentResponse> createGuestComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentCreateRequest request
    ) {
        CommentResponse response = commentService.createComment(CommentCreateDto.of(null, postId, request));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/guest/comments/{commentId}")
    public ResponseEntity<Void> modifyGuestComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentUpdateRequest request
    ) {
        commentService.modifyComment(CommentUpdateDto.of(null, commentId, request));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/guest/comments/{commentId}")
    public ResponseEntity<Void> deleteGuestComment(
            @PathVariable Long commentId,
            @Valid @RequestBody GuestPasswordRequest request
    ) {
        commentService.deleteComment(CommentDeleteDto.of(null, commentId, request));
        return ResponseEntity.noContent().build();
    }
}
