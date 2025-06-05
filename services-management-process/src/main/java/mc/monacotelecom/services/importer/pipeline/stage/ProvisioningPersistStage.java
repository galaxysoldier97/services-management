package mc.monacotelecom.services.importer.pipeline.stage;

import lombok.EqualsAndHashCode;
import mc.monacotelecom.inventory.common.importer.process.pipeline.PersistStage;
import mc.monacotelecom.services.entity.*;
import mc.monacotelecom.services.repository.*;
import org.springframework.stereotype.Component;

@Component
@EqualsAndHashCode(callSuper = true)
public class ProvisioningPersistStage extends PersistStage<Provisioning> {

    private final ProvisioningTagRepository provisioningTagRepository;
    private final ActivationCodeRepository activationCodeRepository;
    private final ProvisioningActionRepository provisioningActionRepository;
    private final ProvisioningProductRepository provisioningProductRepository;
    private final TechnicalParameterRepository technicalParameterRepository;

    public ProvisioningPersistStage(final ProvisioningTagRepository provisioningTagRepository,
                                    final ActivationCodeRepository activationCodeRepository,
                                    final ProvisioningActionRepository provisioningActionRepository,
                                    final ProvisioningProductRepository provisioningProductRepository,
                                    final TechnicalParameterRepository technicalParameterRepository) {
        super();
        addAction(PersistActionType.SAVE, new PersistStageSaveAction((t, p) -> saveOrUpdate(t)));
        addAction(PersistActionType.UPDATE, new PersistStageUpdateAction<Provisioning>((t, p) -> saveOrUpdate(t)));
        addAction(PersistActionType.DELETE, new PersistStageDeleteAction<Provisioning>((t, p) -> delete(t)));

        this.provisioningTagRepository = provisioningTagRepository;
        this.provisioningActionRepository = provisioningActionRepository;
        this.activationCodeRepository = activationCodeRepository;
        this.provisioningProductRepository = provisioningProductRepository;
        this.technicalParameterRepository = technicalParameterRepository;
    }

    Provisioning delete(Provisioning t) {
        if (t instanceof ProvisioningTag) {
            provisioningTagRepository.delete((ProvisioningTag) t);
        } else if (t instanceof ActivationCode) {
            activationCodeRepository.delete((ActivationCode) t);
        } else if (t instanceof ProvisioningAction) {
            provisioningActionRepository.delete((ProvisioningAction) t);
        } else if (t instanceof TechnicalParameter) {
            technicalParameterRepository.delete((TechnicalParameter) t);
        }

        return t;
    }

    Provisioning saveOrUpdate(Provisioning t) {
        if (t instanceof ProvisioningTag) {
            provisioningTagRepository.save((ProvisioningTag) t);
        } else if (t instanceof ActivationCode) {
            activationCodeRepository.save((ActivationCode) t);
        } else if (t instanceof ProvisioningAction) {
            provisioningActionRepository.save((ProvisioningAction) t);
        } else if (t instanceof TechnicalParameter) {
            technicalParameterRepository.save((TechnicalParameter) t);
        }
        return t;
    }
}
