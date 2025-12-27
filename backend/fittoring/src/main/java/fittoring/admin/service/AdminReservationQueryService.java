package fittoring.admin.service;

import fittoring.admin.presentation.dto.AdminReservationResponse;
import fittoring.admin.presentation.dto.PageResult;
import fittoring.admin.service.dto.AdminMentoringReservationDto;
import fittoring.application.reservation.repository.ReservationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AdminReservationQueryService {

    private final ReservationRepository reservationRepository;

    public PageResult<AdminReservationResponse> findMentoringReservationsForAdmin(
            AdminMentoringReservationDto dto
    ) {
        List<AdminReservationResponse> content = reservationRepository.findReservationsForAdmin(
                dto.page(),
                dto.size()
        );
        long total = reservationRepository.count();
        int totalPages = (int) Math.max(1, (total + dto.size() - 1) / dto.size());
        return new PageResult<>(content, dto.page(), dto.size(), total, totalPages);
    }
}
