package fittoring.admin.presentation;

import fittoring.admin.presentation.dto.DummyScenarioPreviewResponse;
import fittoring.admin.presentation.dto.DummySqlInsertRequest;
import fittoring.admin.presentation.dto.DummySqlInsertResponse;
import fittoring.admin.presentation.dto.DummySqlInsertStatusResponse;
import fittoring.admin.service.DummyAdminService;
import fittoring.config.auth.Admin;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin/dummy/sql-insert")
@ConditionalOnProperty(name = "dummy.admin-api.enabled", havingValue = "true")
@RequiredArgsConstructor
public class DummyAdminController {

    private final DummyAdminService service;

    @Admin
    @GetMapping
    public ResponseEntity<List<DummySqlInsertStatusResponse>> list() {
        return ResponseEntity.ok(service.list());
    }

    @Admin
    @PostMapping("/{scenarioId}")
    public ResponseEntity<DummySqlInsertResponse> insert(
            @PathVariable long scenarioId,
            @RequestBody(required = false) DummySqlInsertRequest request
    ) {
        return ResponseEntity.ok(service.insert(
                scenarioId,
                request == null ? null : request.startAt(),
                request == null ? null : request.duration()
        ));
    }

    @Admin
    @GetMapping("/{scenarioId}/preview")
    public ResponseEntity<DummyScenarioPreviewResponse> preview(@PathVariable long scenarioId) {
        return ResponseEntity.ok(service.preview(scenarioId));
    }

    @Admin
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DummySqlInsertStatusResponse> upload(@RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(service.upload(file));
    }

    @Admin
    @GetMapping("/{scenarioId}")
    public ResponseEntity<DummySqlInsertStatusResponse> status(@PathVariable long scenarioId) {
        return ResponseEntity.ok(service.status(scenarioId));
    }

    @Admin
    @DeleteMapping("/{scenarioId}")
    public ResponseEntity<Void> delete(@PathVariable long scenarioId) {
        service.delete(scenarioId);
        return ResponseEntity.noContent().build();
    }
}
