package fittoring.admin.service;

import fittoring.admin.presentation.dto.AdminMemberResponse;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.ForbiddenException;
import fittoring.application.exception.NotFoundMemberException;
import fittoring.application.member.repository.MemberRepository;
import fittoring.domain.model.Member;
import fittoring.domain.model.MemberRole;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AdminMemberService {

    private final MemberRepository memberRepository;

    public List<AdminMemberResponse> findAllForAdmin(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundMemberException(BusinessErrorMessage.MEMBER_NOT_FOUND.getMessage()));
        if (MemberRole.isNotAdmin(member.getRole())) {
            throw new ForbiddenException(BusinessErrorMessage.FORBIDDEN_MEMBER.getMessage());
        }
        List<Member> members = memberRepository.findAllByOrderByRoleAsc();
        return members.stream()
                .map(AdminMemberResponse::from)
                .toList();
    }
}
