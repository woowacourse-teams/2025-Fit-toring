package fittoring.application.presentation.api.admin;

import fittoring.config.auth.AuthRequired;
import fittoring.config.auth.Login;
import fittoring.config.auth.LoginInfo;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.ForbiddenException;
import fittoring.domain.model.Image;
import fittoring.domain.model.ImageType;
import fittoring.application.service.ImageService;
import fittoring.application.service.MemberService;
import fittoring.application.presentation.dto.ImageRequest;
import fittoring.application.presentation.dto.ImageResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RequestMapping("/admin/images")
@RestController
public class AdminImageController {

    private final ImageService imageService;
    private final MemberService memberService;

    @AuthRequired
    @PostMapping
    public ResponseEntity<List<ImageResponse>> save(
            @Login LoginInfo loginInfo,
            @RequestPart(value = "image") MultipartFile image,
            @RequestPart(value = "data", required = false) ImageRequest imageInfo
    ) {
        if (!memberService.getAdminMemberActiveStatus(loginInfo.memberId())) {
            throw new ForbiddenException(BusinessErrorMessage.FORBIDDEN_MEMBER.getMessage());
        }
        if (imageInfo == null) {
            imageInfo = new ImageRequest(ImageType.NONE, 0L);
        }
        List<Image> images = imageService.uploadImageToS3(
                image,
                ImageType.getDir(imageInfo.imageType()),
                imageInfo != null ? imageInfo.imageType() : ImageType.NONE,
                imageInfo != null ? imageInfo.relationId() : 0L
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(images.stream()
                        .map(ImageResponse::from)
                        .toList());
    }
}
