package fittoring.mentoring.business.service;

import fittoring.mentoring.business.exception.BusinessErrorMessage;
import fittoring.mentoring.business.exception.ForbiddenException;
import fittoring.mentoring.business.exception.NotFoundMemberException;
import fittoring.mentoring.business.model.Image;
import fittoring.mentoring.business.model.ImageType;
import fittoring.mentoring.business.model.Member;
import fittoring.mentoring.business.model.MemberRole;
import fittoring.mentoring.business.model.Mentoring;
import fittoring.mentoring.business.repository.MemberRepository;
import fittoring.mentoring.business.repository.MentoringRepository;
import fittoring.mentoring.presentation.dto.AdminMemberResponse;
import fittoring.mentoring.presentation.dto.MyInfoResponse;
import fittoring.mentoring.presentation.dto.MyInfoSummaryResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final ImageService imageService;
    private final MentoringRepository mentoringRepository;

    public MyInfoResponse getMemberInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundMemberException(BusinessErrorMessage.LOGIN_ID_NOT_FOUND.getMessage()));
        if (MemberRole.isMentee(member.getRole())) {
            return MyInfoResponse.from(member);
        }
        Mentoring mentoring = getMentoring(member);
        if (mentoring == null) {
            return MyInfoResponse.from(member);
        }
        Image image = findMentoringImage(mentoring);
        return MyInfoResponse.of(member, image);
    }

    private Mentoring getMentoring(Member member) {
        return mentoringRepository.findByMentorId(member.getId())
                .orElse(null);
    }

    private Image findMentoringImage(Mentoring mentoring) {
        return imageService.findByImageTypeAndRelationId(ImageType.MENTORING_PROFILE, mentoring.getId())
                .orElse(null);
    }

    public MyInfoSummaryResponse getMemberInfoSummary(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundMemberException(BusinessErrorMessage.LOGIN_ID_NOT_FOUND.getMessage()));
        return MyInfoSummaryResponse.of(member);
    }

    public boolean getAdminMemberActiveStatus(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundMemberException(BusinessErrorMessage.LOGIN_ID_NOT_FOUND.getMessage()));
        if (member.isNotAdmin()) {
            throw new ForbiddenException(BusinessErrorMessage.FORBIDDEN_MEMBER.getMessage());
        }
        return true;
    }

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
