package mc.monacotelecom.services.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mc.monacotelecom.services.dto.ServiceParameterDTO;
import mc.monacotelecom.services.dto.request.AddOrUpdateServiceParameterDTO;
import mc.monacotelecom.services.dto.request.UpdateServiceParameterDTO;
import mc.monacotelecom.services.process.ServiceParameterProcess;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServiceParameterService {

    private final ServiceParameterProcess serviceParameterProcess;

    @Transactional
    public List<ServiceParameterDTO> add(final long serviceId, final List<AddOrUpdateServiceParameterDTO> serviceParameters) {
        return serviceParameterProcess.add(serviceId, serviceParameters);
    }

    @Transactional
    public void delete(final long serviceId, final long technicalParameterId) {
        serviceParameterProcess.delete(serviceId, technicalParameterId);
    }

    @Transactional
    public ServiceParameterDTO patch(final long serviceId, final long technicalParameterId, final UpdateServiceParameterDTO dto) {
        return serviceParameterProcess.patch(serviceId, technicalParameterId, dto);
    }

    @Transactional
    public List<ServiceParameterDTO> updateServiceParameters(final long serviceId, final List<AddOrUpdateServiceParameterDTO> serviceParameters) {
        return serviceParameterProcess.updateServiceParameters(serviceId, serviceParameters);
    }

    @Transactional
    public void deleteAll(final long serviceId) {
        serviceParameterProcess.deleteAll(serviceId);
    }

}
