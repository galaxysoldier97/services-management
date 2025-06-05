package mc.monacotelecom.services.repository;

import mc.monacotelecom.services.entity.ActivationCode;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ActivationCodeRepository extends JpaRepository<ActivationCode, String>, JpaSpecificationExecutor<ActivationCode> {

    @Override
    @EntityGraph(value = "ActivationCode.tagActivations")
    Optional<ActivationCode> findById(String activCode);

    Optional<ActivationCode> findByInternalId(Long internalId);

    @Query(value = "SELECT sa.activCode FROM ServiceActivation sa LEFT JOIN sa.activationCode ac WHERE ac is null")
    List<String> findMissingCodes();
}
