package fittoring.admin.service;

import fittoring.admin.presentation.dto.AdminSmsOutboxDetailResponse;
import fittoring.admin.presentation.dto.AdminSmsOutboxResponse;
import fittoring.admin.presentation.dto.PageResult;
import fittoring.application.exception.SmsOutboxNotFoundException;
import fittoring.application.reservation.repository.SmsOutboxRepository;
import fittoring.application.reservation.sms.SmsOutbox;
import fittoring.application.reservation.sms.SmsOutboxStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AdminSmsOutboxService {

    private final SmsOutboxRepository smsOutboxRepository;

    @Transactional(readOnly = true)
    public PageResult<AdminSmsOutboxResponse> findByStatus(SmsOutboxStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(
                Math.max(0, page - 1),
                size,
                Sort.by(Sort.Direction.DESC, "updatedAt").and(Sort.by(Sort.Direction.DESC, "id"))
        );
        Page<SmsOutbox> result = smsOutboxRepository.findByStatus(status, pageable);
        return new PageResult<>(
                result.getContent().stream().map(AdminSmsOutboxResponse::from).toList(),
                page,
                size,
                result.getTotalElements(),
                Math.max(1, result.getTotalPages())
        );
    }

    @Transactional(readOnly = true)
    public AdminSmsOutboxDetailResponse findDetail(Long id) {
        SmsOutbox row = findRow(id);
        return AdminSmsOutboxDetailResponse.from(row);
    }

    @Transactional
    public void retryManually(Long id) {
        SmsOutbox row = findRow(id);
        row.retryManually();
    }

    private SmsOutbox findRow(Long id) {
        return smsOutboxRepository.findById(id)
                .orElseThrow(() -> new SmsOutboxNotFoundException(
                        "SMS outbox row를 찾지 못했습니다. id=" + id
                ));
    }
}
