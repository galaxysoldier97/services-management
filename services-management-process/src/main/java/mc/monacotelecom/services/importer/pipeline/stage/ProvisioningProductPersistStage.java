package mc.monacotelecom.services.importer.pipeline.stage;

import lombok.EqualsAndHashCode;
import mc.monacotelecom.inventory.common.importer.process.pipeline.PersistStage;
import mc.monacotelecom.services.entity.ProvisioningProduct;
import mc.monacotelecom.services.repository.ProvisioningProductRepository;
import org.springframework.stereotype.Component;

@Component
@EqualsAndHashCode(callSuper = true)
public class ProvisioningProductPersistStage extends PersistStage<ProvisioningProduct> {

    private final ProvisioningProductRepository provisioningProductRepository;

    public ProvisioningProductPersistStage(final ProvisioningProductRepository provisioningProductRepository) {
        super();
        addAction(PersistActionType.SAVE, new PersistStageSaveAction((t, p) -> saveOrUpdate(t)));
        addAction(PersistActionType.UPDATE, new PersistStageUpdateAction<ProvisioningProduct>((t, p)-> saveOrUpdate(t)));
        addAction(PersistActionType.DELETE, new PersistStageDeleteAction<ProvisioningProduct>((t, p) -> delete(t)));
        this.provisioningProductRepository = provisioningProductRepository;
    }

    ProvisioningProduct delete(ProvisioningProduct t) {
        provisioningProductRepository.delete(t);
        return t;
    }

    ProvisioningProduct saveOrUpdate(ProvisioningProduct t) {
        return provisioningProductRepository.save(t);
    }
}