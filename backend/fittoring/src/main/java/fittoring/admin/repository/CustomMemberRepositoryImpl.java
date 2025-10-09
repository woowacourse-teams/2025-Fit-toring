package fittoring.admin.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fittoring.admin.presentation.dto.AdminMemberResponse;
import fittoring.domain.model.QMember;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomMemberRepositoryImpl implements CustomMemberRepository {

    private static final QMember member = QMember.member;

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Long> findMemberIdsForAdmin(int page, int size) {
        long offset = (long) (page - 1) * size;

        return jpaQueryFactory.select(member.id)
                .from(member)
                .orderBy(member.id.desc())
                .offset(offset)
                .limit(size)
                .fetch();
    }

    @Override
    public List<AdminMemberResponse> findMembersByIdsOrdered(List<Long> ids) {
        return jpaQueryFactory.select(
                        Projections.constructor(AdminMemberResponse.class,
                                member.name, member.loginId, member.gender, member.phone.number, member.role)
                ).from(member)
                .where(member.id.in(ids))
                .orderBy(member.id.desc())
                .fetch();
    }
}
