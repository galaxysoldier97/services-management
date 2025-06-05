package mc.monacotelecom.services.repository;

import mc.monacotelecom.services.entity.ProvisioningTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ProvisioningTagRepository extends JpaRepository<ProvisioningTag, String>, JpaSpecificationExecutor<ProvisioningTag> {
    List<ProvisioningTag> findByTagCodeIn(Collection<String> tagCodes);

    Optional<ProvisioningTag> findByInternalId(Long internalId);
    @Query(value = "SELECT st.tagCode FROM ServiceTag st LEFT JOIN st.provisioningTag pt WHERE pt is null")
    List<String> findMissingTags();
}
