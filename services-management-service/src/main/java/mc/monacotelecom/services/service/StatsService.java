package mc.monacotelecom.services.service;

import lombok.RequiredArgsConstructor;
import mc.monacotelecom.services.dto.ServicesStatsDto;
import mc.monacotelecom.services.process.StatsProcess;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class StatsService {

    private final StatsProcess statsProcess;

    @Transactional(readOnly = true)
    public ServicesStatsDto resourcesDashboard() {
        return statsProcess.resourcesDashboard();
    }
}
