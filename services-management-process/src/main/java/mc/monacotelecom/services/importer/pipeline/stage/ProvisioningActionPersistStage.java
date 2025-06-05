package mc.monacotelecom.services.importer.pipeline.stage;

import lombok.EqualsAndHashCode;
import mc.monacotelecom.inventory.common.importer.process.pipeline.PersistStage;
import mc.monacotelecom.services.entity.ProvisioningAction;
import mc.monacotelecom.services.repository.ProvisioningActionRepository;
import org.springframework.stereotype.Component;

@Component
@EqualsAndHashCode(callSuper = true)
public class ProvisioningActionPersistStage extends PersistStage<ProvisioningAction> {

    private final ProvisioningActionRepository provisioningActionRepository;

    public ProvisioningActionPersistStage(final ProvisioningActionRepository provisioningActionRepository) {
        super();
        addAction(PersistActionType.SAVE, new PersistStageSaveAction((t, p) -> saveOrUpdate(t)));
        addAction(PersistActionType.UPDATE, new PersistStageUpdateAction<ProvisioningAction>((t, p)-> saveOrUpdate(t)));
        addAction(PersistActionType.DELETE, new PersistStageDeleteAction<ProvisioningAction>((t, p) -> delete(t)));
        this.provisioningActionRepository = provisioningActionRepository;
    }

    ProvisioningAction delete(ProvisioningAction t) {
        provisioningActionRepository.delete(t);
        return t;
    }

    ProvisioningAction saveOrUpdate(ProvisioningAction t) {
        return provisioningActionRepository.save(t);
    }
}
