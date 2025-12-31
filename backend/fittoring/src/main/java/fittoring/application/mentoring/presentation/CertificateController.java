package fittoring.application.mentoring.presentation;

import fittoring.application.mentoring.service.CertificateService;
import fittoring.application.mentoring.service.dto.CertificateDeleteDto;
import fittoring.config.auth.AuthRequired;
import fittoring.config.auth.Login;
import fittoring.config.auth.LoginInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class CertificateController {

    private final CertificateService certificateService;

    @AuthRequired
    @DeleteMapping("certificates/{certificateId}")
    public ResponseEntity<Void> deleteCertificate(
            @Login LoginInfo loginInfo,
            @PathVariable("certificateId") Long certificateId
    ) {
        CertificateDeleteDto dto = new CertificateDeleteDto(loginInfo.memberId(), certificateId);
        certificateService.deleteCertificate(dto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }
}
