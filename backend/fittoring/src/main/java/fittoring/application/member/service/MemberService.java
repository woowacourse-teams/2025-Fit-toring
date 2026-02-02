package fittoring.application.member.service;

import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.DuplicatePhoneException;
import fittoring.application.exception.EmptyRequestException;
import fittoring.application.exception.ForbiddenException;
import fittoring.application.exception.MemberNotFoundException;
import fittoring.application.image.service.ImageService;
import fittoring.application.member.presentation.dto.request.MemberInfoUpdateRequest;
import fittoring.application.member.presentation.dto.response.MyInfoResponse;
import fittoring.application.member.presentation.dto.response.MyInfoSummaryResponse;
import fittoring.application.member.repository.MemberRepository;
import fittoring.application.mentoring.repository.MentoringRepository;
import fittoring.domain.model.Image;
import fittoring.domain.model.ImageType;
import fittoring.domain.model.Member;
import fittoring.domain.model.MemberRole;
import fittoring.domain.model.Mentoring;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final ImageService imageService;
    private final MentoringRepository mentoringRepository;

    public MyInfoResponse getMemberInfo(Long memberId) {
        Member member = getMember(memberId);
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
        return imageService.findThumbnail(ImageType.MENTORING_PROFILE, mentoring.getId())
                .orElse(null);
    }

    public MyInfoSummaryResponse getMemberInfoSummary(Long memberId) {
        Member member = getMember(memberId);
        return MyInfoSummaryResponse.of(member);
    }

    public boolean getAdminMemberActiveStatus(Long memberId) {
        Member member = getMember(memberId);
        if (member.isNotAdmin()) {
            throw new ForbiddenException(BusinessErrorMessage.FORBIDDEN_MEMBER.getMessage());
        }
        return true;
    }

    @Transactional
    public void updateMemberInfo(Long memberId, MemberInfoUpdateRequest request) {
        if (isEmptyRequest(request)) {
            throw new EmptyRequestException(BusinessErrorMessage.EMPTY_REQUEST.getMessage());
        }

        Member member = getMember(memberId);
        if (request.name() != null) {
            member.updateName(request.name());
        }

        if (request.gender() != null) {
            member.updateGender(request.gender());
        }

        if (request.phoneNumber() != null) {
            validateDuplicatePhoneNumber(request);
            member.updatePhoneNumber(request.phoneNumber());
        }

        if (request.password() != null) {
            member.updatePassword(request.password());
        }
    }

    private boolean isEmptyRequest(MemberInfoUpdateRequest request) {
        return request.name() == null && request.gender() == null
                && request.phoneNumber() == null && request.password() == null;
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(BusinessErrorMessage.MEMBER_NOT_FOUND.getMessage()));
    }

    private void validateDuplicatePhoneNumber(MemberInfoUpdateRequest request) {
        if (memberRepository.existsByPhone_Number(request.phoneNumber())) {
            throw new DuplicatePhoneException(BusinessErrorMessage.DUPLICATE_PHONE.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public Map<Long, String> findNameMapping(List<Long> ids) {

        return memberRepository.findAllById(ids)
                .stream()
                .collect(Collectors.toMap(
                        Member::getId,
                        Member::getName
                ));
    }
}
