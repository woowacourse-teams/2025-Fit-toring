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

    private static final int PAGE_SIZE = 10;

    private final MentoringRepository mentoringRepository;

    @Transactional(readOnly = true)
    public PageResult<AdminMentoringResponse> findAllForAdminPaged(Long memberId, int page) {
        Pageable pageable = PageRequest.of(page - 1, PAGE_SIZE, Sort.by("createdAt").descending());
        Page<AdminMentoringResponse> allPagination = mentoringRepository.findAllWithPagination(pageable);

        return new PageResult<>(
                allPagination.getContent(),
                allPagination.getNumber(),
                PAGE_SIZE,
                allPagination.getTotalElements(),
                allPagination.getTotalPages()
        );
    }
}
