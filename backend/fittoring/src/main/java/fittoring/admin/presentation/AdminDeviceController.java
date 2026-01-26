package fittoring.admin.presentation;

import fittoring.admin.presentation.dto.AdminDeviceResponse;
import fittoring.admin.presentation.dto.PageResult;
import fittoring.admin.service.AdminDeviceService;
import fittoring.application.notification.service.NotificationService;
import fittoring.config.auth.Admin;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/admin/devices")
@RestController
public class AdminDeviceController {

    private final AdminDeviceService adminDeviceService;
    private final NotificationService notificationService;

    @Admin
    @GetMapping
    public ResponseEntity<PageResult<AdminDeviceResponse>> getAllDevices(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        PageResult<AdminDeviceResponse> devices = adminDeviceService.getAllDevicesPaged(
                page,
                size
        );
        return ResponseEntity.status(HttpStatus.OK)
                .body(devices);
    }

    @Admin
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable("id") Long id) {
        notificationService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }
}
