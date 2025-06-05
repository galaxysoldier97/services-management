package mc.monacotelecom.services.process;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mc.monacotelecom.inventory.common.Lambdas;
import mc.monacotelecom.inventory.common.nls.LocalizedMessageBuilder;
import mc.monacotelecom.services.dto.ServiceParameterDTO;
import mc.monacotelecom.services.dto.request.AddOrUpdateServiceParameterDTO;
import mc.monacotelecom.services.dto.request.UpdateServiceParameterDTO;
import mc.monacotelecom.services.entity.Service;
import mc.monacotelecom.services.entity.ServiceParameter;
import mc.monacotelecom.services.entity.TechnicalParameter;
import mc.monacotelecom.services.enums.TechnicalParameterType;
import mc.monacotelecom.services.exceptions.SvcConflictException;
import mc.monacotelecom.services.exceptions.SvcNotFoundException;
import mc.monacotelecom.services.mapper.ServiceParameterMapper;
import mc.monacotelecom.services.repository.ServiceParameterRepository;
import mc.monacotelecom.services.repository.ServiceRepository;
import mc.monacotelecom.services.repository.TechnicalParameterRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static mc.monacotelecom.services.translation.TranslationMessages.*;


@Slf4j
@RequiredArgsConstructor
@Component
public class ServiceParameterProcess {

    private final TechnicalParameterRepository technicalParameterRepository;
    private final ServiceParameterRepository serviceParameterRepository;
    private final ServiceParameterMapper serviceParameterMapper;
    private final ServiceRepository<Service> serviceRepository;
    private final LocalizedMessageBuilder localizedMessageBuilder;

    public List<ServiceParameterDTO> add(final long serviceId, final List<AddOrUpdateServiceParameterDTO> serviceParametersRequest) {
        final var service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new SvcNotFoundException(localizedMessageBuilder, SERVICE_NOT_FOUND_ID, serviceId));

        List<ServiceParameterDTO> createdServiceParameters = new ArrayList<>();

        serviceParametersRequest.forEach(serviceParameterRequest -> {
            final var technicalParameter = technicalParameterRepository
                    .findByParameterCodeAndParameterType(serviceParameterRequest.getTechnicalParameterCode(), TechnicalParameterType.CONTEXT)
                    .orElseThrow(() -> new SvcNotFoundException(localizedMessageBuilder, TECHNICAL_PARAMETERS_CODE_TYPE_NOT_FOUND, serviceParameterRequest.getTechnicalParameterCode(), TechnicalParameterType.CONTEXT));


            if (serviceParameterRepository.existsByServiceIdAndParameterCodeAndParameterType(serviceId, technicalParameter.getParameterCode(), technicalParameter.getParameterType().name())) {
                throw new SvcConflictException(localizedMessageBuilder, SERVICE_PARAMETER_ALREADY_EXISTING, serviceId, technicalParameter.getParameterCode());
            }

            var serviceParameter = new ServiceParameter();
            serviceParameter.setService(service);
            serviceParameter.setServiceId(serviceId);
            serviceParameter.setParameterCode(technicalParameter.getParameterCode());
            serviceParameter.setParameterType(technicalParameter.getParameterType().name());
            serviceParameter.setTechnicalParameter(technicalParameter);
            serviceParameter.setValue(serviceParameterRequest.getValue());

            createdServiceParameters.add(serviceParameterMapper.toDto(serviceParameterRepository.save(serviceParameter)));
        });

        return createdServiceParameters;
    }

    public ServiceParameterDTO patch(final long serviceId, final long parameterId, final UpdateServiceParameterDTO serviceParameterDTO) {
        var serviceParameter = serviceParameterRepository.findByTechnicalParameterInternalIdAndServiceId(parameterId, serviceId)
                .orElseThrow(() -> new SvcNotFoundException(localizedMessageBuilder, SERVICE_PARAMETER_NOT_FOUND_SERVICE_PARAMETER, serviceId, parameterId));
        Lambdas.verifyAndApplyString.accept(StringUtils.isNotBlank(serviceParameterDTO.getValue()), serviceParameter::setValue, serviceParameterDTO.getValue());
        serviceParameterRepository.save(serviceParameter);
        return serviceParameterMapper.toDto(serviceParameter);
    }

    public void delete(final long serviceId, final long parameterId) {
        var serviceParameter = serviceParameterRepository.findByTechnicalParameterInternalIdAndServiceId(parameterId, serviceId)
                .orElseThrow(() -> new SvcNotFoundException(localizedMessageBuilder, SERVICE_PARAMETER_NOT_FOUND_SERVICE_PARAMETER, serviceId, parameterId));

        serviceParameterRepository.delete(serviceParameter);
    }

    public void deleteAll(final long serviceId) {
         serviceParameterRepository.deleteAllByServiceId( serviceId);
    }

    public List<ServiceParameterDTO> updateServiceParameters(final long serviceId, final List<AddOrUpdateServiceParameterDTO> serviceParametersRequest) {
        List<ServiceParameterDTO> createdServiceParameters = new ArrayList<>();

        serviceParametersRequest.forEach(serviceParameterRequest -> {
            final TechnicalParameter technicalParameter = technicalParameterRepository
                    .findByParameterCodeAndParameterType(serviceParameterRequest.getTechnicalParameterCode(), TechnicalParameterType.CONTEXT)
                    .orElseThrow(() -> new SvcNotFoundException(localizedMessageBuilder, TECHNICAL_PARAMETERS_CODE_TYPE_NOT_FOUND, serviceParameterRequest.getTechnicalParameterCode(), TechnicalParameterType.CONTEXT));

            var serviceParameter = serviceParameterRepository.findByServiceIdAndParameterCodeAndParameterType(serviceId, technicalParameter.getParameterCode(), technicalParameter.getParameterType().name())
                    .orElseThrow(() -> new SvcNotFoundException(localizedMessageBuilder, SERVICE_PARAMETER_NOT_FOUND_SERVICE_PARAMETER, serviceId, technicalParameter.getParameterCode()));
            Lambdas.verifyAndApplyString.accept(StringUtils.isNotBlank(serviceParameterRequest.getValue()), serviceParameter::setValue, serviceParameterRequest.getValue());
            serviceParameterRepository.save(serviceParameter);
            createdServiceParameters.add(serviceParameterMapper.toDto(serviceParameter));
        });

        return createdServiceParameters;
    }
}
