package fittoring.admin.service;

import fittoring.admin.presentation.dto.AdminMemberResponse;
import fittoring.admin.presentation.dto.PageResult;
import fittoring.application.member.repository.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AdminMemberService {

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public PageResult<AdminMemberResponse> findAllForAdminPaged(int page, int size) {
        List<Long> ids = memberRepository.findMemberIdsForAdmin(page, size);
        List<AdminMemberResponse> responses = memberRepository.findMembersByIdsOrdered(ids);
        long total = memberRepository.count();
        int totalPages = (int) Math.max(1, (total + size - 1) / size);
        return new PageResult<>(responses, page, responses.size(), total, totalPages);
    }
}
