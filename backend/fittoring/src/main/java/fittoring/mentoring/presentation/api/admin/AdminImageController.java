package fittoring.mentoring.presentation.api.admin;

import fittoring.config.auth.AuthRequired;
import fittoring.config.auth.Login;
import fittoring.config.auth.LoginInfo;
import fittoring.mentoring.business.exception.BusinessErrorMessage;
import fittoring.mentoring.business.exception.ForbiddenException;
import fittoring.mentoring.business.model.Image;
import fittoring.mentoring.business.model.ImageType;
import fittoring.mentoring.business.service.ImageService;
import fittoring.mentoring.business.service.MemberService;
import fittoring.mentoring.presentation.dto.ImageRequest;
import fittoring.mentoring.presentation.dto.ImageResponse;
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
