package fittoring.admin.repository;

import fittoring.admin.presentation.dto.AdminReservationResponse;
import fittoring.admin.presentation.dto.PageResult;

public interface CustomReservationRepository {

    PageResult<AdminReservationResponse> findReservationsForAdmin(int page, int size);
}
