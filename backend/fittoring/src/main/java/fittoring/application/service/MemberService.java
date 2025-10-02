package fittoring.application.service;

import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.ForbiddenException;
import fittoring.application.exception.NotFoundMemberException;
import fittoring.domain.model.Image;
import fittoring.domain.model.ImageType;
import fittoring.domain.model.Member;
import fittoring.domain.model.MemberRole;
import fittoring.domain.model.Mentoring;
import fittoring.application.repository.MemberRepository;
import fittoring.application.repository.MentoringRepository;
import fittoring.application.presentation.dto.AdminMemberResponse;
import fittoring.application.presentation.dto.MyInfoResponse;
import fittoring.application.presentation.dto.MyInfoSummaryResponse;
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
        return imageService.findThumbnailByImageTypeAndRelationId(ImageType.MENTORING_PROFILE, mentoring.getId())
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
