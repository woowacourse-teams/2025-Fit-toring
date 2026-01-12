package fittoring.admin.service;

import fittoring.admin.presentation.dto.AdminMentoringResponse;
import fittoring.admin.presentation.dto.PageResult;
import fittoring.application.mentoring.repository.MentoringRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AdminMentoringService {

    private final MentoringRepository mentoringRepository;

    @Transactional(readOnly = true)
    public PageResult<AdminMentoringResponse> findAllForAdminPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page<AdminMentoringResponse> allPagination = mentoringRepository.findAllWithPagination(pageable);

        return new PageResult<>(
                allPagination.getContent(),
                allPagination.getNumber(),
                size,
                allPagination.getTotalElements(),
                allPagination.getTotalPages()
        );
    }
}
