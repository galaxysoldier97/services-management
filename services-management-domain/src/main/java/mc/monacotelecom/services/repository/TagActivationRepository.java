package mc.monacotelecom.services.repository;

import mc.monacotelecom.services.entity.ActivationCode;
import mc.monacotelecom.services.entity.ProvisioningTag;
import mc.monacotelecom.services.entity.TagActivation;
import mc.monacotelecom.services.entity.TagActivationKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface TagActivationRepository extends JpaRepository<TagActivation, TagActivationKey>, JpaSpecificationExecutor<TagActivation> {
    List<TagActivation> findByProvisioningTag(ProvisioningTag tag);
    void deleteByProvisioningTagAndActivationCode(ProvisioningTag tagCode, ActivationCode actvCode);
    Optional<TagActivation> findByTagCodeAndActvCode(String tagCode, String actvCode);
}