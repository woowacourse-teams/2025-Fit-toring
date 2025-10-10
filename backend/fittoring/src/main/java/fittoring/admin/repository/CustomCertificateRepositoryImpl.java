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

    private static final QCertificate certificate = QCertificate.certificate;
    private static final QMentoring mentoring = QMentoring.mentoring;
    private static final QMember member = QMember.member;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<AdminCertificateResponse> findAllWithFilterAndPagination(Status status, int page, int size) {
        long offset = (long) (page - 1) * size;

        return jpaQueryFactory.select(Projections.constructor(AdminCertificateResponse.class,
                certificate.id, certificate.mentoring.mentor.name, certificate.title, certificate.type, certificate.verificationStatus, certificate.createdAt)
            )
            .from(certificate)
            .join(certificate.mentoring, mentoring)
            .join(mentoring.mentor, member)
            .where(buildStatusFilterCondition(status))
            .orderBy(certificate.createdAt.desc())
            .offset(offset)
            .limit(size)
            .fetch();
    }

    private BooleanExpression buildStatusFilterCondition(Status status) {
        if (status == null) {
            return null;
        }
        return certificate.verificationStatus.eq(status);
    }

    @Override
    public long countByStatus(Status status) {
        return jpaQueryFactory
            .select(certificate.count())
            .from(certificate)
            .where(buildStatusFilterCondition(status))
            .fetchOne();
    }
}
