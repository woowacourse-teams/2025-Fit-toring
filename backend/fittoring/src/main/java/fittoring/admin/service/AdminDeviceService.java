package fittoring.admin.service;

import fittoring.admin.presentation.dto.AdminDeviceResponse;
import fittoring.admin.presentation.dto.PageResult;
import fittoring.application.notification.repository.DeviceRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AdminDeviceService {

    private final DeviceRepository deviceRepository;

    @Transactional(readOnly = true)
    public PageResult<AdminDeviceResponse> getAllDevicesPaged(int page, int size) {
        List<Long> ids = deviceRepository.findDeviceIdsForAdmin(page, size);
        List<AdminDeviceResponse> devices = deviceRepository.findDevicesByIdsOrdered(ids);
        long total = deviceRepository.count();
        int totalPages = (int) Math.max(1, (total + size - 1) / size);
        return new PageResult<>(devices, page, devices.size(), total, totalPages);
    }
}
