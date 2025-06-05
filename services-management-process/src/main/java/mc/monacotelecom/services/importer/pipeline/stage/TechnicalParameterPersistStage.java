package mc.monacotelecom.services.importer.pipeline.stage;

import lombok.EqualsAndHashCode;
import mc.monacotelecom.inventory.common.importer.process.pipeline.PersistStage;
import mc.monacotelecom.services.entity.TechnicalParameter;
import mc.monacotelecom.services.repository.ProvisioningActionParameterRepository;
import mc.monacotelecom.services.repository.TechnicalParameterRepository;
import org.springframework.stereotype.Component;

@Component
@EqualsAndHashCode(callSuper = true)
public class TechnicalParameterPersistStage extends PersistStage<TechnicalParameter> {

    private final TechnicalParameterRepository technicalParameterRepository;
    private final ProvisioningActionParameterRepository provisioningActionParameterRepository;

    public TechnicalParameterPersistStage(final TechnicalParameterRepository technicalParameterRepository,
                                          final ProvisioningActionParameterRepository provisioningActionParameterRepository) {
        super();
        addAction(PersistActionType.SAVE, new PersistStageSaveAction((t, p) -> saveOrUpdate(t)));
        addAction(PersistActionType.UPDATE, new PersistStageUpdateAction<TechnicalParameter>((t, p)-> saveOrUpdate(t)));
        addAction(PersistActionType.DELETE, new PersistStageDeleteAction<TechnicalParameter>((t, p) -> delete(t)));
        this.technicalParameterRepository = technicalParameterRepository;
        this.provisioningActionParameterRepository = provisioningActionParameterRepository;
    }

    TechnicalParameter delete(TechnicalParameter t) {
        technicalParameterRepository.delete(t);
        return t;
    }

    TechnicalParameter saveOrUpdate(TechnicalParameter t) {
       return technicalParameterRepository.save(t);
    }
}
