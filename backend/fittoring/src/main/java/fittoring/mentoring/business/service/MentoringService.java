package fittoring.mentoring.business.service;

import fittoring.config.auth.LoginInfo;
import fittoring.mentoring.Cursor;
import fittoring.mentoring.business.exception.BusinessErrorMessage;
import fittoring.mentoring.business.exception.CategoryNotFoundException;
import fittoring.mentoring.business.exception.ForbiddenException;
import fittoring.mentoring.business.exception.ImageNotFoundException;
import fittoring.mentoring.business.exception.MemberNotFoundException;
import fittoring.mentoring.business.exception.MentoringAlreadyExistException;
import fittoring.mentoring.business.exception.MentoringNotFoundException;
import fittoring.mentoring.business.model.Category;
import fittoring.mentoring.business.model.CategoryMentoring;
import fittoring.mentoring.business.model.Certificate;
import fittoring.mentoring.business.model.Image;
import fittoring.mentoring.business.model.ImageType;
import fittoring.mentoring.business.model.Member;
import fittoring.mentoring.business.model.MemberRole;
import fittoring.mentoring.business.model.Mentoring;
import fittoring.mentoring.business.model.MentoringStatistics;
import fittoring.mentoring.business.model.Reservation;
import fittoring.mentoring.business.model.SortKey;
import fittoring.mentoring.business.model.Status;
import fittoring.mentoring.business.repository.CategoryMentoringRepository;
import fittoring.mentoring.business.repository.CategoryRepository;
import fittoring.mentoring.business.repository.CertificateRepository;
import fittoring.mentoring.business.repository.MemberRepository;
import fittoring.mentoring.business.repository.MentoringRepository;
import fittoring.mentoring.business.repository.MentoringStatisticsRepository;
import fittoring.mentoring.business.repository.ReservationRepository;
import fittoring.mentoring.business.repository.ReviewRepository;
import fittoring.mentoring.business.service.dto.MentoringPaginationResult;
import fittoring.mentoring.business.service.dto.MentoringSummaryPaginationResponse;
import fittoring.mentoring.business.service.dto.ModifyMentoringDto;
import fittoring.mentoring.business.service.dto.RatingStatsDto;
import fittoring.mentoring.business.service.dto.RegisterMentoringDto;
import fittoring.mentoring.presentation.dto.CertificateSpecAndImageResponse;
import fittoring.mentoring.presentation.dto.MentoringResponse;
import fittoring.mentoring.presentation.dto.MentoringSummaryResponse;
import fittoring.util.CursorCodec;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MentoringService {

    private final ImageService imageService;
    private final CertificateService certificateService;

    private final MentoringRepository mentoringRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryMentoringRepository categoryMentoringRepository;
    private final MemberRepository memberRepository;
    private final CertificateRepository certificateRepository;
    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;
    private final MentoringStatisticsRepository mentoringStatisticsRepository;

    @Transactional
    public void registerMentoring(RegisterMentoringDto dto) {
        Member member = getMemberById(dto.mentorId());
        validateAlreadyRegistered(member);
        final Mentoring mentoring = new Mentoring(
                member,
                dto.price(),
                dto.career(),
                dto.content(),
                dto.introduction(),
                dto.chatUrl()
        );
        final Mentoring savedMentoring = mentoringRepository.save(mentoring);
        MentoringStatistics mentoringStatistics = MentoringStatistics.defaultOf(mentoring);
        mentoringStatisticsRepository.save(mentoringStatistics);

        List<String> categoryTitles = dto.category();
        mapCategoriesToMentoring(categoryTitles, savedMentoring);

        saveProfileImage(dto.profileImageUrl(), savedMentoring);

        certificateService.mapCertificatesToMentoring(dto.certificateInfos(), savedMentoring);
        member.registerAsMentor();
    }

    private Member getMemberById(Long mentorId) {
        return memberRepository.findById(mentorId)
                .orElseThrow(() -> new MemberNotFoundException(BusinessErrorMessage.MEMBER_NOT_FOUND.getMessage()));
    }

    private void validateAlreadyRegistered(Member member) {
        if (mentoringRepository.existsByMentor(member)) {
            throw new MentoringAlreadyExistException(BusinessErrorMessage.MENTORING_ALREADY_EXIST.getMessage());
        }
    }

    private void mapCategoriesToMentoring(List<String> categoryTitles, Mentoring savedMentoring) {
        for (String categoryTitle : categoryTitles) {
            Category category = categoryRepository.findByTitle(categoryTitle)
                    .orElseThrow(
                            () -> new CategoryNotFoundException(BusinessErrorMessage.CATEGORY_NOT_FOUND.getMessage()));
            CategoryMentoring categoryMentoring = new CategoryMentoring(category, savedMentoring);
            categoryMentoringRepository.save(categoryMentoring);
        }
    }

    private void saveProfileImage(String profileImageUrl, Mentoring mentoring) {
        if (profileImageUrl == null) {
            return;
        }
        // todo: S3 업로드 유효성 검증 추가
        imageService.deleteByImageTypeAndRelationId(ImageType.MENTORING_PROFILE, mentoring.getId());
        imageService.save(ImageType.MENTORING_PROFILE, mentoring.getId(), profileImageUrl);
    }

    @Transactional(readOnly = true)
    public MentoringResponse getMentoringWithRelationsByMentorId(Long mentorId) {
        Mentoring mentoring = mentoringRepository.findByMentorId(mentorId)
                .orElseThrow(
                        () -> new MentoringNotFoundException(BusinessErrorMessage.MENTORING_NOT_FOUND.getMessage()));
        return getMentoringWithRelations(mentoring);
    }

    @Transactional(readOnly = true)
    public MentoringResponse getMentoringWithRelationsById(final Long mentoringId) {
        Mentoring mentoring = getMentoringById(mentoringId);
        return getMentoringWithRelations(mentoring);
    }

    private Mentoring getMentoringById(Long mentoringId) {
        return mentoringRepository.findById(mentoringId)
                .orElseThrow(
                        () -> new MentoringNotFoundException(BusinessErrorMessage.MENTORING_NOT_FOUND.getMessage()));
    }

    private MentoringResponse getMentoringWithRelations(Mentoring mentoring) {
        List<String> categoryTitles = categoryMentoringRepository.findTitlesByMentoringId(mentoring.getId());
        RatingStatsDto ratingStatsDto = getRatingStatsDto(mentoring.getId());

        List<CertificateSpecAndImageResponse> certificateDetails = getApprovedCertificates(mentoring);
        Image image = getMentoringProfileImageOrNull(mentoring);
        return MentoringResponse.of(
                mentoring,
                categoryTitles,
                image,
                certificateDetails,
                ratingStatsDto.average(),
                ratingStatsDto.count()
        );
    }

    private RatingStatsDto getRatingStatsDto(Long mentoringId) {
        return reviewRepository.findRatingStatsByMentoringId(mentoringId)
                .orElseGet(() -> RatingStatsDto.defaultOf(mentoringId));
    }

    private List<CertificateSpecAndImageResponse> getApprovedCertificates(Mentoring mentoring) {
        List<Certificate> certificates = certificateRepository.findByMentoringIdAndVerificationStatus(
                mentoring.getId(),
                Status.APPROVED
        );

        List<Long> certificateIds = certificates.stream()
                .map(Certificate::getId)
                .toList();

        List<Image> certificateImages = imageService.findByRelationIdsAndImageType(
                certificateIds,
                ImageType.CERTIFICATE
        );

        return buildResponsesWithImages(certificateImages, certificates);
    }

    private List<CertificateSpecAndImageResponse> buildResponsesWithImages(
            List<Image> certificateImages,
            List<Certificate> certificates
    ) {
        // (certificateId, Image객체) 형태의 Map 생성
        Map<Long, Image> certificateIdToImageMap = certificateImages.stream()
                .collect(Collectors.toMap(Image::getRelationId, Function.identity()));

        // certificates를 돌면서 이미지가 존재하는 경우에만 response에 추가함
        List<CertificateSpecAndImageResponse> response = new ArrayList<>();
        for (Certificate certificate : certificates) {
            Image image = certificateIdToImageMap.get(certificate.getId());
            if (image != null) {
                response.add(CertificateSpecAndImageResponse.of(certificate, image.getUrl()));
            }
        }

        return response;
    }

    private Image getMentoringProfileImageOrNull(Mentoring mentoring) {
        return imageService.findByImageTypeAndRelationId(ImageType.MENTORING_PROFILE, mentoring.getId())
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<MentoringSummaryResponse> findMentoringSummaries(
            String categoryTitle1,
            String categoryTitle2,
            String categoryTitle3
    ) {
        List<Mentoring> mentorings = findMentorings(categoryTitle1, categoryTitle2, categoryTitle3);
        List<Long> mentoringIds = createMentoringIdsByMentoring(mentorings);

        List<RatingStatsDto> ratingStatsDtos = reviewRepository.findReviewStatsByMentoringIds(mentoringIds);
        Map<Long, RatingStatsDto> ratingStatsDtoMap = createReviewStatsMap(ratingStatsDtos);
        return mentorings.stream()
                .map(mentoring -> {
                            Image profileImage = getProfileImageOrNull(mentoring.getId());
                            List<String> categoryTitles = getCategoryMentoringTitlesByMentoringId(mentoring);
                            RatingStatsDto ratingStatsDto = getReviewStats(mentoring, ratingStatsDtoMap);
                            return MentoringSummaryResponse.of(
                                    mentoring,
                                    categoryTitles,
                                    profileImage,
                                    ratingStatsDto
                            );
                        }
                )
                .toList();
    }

    private List<Mentoring> findMentorings(
            String categoryTitle1,
            String categoryTitle2,
            String categoryTitle3
    ) {
        if (isNoCategoryFilter(categoryTitle1, categoryTitle2, categoryTitle3)) {
            return mentoringRepository.findAll();
        }
        validateAllCategoryTitle(categoryTitle1, categoryTitle2, categoryTitle3);
        return mentoringRepository.findAllMentoringWithFilter(
                categoryTitle1,
                categoryTitle2,
                categoryTitle3
        );
    }

    private List<Long> createMentoringIdsByMentoring(List<Mentoring> mentorings) {
        return mentorings.stream()
                .map(Mentoring::getId)
                .toList();
    }

    private Map<Long, RatingStatsDto> createReviewStatsMap(List<RatingStatsDto> ratingStatsDto) {
        return ratingStatsDto.stream()
                .collect(Collectors.toMap(RatingStatsDto::mentoringId, Function.identity()));
    }

    private boolean isNoCategoryFilter(String categoryTitle1, String categoryTitle2, String categoryTitle3) {
        return categoryTitle1 == null
                && categoryTitle2 == null
                && categoryTitle3 == null;
    }

    private void validateAllCategoryTitle(String categoryTitle1, String categoryTitle2, String categoryTitle3) {
        validateExistCategoryTitle(categoryTitle1);
        validateExistCategoryTitle(categoryTitle2);
        validateExistCategoryTitle(categoryTitle3);
    }

    private void validateExistCategoryTitle(String categoryTitle) {
        if (categoryTitle != null && !categoryRepository.existsByTitle(categoryTitle)) {
            throw new CategoryNotFoundException(BusinessErrorMessage.CATEGORY_NOT_FOUND.getMessage());
        }
    }

    private Image getProfileImageOrNull(Long mentoringId) {
        return imageService.findByImageTypeAndRelationId(
                ImageType.MENTORING_PROFILE,
                mentoringId
        ).orElse(null);
    }

    private List<String> getCategoryMentoringTitlesByMentoringId(Mentoring mentoring) {
        return categoryMentoringRepository.findTitlesByMentoringId(
                mentoring.getId());
    }

    private RatingStatsDto getReviewStats(Mentoring mentoring, Map<Long, RatingStatsDto> longRatingStatsDtoMap) {
        return longRatingStatsDtoMap.getOrDefault(
                mentoring.getId(),
                RatingStatsDto.defaultOf(mentoring.getId())
        );
    }

    @Transactional
    public void modifyMentoring(ModifyMentoringDto dto) {
        Mentoring mentoring = findMentoringOwnedByMentor(dto.mentoringId(), dto.mentorId());
        categoryMentoringRepository.deleteByMentoringId(mentoring.getId());
        mapCategoriesToMentoring(dto.category(), mentoring);
        fetchProfileImage(dto, mentoring);
        certificateService.mapCertificatesToMentoring(dto.certificateInfos(), mentoring);
        mentoring.modify(dto.price(), dto.career(), dto.content(), dto.introduction(), dto.chatUrl());
    }

    private void fetchProfileImage(ModifyMentoringDto dto, Mentoring mentoring) {
        if (dto.profileImageFile() != null) {
            // 프로필 이미지 새 업로드 →profileImageUrl: null,image: 변경할 파일
            saveProfileImage(dto.profileImageFile(), mentoring);
        } else if (dto.profileImageUrl() == null) {
            // 프로필 이미지 삭제 →profileImageUrl: null, image: null
            imageService.deleteByImageTypeAndRelationId(ImageType.MENTORING_PROFILE, mentoring.getId());
        } else {
            // 프로필 이미지 변경 없음 →profileImageUrl: "기존 url값"
            validateProfileImageUrlMatches(mentoring.getId(), dto.profileImageUrl());
        }
    }

    private void validateProfileImageUrlMatches(Long mentoringId, String profileImageUrl) {
        if (imageService.findByImageTypeAndRelationId(ImageType.MENTORING_PROFILE, mentoringId)
                .orElseThrow(() -> new ImageNotFoundException(BusinessErrorMessage.IMAGE_NOT_FOUND.getMessage()))
                .getUrl()
                .equals(profileImageUrl)) {
            return;
        }
        throw new ForbiddenException(BusinessErrorMessage.FORBIDDEN_URL.getMessage());
    }

    private Mentoring findMentoringOwnedByMentor(Long mentoringId, Long mentorId) {
        Mentoring mentoring = mentoringRepository.findById(mentoringId)
                .orElseThrow(
                        () -> new MentoringNotFoundException(BusinessErrorMessage.MENTORING_NOT_FOUND.getMessage()));
        validateMentorMatches(mentoring, mentorId);
        return mentoring;
    }

    private void validateMentorMatches(Mentoring mentoring, Long mentorId) {
        if (mentoring.isCreatedByMember(mentorId)) {
            return;
        }
        throw new ForbiddenException(BusinessErrorMessage.MENTOR_NOT_SAME.getMessage());
    }

    @Transactional
    public void deleteMentoringByAdmin(LoginInfo loginInfo, Long mentoringId) {
        checkAdminAuthority(loginInfo.memberId());
        Mentoring mentoring = getMentoringById(mentoringId);
        List<Reservation> allReservationByMentoring = reservationRepository.findAllByMentoring(mentoring);
        for (Reservation reservation : allReservationByMentoring) {
            reviewRepository.deleteByReservation(reservation);
        }
        reservationRepository.deleteAll(allReservationByMentoring);
        categoryMentoringRepository.deleteByMentoringId(mentoring.getId());
        certificateRepository.deleteAllByMentoring(mentoring);
        mentoringStatisticsRepository.deleteById(mentoring.getId());
        mentoringRepository.delete(mentoring);
    }

    private void checkAdminAuthority(Long memberId) {
        Member member = getMemberById(memberId);
        if (MemberRole.isNotAdmin(member.getRole())) {
            throw new ForbiddenException(BusinessErrorMessage.FORBIDDEN_MEMBER.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public MentoringSummaryPaginationResponse findMentoringSummaryPages(
            SortKey sortKey,
            String cursorCode,
            List<Long> categoryIds
    ) {
        Cursor cursor = CursorCodec.decode(cursorCode);
        MentoringPaginationResult mentoringPaginationResult = mentoringRepository.findMentoringsWithPagination(sortKey,
                cursor, categoryIds);

        List<Mentoring> mentorings = mentoringPaginationResult.mentorings();

        List<Long> mentoringIds = createMentoringIdsByMentoring(mentorings);

        List<RatingStatsDto> ratingStatsDtos = reviewRepository.findReviewStatsByMentoringIds(mentoringIds);

        Map<Long, RatingStatsDto> ratingStatsDtoMap = createReviewStatsMap(ratingStatsDtos);
        List<MentoringSummaryResponse> mentoringSummaryResponses = mentorings.stream()
                .map(mentoring -> {
                            Image profileImage = getProfileImageOrNull(mentoring.getId());
                            List<String> categoryTitles = getCategoryMentoringTitlesByMentoringId(mentoring);
                            RatingStatsDto ratingStatsDto = getReviewStats(mentoring, ratingStatsDtoMap);
                            return MentoringSummaryResponse.of(
                                    mentoring,
                                    categoryTitles,
                                    profileImage,
                                    ratingStatsDto
                            );
                        }
                )
                .toList();

        return new MentoringSummaryPaginationResponse(
                mentoringSummaryResponses,
                mentoringPaginationResult.nextCursorCode(),
                mentoringPaginationResult.hasNext()
        );
    }
}
