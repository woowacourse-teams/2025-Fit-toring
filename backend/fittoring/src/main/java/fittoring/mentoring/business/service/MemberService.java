package fittoring.mentoring.business.service;

import fittoring.mentoring.business.exception.BusinessErrorMessage;
import fittoring.mentoring.business.exception.NotFoundMemberException;
import fittoring.mentoring.business.model.Image;
import fittoring.mentoring.business.model.ImageType;
import fittoring.mentoring.business.model.Member;
import fittoring.mentoring.business.model.MemberRole;
import fittoring.mentoring.business.model.Mentoring;
import fittoring.mentoring.business.repository.MemberRepository;
import fittoring.mentoring.business.repository.MentoringRepository;
import fittoring.mentoring.presentation.dto.MyInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final ImageService imageService;
    private final MentoringRepository mentoringRepository;

    public MyInfoResponse getMeInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundMemberException(BusinessErrorMessage.LOGIN_ID_NOT_FOUND.getMessage()));
        if (!MemberRole.isMentorOrHigher(member.getRole())) {
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
        return mentoringRepository.findByMentor(member)
                .orElse(null);
    }

    private Image findMentoringImage(Mentoring mentoring) {
        return imageService.findByImageTypeAndRelationId(ImageType.MENTORING_PROFILE, mentoring.getId())
                .orElse(null);
    }
}
