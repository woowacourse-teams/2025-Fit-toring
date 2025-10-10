package fittoring.admin.repository;

import fittoring.admin.presentation.dto.AdminCertificateResponse;
import fittoring.domain.model.Status;
import java.util.List;

public interface CustomCertificateRepository {

    List<AdminCertificateResponse> findAllWithFilterAndPagination(Status status, int page, int size);

    long countByStatus(Status status);
}
