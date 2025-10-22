package fittoring.application.reservation.service;

import fittoring.admin.presentation.dto.AdminReservationDeleteDto;
import fittoring.admin.service.dto.AdminReservationStatusUpdateDto;
import fittoring.application.chat.service.ChatRoomService;
import fittoring.application.chat.service.dto.ChatRoomCreatedInfo;
import fittoring.application.exception.BusinessErrorMessage;
import fittoring.application.exception.ForbiddenException;
import fittoring.application.exception.MentorAndMenteeIsSameException;
import fittoring.application.exception.MentoringNotFoundException;
import fittoring.application.exception.NotFoundMemberException;
import fittoring.application.exception.ReservationNotFoundException;
import fittoring.application.image.service.ImageService;
import fittoring.application.member.repository.MemberRepository;
import fittoring.application.mentoring.repository.MentoringRepository;
import fittoring.application.mentoring.repository.MentoringStatisticsRepository;
import fittoring.application.mentoring.service.dto.MentorMentoringReservationResponse;
import fittoring.application.mentoring.service.dto.ReservationInfo;
import fittoring.application.reservation.presentation.dto.response.ParticipatedReservationResponse;
import fittoring.application.reservation.presentation.dto.response.PhoneNumberResponse;
import fittoring.application.reservation.repository.ReservationRepository;
import fittoring.application.reservation.service.dto.ParticipatedReservationWithoutProfileImageDto;
import fittoring.application.reservation.service.dto.ReservationCreateDto;
import fittoring.application.review.repository.ReviewRepository;
import fittoring.domain.model.ChatRoom;
import fittoring.domain.model.ImageType;
import fittoring.domain.model.Member;
import fittoring.domain.model.MemberRole;
import fittoring.domain.model.Mentoring;
import fittoring.domain.model.Reservation;
import fittoring.domain.model.Status;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReservationService {

    private final ChatRoomService chatRoomService;
    private final MentoringRepository mentoringRepository;
    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;
    private final MentoringStatisticsRepository mentoringStatisticsRepository;
    private final ImageService imageService;

    @Transactional
    public Reservation createReservation(ReservationCreateDto dto) {
        Reservation reservation = createReservationEntity(dto);
        Reservation savedReservation = reservationRepository.save(reservation);
        mentoringStatisticsRepository.updateReservationCountPlus(dto.mentoringId());
        return savedReservation;
    }

    private Reservation createReservationEntity(ReservationCreateDto dto) {
        Mentoring mentoring = getMentoring(dto.mentoringId());
        validateNotMyMentoring(mentoring, dto.menteeId());
        Member mentee = getMember(dto.menteeId());
        return new Reservation(
                dto.content(),
                Status.PENDING,
                mentoring,
                mentee
        );
    }

    private Mentoring getMentoring(Long mentoringId) {
        return mentoringRepository.findById(mentoringId)
                .orElseThrow(
                        () -> new MentoringNotFoundException(BusinessErrorMessage.MENTORING_NOT_FOUND.getMessage()));
    }

    private void validateNotMyMentoring(Mentoring mentoring, Long menteeId) {
        if (mentoring.isCreatedByMember(menteeId)) {
            throw new MentorAndMenteeIsSameException(BusinessErrorMessage.MENTOR_AND_MENTEE_IS_SAME.getMessage());
        }
    }

    private Member getMember(Long menteeId) {
        return memberRepository.findById(menteeId)
                .orElseThrow(
                        () -> new NotFoundMemberException(BusinessErrorMessage.MEMBER_NOT_FOUND.getMessage()));
    }

    @Transactional(readOnly = true)
    public List<MentorMentoringReservationResponse> getReservationsByMentor(Long mentorId) {
        List<Reservation> reservations = reservationRepository.findAllByMentorId(mentorId);
        return getMentorMentoringReservationResponses(reservations);
    }

    private List<MentorMentoringReservationResponse> getMentorMentoringReservationResponses(
            List<Reservation> reservations
    ) {
        List<Long> reservationIds = reservations.stream()
            .map(Reservation::getId)
            .toList();
        Map<Long, ChatRoom> chatRoomMap = chatRoomService.findAllByReservationIds(reservationIds);

        List<MentorMentoringReservationResponse> result = new ArrayList<>();
        for (Reservation reservation : reservations) {
            if (reservation.isAccessibleForChatRoom()) {
                result.add(MentorMentoringReservationResponse.of(reservation, chatRoomMap.get(reservation.getId())));
                continue;
            }
            result.add(MentorMentoringReservationResponse.of(reservation));
        }
        return result;
    }

    @Transactional(readOnly = true)
    public PhoneNumberResponse getPhone(Long reservationId) {
        Reservation reservation = getReservation(reservationId);
        return new PhoneNumberResponse(reservation.getMenteePhone());
    }

    @Transactional(readOnly = true)
    public List<ParticipatedReservationResponse> findMemberReservations(Long memberId) {
        List<ParticipatedReservationWithoutProfileImageDto> rows = reservationRepository.findMemberReservationDtos(
                memberId
        );

        Set<Long> mentoringIds = rows.stream()
                .map(ParticipatedReservationWithoutProfileImageDto::getMentoringId)
                .collect(Collectors.toSet());
        Map<Long, String> profileImageByMentoring = imageService.findMentoringThumbnailMapByImageTypeAndRelationIds(
                ImageType.MENTORING_PROFILE,
                mentoringIds
        );
        
        List<Long> reservationIds = rows.stream()
            .map(ParticipatedReservationWithoutProfileImageDto::getReservationId)
            .toList();
        Map<Long, ChatRoom> chatRoomMap = chatRoomService.findAllByReservationIds(reservationIds);
        
        List<ParticipatedReservationResponse> result = new ArrayList<>();
        for (ParticipatedReservationWithoutProfileImageDto participatedReservation : rows) {
            Long chatRoomId = isChattableStatus(participatedReservation.getStatus())
                ? chatRoomMap.get(participatedReservation.getReservationId()).getId()
                : null;
            result.add(new ParticipatedReservationResponse(
                participatedReservation.getReservationId(),
                participatedReservation.getMentoringId(),
                participatedReservation.getMentorName(),
                profileImageByMentoring.get(participatedReservation.getMentoringId()),
                participatedReservation.getReservedAt(),
                participatedReservation.getContent(),
                participatedReservation.getStatus(),
                chatRoomId,
                participatedReservation.getIsReviewed()));
        }
        return result;
    }

    private boolean isChattableStatus(String statusName) {
        return statusName.equals(Status.APPROVED.name())
            || statusName.equals(Status.COMPLETE.name());
    }

    private void checkAdminAuthority(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundMemberException(BusinessErrorMessage.MEMBER_NOT_FOUND.getMessage()));
        if (MemberRole.isNotAdmin(member.getRole())) {
            throw new ForbiddenException(BusinessErrorMessage.FORBIDDEN_MEMBER.getMessage());
        }
    }

    @Transactional
    public ReservationInfo updateStatus(Long reservationId, String updateStatus) {
        Reservation reservation = getReservation(reservationId);
        Status status = Status.of(updateStatus);
        reservation.changeStatus(status);

        String url = "";
        if (reservation.isApprove()) {
            ChatRoomCreatedInfo chatRoomCreatedInfo = chatRoomService.registerChatRoom(reservation);
            url = chatRoomCreatedInfo.url();
        }

        return new ReservationInfo(reservation, url);
    }

    private Reservation getReservation(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(
                        () -> new ReservationNotFoundException(
                                BusinessErrorMessage.RESERVATION_NOT_FOUND.getMessage())
                );
    }

    @Transactional
    public void updateStatusWithAdminAuthorization(AdminReservationStatusUpdateDto adminReservationStatusUpdateDto) {
        checkAdminAuthority(adminReservationStatusUpdateDto.memberId());
        Reservation reservation = getReservation(adminReservationStatusUpdateDto.reservationId());
        Status status = Status.of(adminReservationStatusUpdateDto.status());
        reservation.changeStatusWithoutValidation(status);
    }

    @Transactional
    public void deleteReservationWithAdminAuthorization(AdminReservationDeleteDto adminReservationDeleteDto) {
        checkAdminAuthority(adminReservationDeleteDto.memberId());
        Reservation reservation = getReservation(adminReservationDeleteDto.reservationId());
        reviewRepository.deleteByReservation(reservation);
        mentoringStatisticsRepository.updateReservationCountMinus(reservation.getMentoring().getId());
        reservationRepository.delete(reservation);
    }
}
