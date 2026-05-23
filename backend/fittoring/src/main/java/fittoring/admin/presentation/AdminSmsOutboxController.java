package fittoring.admin.presentation;

import fittoring.admin.presentation.dto.AdminSmsOutboxDetailResponse;
import fittoring.admin.presentation.dto.AdminSmsOutboxResponse;
import fittoring.admin.presentation.dto.PageResult;
import fittoring.admin.service.AdminSmsOutboxService;
import fittoring.config.auth.Admin;
import fittoring.domain.model.SmsOutboxStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class AdminSmsOutboxController {

    private final AdminSmsOutboxService adminSmsOutboxService;

    @Admin
    @GetMapping("/admin/sms-outbox")
    public ResponseEntity<PageResult<AdminSmsOutboxResponse>> findByStatus(
            @RequestParam(defaultValue = "FAILED") SmsOutboxStatus status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(adminSmsOutboxService.findByStatus(status, page, size));
    }

    @Admin
    @GetMapping("/admin/sms-outbox/{id}")
    public ResponseEntity<AdminSmsOutboxDetailResponse> findDetail(@PathVariable Long id) {
        return ResponseEntity.ok(adminSmsOutboxService.findDetail(id));
    }

    @Admin
    @PostMapping("/admin/sms-outbox/{id}/retry")
    public ResponseEntity<Void> retry(@PathVariable Long id) {
        adminSmsOutboxService.retryManually(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
