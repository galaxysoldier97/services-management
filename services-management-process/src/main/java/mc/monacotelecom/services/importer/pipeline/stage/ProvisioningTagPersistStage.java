package mc.monacotelecom.services.importer.pipeline.stage;

import lombok.EqualsAndHashCode;
import mc.monacotelecom.inventory.common.importer.process.pipeline.PersistStage;
import mc.monacotelecom.services.entity.ProvisioningTag;
import mc.monacotelecom.services.repository.ProvisioningTagRepository;
import org.springframework.stereotype.Component;

@Component
@EqualsAndHashCode(callSuper = true)
public class ProvisioningTagPersistStage extends PersistStage<ProvisioningTag> {

    private final ProvisioningTagRepository provisioningTagRepository;

    public ProvisioningTagPersistStage(final ProvisioningTagRepository provisioningTagRepository) {
        super();
        addAction(PersistActionType.SAVE, new PersistStageSaveAction((t, p) -> saveOrUpdate(t)));
        addAction(PersistActionType.UPDATE, new PersistStageUpdateAction<ProvisioningTag>((t, p)-> saveOrUpdate(t)));
        addAction(PersistActionType.DELETE, new PersistStageDeleteAction<ProvisioningTag>((t, p) -> delete(t)));
        this.provisioningTagRepository = provisioningTagRepository;
    }

    ProvisioningTag delete(ProvisioningTag t) {
        provisioningTagRepository.delete(t);
        return t;
    }

    ProvisioningTag saveOrUpdate(ProvisioningTag t) {
        return provisioningTagRepository.save(t);
    }
}
