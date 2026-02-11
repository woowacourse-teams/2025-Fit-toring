package fittoring.admin.presentation;

import fittoring.admin.presentation.dto.AdminCertificateResponse;
import fittoring.admin.presentation.dto.PageResult;
import fittoring.admin.service.AdminCertificateService;
import fittoring.application.mentoring.presentation.dto.response.CertificateDetailResponse;
import fittoring.config.auth.Admin;
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

    @Admin
    @GetMapping
    public ResponseEntity<PageResult<AdminCertificateResponse>> getAllCertificates(
            @RequestParam(value = "type", required = false) Status status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PageResult<AdminCertificateResponse> certificates = adminCertificateService.getAllCertificatesPaged(
                status,
                page,
                size
        );
        return ResponseEntity.status(HttpStatus.OK)
                .body(certificates);
    }

    @Admin
    @GetMapping("/{certificateId}")
    public ResponseEntity<CertificateDetailResponse> getCertificate(
            @PathVariable("certificateId") Long certificateId
    ) {
        CertificateDetailResponse response = adminCertificateService.getCertificate(certificateId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(response);
    }

    @Admin
    @PostMapping("/{certificateId}/approve")
    public ResponseEntity<Void> approveCertificate(
            @PathVariable("certificateId") Long certificateId
    ) {
        adminCertificateService.approveCertificate(certificateId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }

    @Admin
    @PostMapping("/{certificateId}/reject")
    public ResponseEntity<Void> rejectCertificate(
            @PathVariable("certificateId") Long certificateId
    ) {
        adminCertificateService.rejectCertificate(certificateId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }
}
