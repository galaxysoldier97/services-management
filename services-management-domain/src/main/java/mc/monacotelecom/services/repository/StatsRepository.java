package mc.monacotelecom.services.repository;

import mc.monacotelecom.services.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StatsRepository extends JpaRepository<Service, Long> {
    @Query("SELECT status, count(*) FROM Service GROUP BY status")
    List<Object[]> statusOfServices();
}
