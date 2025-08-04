package fittoring.mentoring.presentation.api.admin;

import fittoring.mentoring.business.model.Status;
import fittoring.mentoring.business.service.CertificateService;
import fittoring.mentoring.presentation.dto.CertificateDetailResponse;
import fittoring.mentoring.presentation.dto.CertificateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// TODO: 관리자만 접근 가능하도록 권한 수정
@RequiredArgsConstructor
@RequestMapping("/admin/certificates")
@RestController
public class AdminCertificateController {

    private final CertificateService certificateService;

    @GetMapping("/")
    public ResponseEntity<Page<CertificateResponse>> getAllCertificates(
            @RequestParam(value = "type", required = false) Status status,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "8") int size
    ) {
        Page<CertificateResponse> certificates = certificateService.getAllCertificates(status, page, size);
        return ResponseEntity.ok().body(certificates);
    }

    @GetMapping("/{certificateId}")
    public ResponseEntity<CertificateDetailResponse> getCertificate(@PathVariable("certificateId") Long certificateId) {
        CertificateDetailResponse response = certificateService.getCertificate(certificateId);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/{certificateId}/approve")
    public ResponseEntity<Void> approveCertificate(@PathVariable("certificateId") Long certificateId) {
        certificateService.approveCertificate(certificateId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{certificateId}/reject")
    public ResponseEntity<Void> rejectCertificate(@PathVariable("certificateId") Long certificateId) {
        certificateService.rejectCertificate(certificateId);
        return ResponseEntity.noContent().build();
    }
}
