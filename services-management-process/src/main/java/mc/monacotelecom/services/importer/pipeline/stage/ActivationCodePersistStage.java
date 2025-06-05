package mc.monacotelecom.services.importer.pipeline.stage;

import lombok.EqualsAndHashCode;
import mc.monacotelecom.inventory.common.importer.process.pipeline.PersistStage;
import mc.monacotelecom.services.entity.ActivationCode;
import mc.monacotelecom.services.repository.ActivationCodeRepository;
import org.springframework.stereotype.Component;

@Component
@EqualsAndHashCode(callSuper = true)
public class ActivationCodePersistStage extends PersistStage<ActivationCode> {

    private final ActivationCodeRepository activationCodeRepository;

    public ActivationCodePersistStage(final ActivationCodeRepository activationCodeRepository) {
        super();
        addAction(PersistActionType.SAVE, new PersistStageSaveAction((t, p) -> saveOrUpdate(t)));
        addAction(PersistActionType.UPDATE, new PersistStageUpdateAction<ActivationCode>((t, p)-> saveOrUpdate(t)));
        addAction(PersistActionType.DELETE, new PersistStageDeleteAction<ActivationCode>((t, p) -> delete(t)));
        this.activationCodeRepository = activationCodeRepository;
    }

    ActivationCode delete(ActivationCode t) {
        activationCodeRepository.delete(t);
        return t;
    }

    ActivationCode saveOrUpdate(ActivationCode t) {
        return activationCodeRepository.save(t);
    }
}