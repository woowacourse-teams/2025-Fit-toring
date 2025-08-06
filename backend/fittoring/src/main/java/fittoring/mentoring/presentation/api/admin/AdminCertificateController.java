package fittoring.mentoring.presentation.api.admin;

import fittoring.config.auth.Login;
import fittoring.config.auth.LoginInfo;
import fittoring.mentoring.business.model.Status;
import fittoring.mentoring.business.service.CertificateService;
import fittoring.mentoring.presentation.dto.CertificateDetailResponse;
import fittoring.mentoring.presentation.dto.CertificateResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
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

    private final CertificateService certificateService;

    @GetMapping("/")
    public ResponseEntity<List<CertificateResponse>> getAllCertificates(
            @Login LoginInfo loginInfo,
            @RequestParam(value = "type", required = false) Status status
    ) {
        List<CertificateResponse> certificates = certificateService.getAllCertificates(
                loginInfo.memberId(),
                status
        );
        return ResponseEntity.ok().body(certificates);
    }

    @GetMapping("/{certificateId}")
    public ResponseEntity<CertificateDetailResponse> getCertificate(
            @Login LoginInfo loginInfo,
            @PathVariable("certificateId") Long certificateId
    ) {
        CertificateDetailResponse response = certificateService.getCertificate(
                loginInfo.memberId(),
                certificateId
        );
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/{certificateId}/approve")
    public ResponseEntity<Void> approveCertificate(
            @Login LoginInfo loginInfo,
            @PathVariable("certificateId") Long certificateId
    ) {
        certificateService.approveCertificate(
                loginInfo.memberId(),
                certificateId
        );
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{certificateId}/reject")
    public ResponseEntity<Void> rejectCertificate(
            @Login LoginInfo loginInfo,
            @PathVariable("certificateId") Long certificateId
    ) {
        certificateService.rejectCertificate(
                loginInfo.memberId(),
                certificateId
        );
        return ResponseEntity.noContent().build();
    }
}
