package fittoring.admin.repository;

import fittoring.admin.presentation.dto.AdminReservationResponse;
import java.util.List;

public interface CustomReservationRepository {

    List<AdminReservationResponse> findReservationsForAdmin(int page, int size);
}
