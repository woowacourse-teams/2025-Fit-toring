package fittoring.admin.presentation;

import fittoring.admin.presentation.dto.DummySqlInsertRequest;
import fittoring.admin.presentation.dto.DummySqlInsertResponse;
import fittoring.admin.presentation.dto.DummySqlInsertStatusResponse;
import fittoring.admin.service.DummyAdminService;
import fittoring.config.auth.Admin;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/dummy/sql-insert")
@ConditionalOnProperty(name = "dummy.admin-api.enabled", havingValue = "true")
@RequiredArgsConstructor
public class DummyAdminController {

    private final DummyAdminService service;

    @Admin
    @PostMapping("/{fileSeq}")
    public ResponseEntity<DummySqlInsertResponse> insert(
            @PathVariable int fileSeq,
            @RequestBody(required = false) DummySqlInsertRequest request
    ) {
        return ResponseEntity.ok(service.insert(fileSeq, request == null ? null : request.startAt()));
    }

    @Admin
    @GetMapping("/{fileSeq}")
    public ResponseEntity<DummySqlInsertStatusResponse> status(@PathVariable int fileSeq) {
        return ResponseEntity.ok(service.status(fileSeq));
    }
}
