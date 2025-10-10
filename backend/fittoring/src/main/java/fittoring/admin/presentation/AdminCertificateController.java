package fittoring.admin.presentation;

import fittoring.admin.presentation.dto.AdminCertificateResponse;
import fittoring.admin.presentation.dto.PageResult;
import fittoring.admin.service.AdminCertificateService;
import fittoring.application.mentoring.presentation.dto.response.CertificateDetailResponse;
import fittoring.config.auth.AuthRequired;
import fittoring.config.auth.Login;
import fittoring.config.auth.LoginInfo;
import fittoring.domain.model.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/admin/certificates")
@RestController
public class AdminCertificateController {

    private final AdminCertificateService adminCertificateService;

    @AuthRequired
    @GetMapping
    public ResponseEntity<PageResult<AdminCertificateResponse>> getAllCertificates(
            @Login LoginInfo loginInfo,
            @RequestParam(value = "type", required = false) Status status,
            @RequestParam(defaultValue = "1") int page
    ) {
        PageResult<AdminCertificateResponse> certificates = adminCertificateService.getAllCertificatesPaged(
                loginInfo.memberId(),
                status,
                page
        );
        return ResponseEntity.status(HttpStatus.OK)
            .body(certificates);
    }

    @AuthRequired
    @GetMapping("/{certificateId}")
    public ResponseEntity<CertificateDetailResponse> getCertificate(
            @Login LoginInfo loginInfo,
            @PathVariable("certificateId") Long certificateId
    ) {
        CertificateDetailResponse response = adminCertificateService.getCertificate(
                loginInfo.memberId(),
                certificateId
        );
        return ResponseEntity.status(HttpStatus.OK)
            .body(response);
    }

    @AuthRequired
    @PostMapping("/{certificateId}/approve")
    public ResponseEntity<Void> approveCertificate(
            @Login LoginInfo loginInfo,
            @PathVariable("certificateId") Long certificateId
    ) {
        adminCertificateService.approveCertificate(
                loginInfo.memberId(),
                certificateId
        );
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
            .build();
    }

    @AuthRequired
    @PostMapping("/{certificateId}/reject")
    public ResponseEntity<Void> rejectCertificate(
            @Login LoginInfo loginInfo,
            @PathVariable("certificateId") Long certificateId
    ) {
        adminCertificateService.rejectCertificate(
                loginInfo.memberId(),
                certificateId
        );
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
            .build();
    }
}
