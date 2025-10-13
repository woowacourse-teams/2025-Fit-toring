package fittoring.admin.repository;

import fittoring.admin.presentation.dto.AdminCertificateResponse;
import fittoring.domain.model.Status;
import java.util.List;

public interface CustomCertificateRepository {

    List<Long> findCertificateIdsForAdmin(Status status, int page, int size);

    List<AdminCertificateResponse> findCertificatesByIdsOrdered(List<Long> ids);

    long countByStatus(Status status);
}
