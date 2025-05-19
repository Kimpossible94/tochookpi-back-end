package com.tochookpi.tochookpi.service.storage;

import com.tochookpi.tochookpi.entity.OrphanFileLogEntity;
import com.tochookpi.tochookpi.enums.OrphanFileStatus;
import com.tochookpi.tochookpi.repository.OrphanFileLogRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class OrphanFileLogServiceImpl implements OrphanFileLogService {

    private final OrphanFileLogRepository orphanFileLogRepository;

    @Override
    public void saveLog(String fileUrl, String reason) {
        OrphanFileLogEntity orphanFileLogEntity = OrphanFileLogEntity.builder()
                .fileUrl(fileUrl)
                .reason(reason)
                .status(OrphanFileStatus.PENDING)
                .retryCount(0)
                .build();

        orphanFileLogRepository.save(orphanFileLogEntity);
    }
}
