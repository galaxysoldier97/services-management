package mc.monacotelecom.services.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mc.monacotelecom.inventory.common.nls.LocalizedMessageBuilder;
import mc.monacotelecom.services.dto.request.UpdateServiceDTO;
import mc.monacotelecom.services.entity.Service;
import mc.monacotelecom.services.entity.ServiceAccess;
import mc.monacotelecom.services.entity.ServiceComponent;
import mc.monacotelecom.services.enums.Network;
import mc.monacotelecom.services.exceptions.SvcValidationException;
import mc.monacotelecom.services.repository.ServiceAccessRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static mc.monacotelecom.services.translation.TranslationMessages.TECHID_NOT_LONG;
import static mc.monacotelecom.services.translation.TranslationMessages.TECHID_NOT_START_WITH_PLMN;

/**
 * Computes what should be the techId of a service according to several fields
 */
@Slf4j
@RequiredArgsConstructor
@Component
public final class TechIdProcessor {

    private final ServiceAccessRepository serviceAccessRepository;
    private final LocalizedMessageBuilder localizedMessageBuilder;

    @Value("${plmn.code:21210}")
    protected String plmnCode;

    protected static final String DEFAULT_MIN = "0000000001";

    /**
     * Compute TechID at creation time for a given Service Access
     *
     * @param serviceAccess Service Access
     * @return Tech ID
     */
    public Optional<String> getAtCreationTime(ServiceAccess serviceAccess) {
        var accessType = serviceAccess.getAccessType();
        switch (serviceAccess.getServiceActivity()) {
            case INTERNET:
                switch (accessType) {
                    case DOCSIS:
                    case FTTH:
                    default:
                        // No value at creation time, will be set manually at update time.
                        return Optional.empty();
                }
            case TV:
                switch (accessType) {
                    case ZATTOO:
                    default:
                        // No value at creation time, will be set manually at update time.
                        return Optional.empty();
                }
            case MOBILE:
                switch (accessType) {
                    case FREEDHOME:
                        return Optional.ofNullable(calculateNextNumericTechId(accessType));
                   
                    case BBHB:
                 
                    case MOBILE:
                    default:
                        // No value at creation time, will be set manually at update time.
                        return Optional.empty();
                }
            default:
                return Optional.empty();
        }
    }

    /**
     * Calculate next value for numeric Tech IDs like in FREEDHOME/CAS cases
     *
     * @param accessType Access Type to consider
     * @return Next Tech ID value
     * @throws SvcValidationException if the existing Tech IDs are inconsistent
     */
    public String calculateNextNumericTechId(Network accessType) {
        final AtomicReference<String> nextTechId = new AtomicReference<>();

        serviceAccessRepository.findFirstByAccessTypeOrderByTechIdDesc(accessType).ifPresentOrElse(
                maxServiceAccess -> {
                    String currentMaxTechId = maxServiceAccess.getTechId();
                    try {
                        nextTechId.set(this.nextTechId(currentMaxTechId, accessType));
                    } catch (NumberFormatException e) {
                        throw new SvcValidationException(localizedMessageBuilder, TECHID_NOT_LONG, accessType, currentMaxTechId);
                    }
                },
                () -> {
                    if (accessType.equals(Network.FREEDHOME)) {
                        nextTechId.set(plmnCode + DEFAULT_MIN);
                    }
                }
        );

        return nextTechId.get();
    }

    public String nextTechId(String currentTechId, Network accessType) {
        if (accessType.equals(Network.FREEDHOME)) {
            // Remove PLMN prefix before incrementing
            if (!currentTechId.startsWith(plmnCode)) {
                throw new SvcValidationException(localizedMessageBuilder, TECHID_NOT_START_WITH_PLMN, accessType, currentTechId, plmnCode);
            }
            return plmnCode + String.format("%010d", (Long.parseLong(currentTechId.substring(plmnCode.length() - 1)) + 1));
        } else {
            return String.format("%010d", Long.parseLong(currentTechId) + 1);
        }
    }


    /**
     * Compute Tech ID at update time for a Service (Access or Component)
     *
     * @param service                 Service to work on
     * @param updateServiceDTO DTO describing the current update
     * @param <T>                     Type of the Service: Access or Component
     * @return Tech ID
     */
    public <T extends Service> Optional<String> getAtUpdateTime(final T service, final UpdateServiceDTO updateServiceDTO) {
        switch (service.getServiceCategory()) {
            case ACCESS:
                final var accessType = ((ServiceAccess) service).getAccessType();
                if (accessType == null) {
                    log.warn(String.format("Access type should not be null on service access '%s'", service.getServiceId()));
                    return Optional.ofNullable(updateServiceDTO.getTechId());
                }
                switch (accessType) {
                    case ZATTOO:
                        return Optional.ofNullable(String.valueOf(updateServiceDTO.getCustomerNo()));
                    case FREEDHOME:
                        return Optional.empty();
                    case FTTH:
           
                    case DOCSIS:
                    
                   
                    case MOBILE:
                    default:
                        return Optional.ofNullable(updateServiceDTO.getTechId());
                }
            case COMPONENT:
                final var componentType = ((ServiceComponent) service).getComponentType();
                if (componentType == null) {
                    log.warn(String.format("Component type should not be null on service component '%s'", service.getServiceId()));
                    return Optional.ofNullable(updateServiceDTO.getTechId());
                }
                switch (componentType) {
                    case "TEAMS":
                        return Optional.ofNullable(updateServiceDTO.getTechId());
                    case "CAS":
                    default:
                        return Optional.empty();
                }
            default:
                return Optional.empty();
        }
    }
}
