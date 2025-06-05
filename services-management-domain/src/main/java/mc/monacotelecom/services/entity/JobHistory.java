package mc.monacotelecom.services.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import mc.monacotelecom.inventory.common.recycling.entities.BaseJobHistory;
import mc.monacotelecom.inventory.common.recycling.enums.JobHistoryStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

import static mc.monacotelecom.services.entity.Constants.STATUS;

@Entity
@EqualsAndHashCode(callSuper = false)
@Getter
@Setter
@Table(name = "job_history")
public class JobHistory extends BaseJobHistory {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "start", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime start;

    @Column(name = "end", columnDefinition = "TIMESTAMP")
    private LocalDateTime end;

    @Column(name = STATUS, nullable = false, columnDefinition = "ENUM('STARTED', 'COMPLETED', 'FAILED')")
    @Enumerated(EnumType.STRING)
    private JobHistoryStatus status;
}
