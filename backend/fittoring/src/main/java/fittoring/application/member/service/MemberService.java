package fittoring.application.member.service;

import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.DuplicatePhoneException;
import fittoring.application.exception.EmptyRequestException;
import fittoring.application.exception.ForbiddenException;
import fittoring.application.exception.InvalidImageKeyException;
import fittoring.application.exception.MemberNotFoundException;
import fittoring.application.image.service.ImageService;
import fittoring.application.image.service.PresignedUrlService;
import fittoring.application.member.presentation.dto.request.MemberInfoUpdateRequest;
import fittoring.application.member.presentation.dto.response.MyInfoResponse;
import fittoring.application.member.presentation.dto.response.MyInfoSummaryResponse;
import fittoring.application.member.repository.MemberRepository;
import fittoring.domain.model.Image;
import fittoring.domain.model.ImageType;
import fittoring.domain.model.ImageVariant;
import fittoring.domain.model.Member;
import fittoring.infrastructure.image.KeyBuilder;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final ImageService imageService;
    private final PresignedUrlService presignedUrlService;
    private final KeyBuilder keyBuilder;

    @Transactional(readOnly = true)
    public MyInfoResponse getMemberInfo(Long memberId) {
        Member member = getMember(memberId);
        return imageService.findDefault(ImageType.MEMBER_PROFILE, memberId)
                .map(Image::getKey)
                .map(presignedUrlService::issueGetPresignedUrl)
                .map(imageUrl -> MyInfoResponse.of(member, imageUrl))
                .orElseGet(() -> MyInfoResponse.from(member));
    }

    @Transactional(readOnly = true)
    public MyInfoSummaryResponse getMemberInfoSummary(Long memberId) {
        Member member = getMember(memberId);
        return MyInfoSummaryResponse.of(member);
    }

    @Transactional(readOnly = true)
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

        updateMemberProfileImage(memberId, request.profileImageKey());
    }

    private boolean isEmptyRequest(MemberInfoUpdateRequest request) {
        return request.name() == null && request.gender() == null
                && request.phoneNumber() == null && request.password() == null
                && request.profileImageKey() == null;
    }

    private void updateMemberProfileImage(Long memberId, String profileImageKey) {
        if (profileImageKey == null) {
            return;
        }
        if (profileImageKey.isBlank()) {
            imageService.delete(ImageType.MEMBER_PROFILE, memberId);
            return;
        }
        if (!keyBuilder.isValidKeyFor(ImageType.MEMBER_PROFILE, ImageVariant.DEFAULT, profileImageKey)) {
            throw new InvalidImageKeyException(BusinessErrorMessage.IMAGE_NOT_FOUND.getMessage());
        }
        if (!presignedUrlService.isObjectExistsFromKey(profileImageKey)) {
            throw new InvalidImageKeyException(BusinessErrorMessage.IMAGE_NOT_FOUND.getMessage());
        }

        imageService.delete(ImageType.MEMBER_PROFILE, memberId);
        imageService.saveKey(ImageType.MEMBER_PROFILE, memberId, profileImageKey);
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
