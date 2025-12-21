package fittoring.application.notification.presentation;

import fittoring.application.notification.presentation.dto.request.FcmTokenUpsertRequest;
import fittoring.application.notification.service.MemberFcmTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class NotificationController {

    private final MemberFcmTokenService memberFcmTokenService;

    @PostMapping("/notification/token")
    public ResponseEntity<Void> upsertFcmToken(@RequestBody FcmTokenUpsertRequest requestBody) {
        memberFcmTokenService.upsertFcmToken(requestBody.memberId(), requestBody.token());
        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }
}
