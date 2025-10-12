package fittoring.admin.repository;

import fittoring.admin.presentation.dto.AdminMemberResponse;
import java.util.List;

public interface CustomMemberRepository {

    List<Long> findMemberIdsForAdmin(int page, int size);

    List<AdminMemberResponse> findMembersByIdsOrdered(List<Long> ids);
}
