package fittoring.admin.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fittoring.admin.presentation.dto.AdminReservationResponse;
import fittoring.admin.presentation.dto.PageResult;
import fittoring.domain.model.QMember;
import fittoring.domain.model.QMentoring;
import fittoring.domain.model.QReservation;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomReservationRepositoryImpl implements CustomReservationRepository {

    private static final QMentoring MENTORING = QMentoring.mentoring;
    private static final QReservation RESERVATION = QReservation.reservation;
    private static final QMember MENTEE = QMember.member;

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public PageResult<AdminReservationResponse> findReservationsForAdmin(int page, int size) {
        int limitPlusOne = size + 1;
        int offset = Math.max(0, (page - 1) * size);

        List<AdminReservationResponse> body = jpaQueryFactory.select(Projections.constructor(
                        AdminReservationResponse.class,
                        RESERVATION.id,
                        MENTEE.name,
                        RESERVATION.createdAt,
                        RESERVATION.status.stringValue(),
                        RESERVATION.content
                ))
                .from(RESERVATION)
                .join(RESERVATION.mentoring, MENTORING)
                .join(RESERVATION.mentee, MENTEE)
                .orderBy(RESERVATION.createdAt.desc(), RESERVATION.id.desc())
                .offset(offset)
                .limit(limitPlusOne)
                .fetch();

        boolean hasNext = body.size() > size;
        if (hasNext) {
            body = body.subList(0, size);
        }

        return new PageResult<>(body, page, size, hasNext);
    }
}
