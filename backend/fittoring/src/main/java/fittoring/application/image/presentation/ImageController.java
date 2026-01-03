package fittoring.application.image.presentation;

import fittoring.application.image.presentation.dto.request.IssuedPresignedRequest;
import fittoring.application.image.presentation.dto.response.PresignedIssueResponse;
import fittoring.application.image.service.PresignedUrlService;
import fittoring.application.image.service.dto.IssuedPresignedDto;
import fittoring.config.auth.AuthRequired;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/images")
@RestController
public class ImageController {

    private final PresignedUrlService presignedUrlService;

    @AuthRequired
    @PostMapping("/presigned")
    public ResponseEntity<PresignedIssueResponse> issuePresignedUrl(
            @Valid @RequestBody IssuedPresignedRequest requestBody
    ) {
        PresignedIssueResponse presignedIssueResponse = presignedUrlService.issuePresignedUrl(
                new IssuedPresignedDto(
                        requestBody.imageType(),
                        requestBody.extension()
                ));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(presignedIssueResponse);
    }
}
