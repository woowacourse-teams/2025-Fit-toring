package fittoring.application.community.presentation;

import fittoring.application.community.presentation.dto.request.GuestPasswordRequest;
import fittoring.application.community.presentation.dto.request.PostCreateRequest;
import fittoring.application.community.presentation.dto.request.PostUpdateRequest;
import fittoring.application.community.presentation.dto.response.PostDetailResponse;
import fittoring.application.community.service.PostService;
import fittoring.application.community.service.dto.PostCreateDto;
import fittoring.application.community.service.dto.PostDeleteDto;
import fittoring.application.community.service.dto.PostUpdateDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/guest/posts")
public class GuestPostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostDetailResponse> createGuestPost(
            @Valid @RequestBody PostCreateRequest request
    ) {
        PostDetailResponse response = postService.createPost(PostCreateDto.of(null, request));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<Void> modifyGuestPost(
            @PathVariable Long postId,
            @Valid @RequestBody PostUpdateRequest request
    ) {
        postService.modifyPost(PostUpdateDto.of(null, postId, request));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deleteGuestPost(
            @PathVariable Long postId,
            @Valid @RequestBody GuestPasswordRequest request
    ) {
        postService.deletePost(PostDeleteDto.of(null, postId, request));
        return ResponseEntity.noContent().build();
    }
}
