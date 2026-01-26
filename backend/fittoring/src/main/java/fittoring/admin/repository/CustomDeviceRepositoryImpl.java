package fittoring.admin.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fittoring.admin.presentation.dto.AdminDeviceResponse;
import fittoring.domain.model.QDevice;
import fittoring.domain.model.QMember;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomDeviceRepositoryImpl implements CustomDeviceRepository {

    private static final QDevice DEVICE = QDevice.device;
    private static final QMember MEMBER = QMember.member;

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Long> findDeviceIdsForAdmin(int page, int size) {
        long offset = (long) (page - 1) * size;

        return jpaQueryFactory.select(DEVICE.id)
                .from(DEVICE)
                .orderBy(DEVICE.id.desc())
                .offset(offset)
                .limit(size)
                .fetch();
    }

    @Override
    public List<AdminDeviceResponse> findDevicesByIdsOrdered(List<Long> ids) {
        return jpaQueryFactory.select(
                        Projections.constructor(
                                AdminDeviceResponse.class,
                                DEVICE.id, DEVICE.member.name, DEVICE.member.id, DEVICE.pushToken))
                .from(DEVICE)
                .join(DEVICE.member, MEMBER)
                .where(DEVICE.id.in(ids))
                .orderBy(DEVICE.id.desc())
                .fetch();
    }
}
