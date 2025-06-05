package mc.monacotelecom.services.process;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mc.monacotelecom.inventory.common.exporter.process.ExcelGenerator;
import mc.monacotelecom.inventory.common.nls.LocalizedMessageBuilder;
import mc.monacotelecom.services.assembler.ProvisioningActionResourceAssembler;
import mc.monacotelecom.services.dto.ProvisioningActionDTO;
import mc.monacotelecom.services.dto.request.CreateProvisioningActionDTO;
import mc.monacotelecom.services.dto.request.UpdateProvisioningActionDTO;
import mc.monacotelecom.services.entity.ProvisioningAction;
import mc.monacotelecom.services.entity.ProvisioningActionKey;
import mc.monacotelecom.services.entity.ProvisioningProduct;
import mc.monacotelecom.services.entity.ProvisioningTag;
import mc.monacotelecom.services.enums.ProvisioningTagAction;
import mc.monacotelecom.services.exceptions.SvcNotFoundException;
import mc.monacotelecom.services.exceptions.SvcValidationException;
import mc.monacotelecom.services.importer.data.GenericDataCsvLines.ProvisioningActionCsvLine;
import mc.monacotelecom.services.mapper.ExporterEntityMapper;
import mc.monacotelecom.services.mapper.ProvisioningActionMapper;
import mc.monacotelecom.services.mapper.ProvisioningTagMapper;
import mc.monacotelecom.services.repository.ProvisioningActionRepository;
import mc.monacotelecom.services.repository.ProvisioningTagRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.Objects;
import java.util.stream.Collectors;

import static mc.monacotelecom.services.translation.TranslationMessages.*;


@Slf4j
@Component
@RequiredArgsConstructor
public class ProvisioningActionProcess {

    private final ProvisioningActionRepository provisioningActionRepository;
    private final ProvisioningTagRepository provisioningTagRepository;
    private final ProvisioningActionResourceAssembler provisioningActionResourceAssembler = ProvisioningActionResourceAssembler.of(ProvisioningActionProcess.class);
    private final LocalizedMessageBuilder localizedMessageBuilder;
    private final ProvisioningActionMapper provisioningActionMapper;
    private final ProvisioningTagMapper provisioningTagMapper;
    private final ExcelGenerator excelGenerator;
    private final ExporterEntityMapper exporterEntityMapper;

    public Page<ProvisioningActionDTO> getAll(Pageable pageable) {
        Page<ProvisioningAction> provisioningActions = provisioningActionRepository.findAll(pageable);
        return provisioningActions.map(provisioningActionResourceAssembler::toModel);
    }

    public Page<ProvisioningActionDTO> getAllProvisioningActionForProvisioningTag(Long tagId, Pageable pageable) {
        Page<ProvisioningAction> provisioningActions = provisioningActionRepository.findByTagInternalId(tagId, pageable);
        return provisioningActions.map(provisioningActionResourceAssembler::toModel);
    }

    public ProvisioningActionDTO getById(Long id) {
        ProvisioningAction provisioningAction = provisioningActionRepository.findByInternalId(id)
                .orElseThrow(() -> new SvcValidationException(localizedMessageBuilder, PROVISIONING_ACTION_NOT_FOUND_ID, id));
        return provisioningActionResourceAssembler.toModel(provisioningAction);
    }

    public ProvisioningActionDTO get(Long tagId, ProvisioningTagAction action) {
        ProvisioningAction provisioningAction = provisioningActionRepository.findByTagInternalIdAndTagAction(tagId, action)
                .orElseThrow(() -> new SvcValidationException(localizedMessageBuilder, PROVISIONING_ACTION_NOT_FOUND_TAGID_ACTION, tagId, action));
        return provisioningActionResourceAssembler.toModel(provisioningAction);
    }

    public void add(CreateProvisioningActionDTO dto) {
        ProvisioningTag provisioningTag = provisioningTagRepository.findByInternalId(dto.getTagId())
                .orElseThrow(() -> new SvcValidationException(localizedMessageBuilder, PROVISIONING_TAG_NOT_FOUND_ID, dto.getTagId()));

        if (provisioningActionRepository.existsById(new ProvisioningActionKey(provisioningTag.getTagCode(), dto.getTagAction()))) {
            throw new SvcValidationException(localizedMessageBuilder, PROVISIONING_ACTION_ALREADY_EXIST, provisioningTag.getTagCode(), dto.getTagAction());
        }

        ProvisioningActionDTO provisioningActionDTO = new ProvisioningActionDTO();
        provisioningActionDTO.setProvisioningTag(provisioningTagMapper.toDto(provisioningTag));
        provisioningActionDTO.setTagAction(dto.getTagAction());
        provisioningActionDTO.setServiceAction(dto.getServiceAction());

        provisioningActionRepository.save(provisioningActionMapper.toEntity(provisioningActionDTO));
    }

    public ProvisioningActionDTO update(Long id, UpdateProvisioningActionDTO dto) {
        ProvisioningAction provisioningAction = provisioningActionRepository.findByInternalId(id)
                .orElseThrow(() -> new SvcValidationException(localizedMessageBuilder, PROVISIONING_ACTION_NOT_FOUND_ID, id));

        if (Objects.nonNull(dto.getServiceAction())) {
            provisioningAction.setServiceAction(dto.getServiceAction());
        }

        return provisioningActionResourceAssembler.toModel(provisioningActionRepository.save(provisioningAction));
    }

    public void delete(final Long id, final boolean force) {
        ProvisioningAction provisioningAction = provisioningActionRepository.findByInternalId(id)
                .orElseThrow(() -> new SvcNotFoundException(localizedMessageBuilder, PROVISIONING_ACTION_NOT_FOUND_ID, id));

        final var relatedProvisioningProductsIds = provisioningAction.getProvisioningProducts()
                .stream()
                .map(ProvisioningProduct::getInternalId)
                .collect(Collectors.toList());

        if (!relatedProvisioningProductsIds.isEmpty() && !force) {
            throw new SvcValidationException(localizedMessageBuilder, PROVISIONING_ACTION_NOT_DELETABLE, id, relatedProvisioningProductsIds);
        }

        provisioningActionRepository.deleteByInternalId(id);
    }

    public ByteArrayInputStream export() {
        final var toExport = provisioningActionRepository.findAll()
                .stream().map(exporterEntityMapper::toProvisioningActionExporter);

        return excelGenerator.writeToExcel(toExport, ProvisioningActionCsvLine.class, "export.provisioningAction");
    }
}
