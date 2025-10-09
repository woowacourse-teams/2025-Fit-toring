package fittoring.admin.service;

import fittoring.admin.presentation.dto.AdminReservationResponse;
import fittoring.admin.presentation.dto.PageResult;
import fittoring.admin.repository.CustomReservationRepository;
import fittoring.admin.service.dto.AdminMentoringReservationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AdminReservationQueryService {

    private final AdminMemberQueryService memberService;
    private final CustomReservationRepository reservationRepository;

    public PageResult<AdminReservationResponse> findMentoringReservationsForAdmin(
            AdminMentoringReservationDto dto
    ) {
        memberService.validateAdminAuthorization(dto.memberId());
        return reservationRepository.findReservationsForAdmin(
                dto.page(),
                dto.size()
        );
    }
}
