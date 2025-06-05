package mc.monacotelecom.services.importer.pipeline;

import lombok.extern.slf4j.Slf4j;
import mc.monacotelecom.inventory.common.importer.domain.entity.IEntity;
import mc.monacotelecom.inventory.common.importer.process.ImporterResolver;
import mc.monacotelecom.inventory.common.importer.process.pipeline.PersistStage;
import mc.monacotelecom.inventory.common.importer.process.pipeline.PipelineFactory;
import mc.monacotelecom.inventory.common.nls.LocalizedMessageBuilder;
import mc.monacotelecom.services.entity.*;
import mc.monacotelecom.services.importer.pipeline.stage.*;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RscPipelineFactory<T extends IEntity> extends PipelineFactory<T> {

    private final ProvisioningTagPersistStage provisioningTagPersistStage;
    private final ActivationCodePersistStage activationCodePersistStage;
    private final ProvisioningActionPersistStage provisioningActionPersistStage;
    private final ProvisioningProductPersistStage provisioningProductPersistStage;
    private final TechnicalParameterPersistStage technicalParameterPersistStage;
	
	private final ProvisioningPersistStage provisioningPersistStage;

    public RscPipelineFactory(final ProvisioningTagPersistStage provisioningTagPersistStage,
                              final ActivationCodePersistStage activationCodePersistStage,
                              final ProvisioningActionPersistStage provisioningActionPersistStage,
                              final ProvisioningProductPersistStage provisioningProductPersistStage,
                              final TechnicalParameterPersistStage technicalParameterPersistStage,
							  final ProvisioningPersistStage provisioningPersistStage,
                              final LocalizedMessageBuilder localizedMessageBuilder,
                              final ImporterResolver importerResolver) {
        super(importerResolver, localizedMessageBuilder);
        this.provisioningTagPersistStage = provisioningTagPersistStage;
        this.activationCodePersistStage = activationCodePersistStage;
        this.provisioningActionPersistStage = provisioningActionPersistStage;
        this.provisioningProductPersistStage = provisioningProductPersistStage;
        this.technicalParameterPersistStage = technicalParameterPersistStage;
		this.provisioningPersistStage = provisioningPersistStage;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected PersistStage<T> resolvePersistStageForType(Class<? extends IEntity> type) {
        if (ProvisioningTag.class.isAssignableFrom(type)) {
            return (PersistStage<T>) provisioningTagPersistStage;
        } else if (ActivationCode.class.isAssignableFrom(type)) {
            return (PersistStage<T>) activationCodePersistStage;
        } else if (ProvisioningAction.class.isAssignableFrom(type)) {
            return (PersistStage<T>) provisioningActionPersistStage;
        } else if (ProvisioningProduct.class.isAssignableFrom(type)) {
            return (PersistStage<T>) provisioningProductPersistStage;
        } else if (TechnicalParameter.class.isAssignableFrom(type)) {
            return (PersistStage<T>) technicalParameterPersistStage;
        } else if (Provisioning.class.isAssignableFrom(type)) {
            return (PersistStage<T>) provisioningPersistStage;
        }
        return new PersistStage<T>() {
            @Override
            public void run() {
                log.info("Do nothing");
            }
        };
    }
}
