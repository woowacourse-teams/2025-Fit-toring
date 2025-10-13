package fittoring.admin.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fittoring.admin.presentation.dto.AdminCertificateResponse;
import fittoring.domain.model.QCertificate;
import fittoring.domain.model.QMember;
import fittoring.domain.model.QMentoring;
import fittoring.domain.model.Status;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomCertificateRepositoryImpl implements CustomCertificateRepository {

    private static final QCertificate CERTIFICATE = QCertificate.certificate;
    private static final QMentoring MENTORING = QMentoring.mentoring;
    private static final QMember MEMBER = QMember.member;

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<AdminCertificateResponse> findAllWithFilterAndPagination(Status status, int page, int size) {
        long offset = (long) (page - 1) * size;

        return jpaQueryFactory.select(
            Projections.constructor(AdminCertificateResponse.class,
                CERTIFICATE.id, CERTIFICATE.mentoring.mentor.name, CERTIFICATE.title, CERTIFICATE.type, CERTIFICATE.verificationStatus, CERTIFICATE.createdAt)
            )
            .from(CERTIFICATE)
            .join(CERTIFICATE.mentoring, MENTORING)
            .join(MENTORING.mentor, MEMBER)
            .where(buildStatusFilterCondition(status))
            .orderBy(CERTIFICATE.createdAt.desc())
            .offset(offset)
            .limit(size)
            .fetch();
    }

    private BooleanExpression buildStatusFilterCondition(Status status) {
        if (status == null) {
            return null;
        }
        return CERTIFICATE.verificationStatus.eq(status);
    }

    @Override
    public long countByStatus(Status status) {
        return jpaQueryFactory
            .select(CERTIFICATE.count())
            .from(CERTIFICATE)
            .where(buildStatusFilterCondition(status))
            .fetchOne();
    }
}
