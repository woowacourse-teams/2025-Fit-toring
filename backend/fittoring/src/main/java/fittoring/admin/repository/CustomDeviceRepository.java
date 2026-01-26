package fittoring.admin.repository;

import fittoring.admin.presentation.dto.AdminDeviceResponse;
import java.util.List;

public interface CustomDeviceRepository {

    List<Long> findDeviceIdsForAdmin(int page, int size);

    List<AdminDeviceResponse> findDevicesByIdsOrdered(List<Long> ids);
}
