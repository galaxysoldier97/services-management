package mc.monacotelecom.services.process;

import lombok.RequiredArgsConstructor;
import mc.monacotelecom.services.dto.ServicesStatsDto;
import mc.monacotelecom.services.enums.Status;
import mc.monacotelecom.services.repository.StatsRepository;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class StatsProcess {
    private final StatsRepository statsRepository;

    public ServicesStatsDto resourcesDashboard() {
        ServicesStatsDto stats = new ServicesStatsDto();
        Map<Status, Long> statusOfServices = new EnumMap<>(Status.class);

        statsRepository.statusOfServices().forEach(line -> statusOfServices.put(Status.valueOf(line[0].toString()), Long.valueOf(line[1].toString())));

        stats.setStatusOfServices(statusOfServices);
        return stats;
    }
}
