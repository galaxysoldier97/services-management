package mc.monacotelecom.services.client.connector;

import lombok.extern.slf4j.Slf4j;
import mc.monacotelecom.common.restclient.AbstractConnector;
import mc.monacotelecom.common.restclient.AuthenticationManager;
import mc.monacotelecom.services.client.connector.dto.PagedServiceAccessResourceResponse;
import mc.monacotelecom.services.client.connector.dto.PagedServiceComponentResourceResponse;
import mc.monacotelecom.services.dto.*;
import mc.monacotelecom.services.dto.request.*;
import mc.monacotelecom.services.dto.search.SearchServiceDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ServicesManagementConnector extends AbstractConnector {

    static final String PRIVATE_AUTH = "/private/auth";

    public ServicesManagementConnector(final String baseUrl,
                                       final RestTemplate restTemplate,
                                       final AuthenticationManager authenticationManager) {
        super(baseUrl, restTemplate, authenticationManager);
    }

    public List<ProvisioningProductDTO> decomposeProductsRequest(DecompositionRequestDTO decompositionRequestDTO) {
        URI uri = this.getUri(PRIVATE_AUTH, "/provisioningtags/decomposeproductsrequest");
        final var method = HttpMethod.PATCH;
        log.info("Decompose products from DTO {} by {} on URL={}", decompositionRequestDTO, method, uri);
        HttpHeaders headers = patchHeader();
        HttpEntity<DecompositionRequestDTO> requestEntity = new HttpEntity<>(decompositionRequestDTO, headers);
        return exchange(uri, method, requestEntity, new ParameterizedTypeReference<>() {
        });
    }


    /**
     * find Service Information identified by its {@param matrixVars}
     *
     * @param matrixVars Search parameters
     * @return {@link List<ServiceDTO>} object.
     */
    public <T extends ServiceDTO> List<T> searchService(MultiValueMap<String, String> matrixVars, Class<T> type) {
        URI uri = this.getUri("/private/auth/services/search");
        uri = UriComponentsBuilder.fromUriString(uri.toString()).queryParams(matrixVars).build().toUri();
        final var method = HttpMethod.GET;
        List<T> services = new ArrayList<>();
        log.info("Search Services by {} on URL={}", method, uri);
        HttpEntity<Void> httpEntity = new HttpEntity<>(authHeaders());
        if (type.equals(ServiceAccessDTO.class)) {
            PagedServiceAccessResourceResponse response = exchange(uri, method, httpEntity, PagedServiceAccessResourceResponse.class);
            if (response != null && response.getEmbedded() != null && CollectionUtils.isNotEmpty(response.getEmbedded().getServiceAccesses())) {
                response.getEmbedded().getServiceAccesses().forEach(serviceAccess -> services.add(type.cast(serviceAccess)));
            }
            return services;
        } else if (type.equals(ServiceComponentDTO.class)) {
            PagedServiceComponentResourceResponse response = exchange(uri, method, httpEntity, PagedServiceComponentResourceResponse.class);
            if (response != null && response.getEmbedded() != null && CollectionUtils.isNotEmpty(response.getEmbedded().getServiceComponents())) {
                response.getEmbedded().getServiceComponents().forEach(serviceComponent -> services.add(type.cast(serviceComponent)));
            }
            return services;
        } else {
            throw new NotImplementedException(String.format("Connector function does not support %s type.", type));
        }
    }

    /**
     * find Service Access Information identified by its {@param matrixVars}
     *
     * @param matrixVars Search parameters
     * @return {@link List<ServiceAccessDTO>} object.
     */
    public List<ServiceAccessDTO> searchServiceAccess(MultiValueMap<String, String> matrixVars) {
        URI uri = this.getUri("/private/auth/services/search/accesses");
        uri = UriComponentsBuilder.fromUriString(uri.toString()).queryParams(matrixVars).build().toUri();
        final var method = HttpMethod.GET;
        log.info("Search Services Accesses by {} on URL={}", method, uri);
        HttpEntity<Void> httpEntity = new HttpEntity<>(authHeaders());

        List<ServiceAccessDTO> services = new ArrayList<>();
        PagedServiceAccessResourceResponse response = exchange(uri, method, httpEntity, PagedServiceAccessResourceResponse.class);
        if (response != null && response.getEmbedded() != null && CollectionUtils.isNotEmpty(response.getEmbedded().getServiceAccesses())) {
            services.addAll(response.getEmbedded().getServiceAccesses());
        }
        return services;
    }

    /**
     * find Service Component Information identified by its {@param matrixVars}
     *
     * @param matrixVars Search parameters
     * @return {@link List<ServiceComponentDTO>} object.
     */
    public List<ServiceComponentDTO> searchServiceComponent(MultiValueMap<String, String> matrixVars) {
        URI uri = this.getUri("/private/auth/services/search/components");
        uri = UriComponentsBuilder.fromUriString(uri.toString()).queryParams(matrixVars).build().toUri();
        final var method = HttpMethod.GET;
        log.info("Search Services Components by {} on URL={}", method, uri);
        HttpEntity<Void> httpEntity = new HttpEntity<>(authHeaders());

        List<ServiceComponentDTO> services = new ArrayList<>();
        PagedServiceComponentResourceResponse response = exchange(uri, method, httpEntity, PagedServiceComponentResourceResponse.class);
        if (response != null && response.getEmbedded() != null && CollectionUtils.isNotEmpty(response.getEmbedded().getServiceComponents())) {
            services.addAll(response.getEmbedded().getServiceComponents());
        }
        return services;
    }

    public <T extends ServiceDTO> T getServiceById(Long id, Class<T> type) {
        URI uri = this.getUri("/private/auth/services/" + id);
        final var method = HttpMethod.GET;
        log.info("Get Service Information with id {} by {} on URL={}", id, method, uri);
        if (id != null) {
            HttpEntity<Void> httpEntity = new HttpEntity<>(authHeaders());
            return exchange(uri, method, httpEntity, type);
        }
        return null;
    }

    public <T extends ServiceDTO> T addServiceOnSubscription(AddServiceRequestDTO addServiceRequestDTO, Class<T> type) {
        URI uri = this.getUri(PRIVATE_AUTH, "/services");
        final var method = HttpMethod.PUT;
        log.info("Add service with DTO {} by {} on URL={}", addServiceRequestDTO, method, uri);
        HttpEntity<AddServiceRequestDTO> requestEntity = new HttpEntity<>(addServiceRequestDTO, authHeaders());
        return exchange(uri, method, requestEntity, type);
    }

    public ChangeTagsResponse changeTags(Long serviceId, ChangeTagsDTO changeTagsDTO) {
        URI uri = this.getUri(PRIVATE_AUTH, "/services/", serviceId + "", "/changetags");
        final var method = HttpMethod.PATCH;
        log.info("Change tags with DTO {} by {} on URL={}", changeTagsDTO, method, uri);
        HttpHeaders headers = patchHeader();
        HttpEntity<ChangeTagsDTO> requestEntity = new HttpEntity<>(changeTagsDTO, headers);
        return exchange(uri, method, requestEntity, ChangeTagsResponse.class);
    }

    public <T extends ServiceDTO> T updateService(Long serviceId, UpdateServiceDTO updateServiceRequest, Class<T> type) {
        URI uri = this.getUri(PRIVATE_AUTH, "/services/", serviceId + "");
        final var method = HttpMethod.PATCH;
        log.info("Update service with ID {} and DTO {} by {} on URL={}", serviceId, updateServiceRequest, method, uri);
        HttpHeaders headers = patchHeader();
        HttpEntity<UpdateServiceDTO> requestEntity = new HttpEntity<>(updateServiceRequest, headers);
        return exchange(uri, method, requestEntity, type);
    }

    public <T extends ServiceDTO> T addRequestOnService(ServiceRequestDTO serviceRequestDTO, Class<T> type) {
        URI uri = this.getUri(PRIVATE_AUTH, "/services/", "/addrequest");
        final var method = HttpMethod.PATCH;
        log.info("Add request on service with DTO {} by {} on URL={}", serviceRequestDTO, method, uri);
        HttpHeaders headers = patchHeader();
        HttpEntity<ServiceRequestDTO> requestEntity = new HttpEntity<>(serviceRequestDTO, headers);
        return exchange(uri, method, requestEntity, type);
    }

    public <T extends ServiceDTO> T activateRequestOnService(SearchServiceDTO request, Class<T> type) {
        URI uri = this.getUri(PRIVATE_AUTH, "/services/", "/activaterequest");
        final var method = HttpMethod.PATCH;
        log.info("Activate request with DTO {} by {} on URL={}", request, method, uri);
        HttpHeaders headers = patchHeader();
        HttpEntity<SearchServiceDTO> requestEntity = new HttpEntity<>(request, headers);
        return exchange(uri, method, requestEntity, type);
    }

    public <T extends ServiceDTO> T terminateRequestOnService(Long serviceId, Class<T> type) {
        URI uri = this.getUri(PRIVATE_AUTH, "/services/", serviceId + "", "/terminaterequest");
        final var method = HttpMethod.PATCH;
        log.info("Terminate request on service with ID {} by {} on URL={}", serviceId, method, uri);
        HttpHeaders headers = patchHeader();
        HttpEntity<Void> requestEntity = new HttpEntity<>(null, headers);
        return exchange(uri, method, requestEntity, type);
    }

    public void setActionOnService(ServiceActionRequestDTO serviceActionRequestDTO) {
        URI uri = this.getUri(PRIVATE_AUTH, "/services/", "setactiononservice");
        final var method = HttpMethod.PATCH;
        log.info("Set action on service with DTO {} by {} on URL={}", serviceActionRequestDTO, method, uri);
        HttpHeaders headers = patchHeader();
        HttpEntity<ServiceActionRequestDTO> requestEntity = new HttpEntity<>(serviceActionRequestDTO, headers);
        exchange(uri, method, requestEntity, Void.class);
    }

    public <T extends ServiceDTO> T cancelRequestOnService(SearchServiceDTO searchServiceDTO, Class<T> type) {
        URI uri = this.getUri(PRIVATE_AUTH, "/services/", "/cancelrequest");
        final var method = HttpMethod.PATCH;
        log.info("Cancel action on service with DTO {} by {} on URL={}", searchServiceDTO, method, uri);
        HttpHeaders headers = patchHeader();
        HttpEntity<SearchServiceDTO> requestEntity = new HttpEntity<>(searchServiceDTO, headers);
        return exchange(uri, method, requestEntity, type);
    }

    public <T extends ServiceDTO> T rollbackAddRequestOnService(SearchServiceDTO searchServiceDTO, Class<T> type) {
        URI uri = this.getUri(PRIVATE_AUTH, "/services/", "/rollbackaddrequest");
        final var method = HttpMethod.PATCH;
        log.info("Rollback add request on service with DTO {} by {} on URL={}", searchServiceDTO, method, uri);
        HttpHeaders headers = patchHeader();
        HttpEntity<SearchServiceDTO> requestEntity = new HttpEntity<>(searchServiceDTO, headers);
        return exchange(uri, method, requestEntity, type);
    }

    public void deleteService(Long serviceId) {
        this.deleteService(serviceId, false);
    }

    public void deleteService(Long serviceId, boolean forced) {
        MultiValueMap<String, String> matrixVars = new LinkedMultiValueMap<>();
        matrixVars.add("forced", String.valueOf(forced));
        URI uri = this.getUri(PRIVATE_AUTH, "/services/", String.valueOf(serviceId));
        uri = UriComponentsBuilder.fromUriString(uri.toString()).queryParams(matrixVars).build().toUri();
        final var method = HttpMethod.DELETE;
        log.info("Delete service with ID {} by {} on URL={}", serviceId, method, uri);
        HttpEntity<Void> requestEntity = new HttpEntity<>(null, authHeaders());
        exchange(uri, method, requestEntity, Void.class);
    }

    /**
     * Get all ParametersOnAction for a provisioningtag
     *
     * @param getParametersOnActionDTO Tag codes and actions
     * @return DTO containing technical parameters
     */
    public ParametersOnActionResponseDTO getAllParametersOnAction(GetParametersOnActionDTO getParametersOnActionDTO) {
        final URI uri = this.getUri("/private/auth/provisioningtags/parametersOnActions");
        final var method = HttpMethod.POST;
        log.info("Get all parameters on action with DTO {} by {} on URL={}", getParametersOnActionDTO, method, uri);
        final HttpEntity<GetParametersOnActionDTO> requestEntity = new HttpEntity<>(getParametersOnActionDTO, authHeaders());
        return exchange(uri, method, requestEntity, ParametersOnActionResponseDTO.class);
    }

    /**
     * Add service parameters on a service
     *
     * @param serviceId         Service to add parameters on on
     * @param serviceParameters Parameters to add
     * @return Parameters that were created
     */
    public List<ServiceParameterDTO> addServiceParameters(long serviceId, List<AddOrUpdateServiceParameterDTO> serviceParameters) {
        URI uri = this.getUri(PRIVATE_AUTH + "/services/" + serviceId + "/parameters");
        final var method = HttpMethod.POST;
        log.info("Adding service parameters {} on service {} by {} on URL={}", serviceParameters, serviceId, method, uri);
        HttpEntity<List<AddOrUpdateServiceParameterDTO>> requestEntity = new HttpEntity<>(serviceParameters, authHeaders());
        return exchange(uri, method, requestEntity, new ParameterizedTypeReference<>() {
        });
    }

    /**
     * Delete service parameter on a service
     *
     * @param serviceId            Service to remove parameter from, identified by its internal ID
     * @param technicalParameterId Technical parameter to remove, identified by its technical parameter ID
     */
    public void deleteServiceParameter(long serviceId, long technicalParameterId) {
        URI uri = this.getUri(PRIVATE_AUTH + "/services/" + serviceId + "/parameters/" + technicalParameterId);
        final var method = HttpMethod.DELETE;
        log.info("Deleting service parameter {} on service {} by {} on URL={}", technicalParameterId, serviceId, method, uri);
        HttpEntity<List<AddOrUpdateServiceParameterDTO>> requestEntity = new HttpEntity<>(null, authHeaders());
        exchange(uri, method, requestEntity, Void.class);
    }

    /**
     * Update a service parameter on a service
     *
     * @param serviceId                 Service to update parameter on, identified by its internal ID
     * @param technicalParameterId      Technical parameter to update, identified by its technical parameter ID
     * @param updateServiceParameterDTO DTO with information about the update
     * @return Updated service parameter
     */
    public ServiceParameterDTO updateServiceParameter(long serviceId, long technicalParameterId, UpdateServiceParameterDTO updateServiceParameterDTO) {
        URI uri = this.getUri(PRIVATE_AUTH + "/services/" + serviceId + "/parameters/" + technicalParameterId);
        final var method = HttpMethod.PATCH;
        log.info("Updating service parameter {} on service {} with DTO {} by {} on URL={}", technicalParameterId, serviceId, updateServiceParameterDTO, method, uri);
        HttpEntity<UpdateServiceParameterDTO> requestEntity = new HttpEntity<>(updateServiceParameterDTO, authHeaders());
        return exchange(uri, method, requestEntity, ServiceParameterDTO.class);
    }

    /**
     * Update service parameters on a service
     *
     * @param serviceId         Service to update parameters on, identified by its internal ID
     * @param serviceParameters DTO with information about the updated service parameters
     * @return Updated service parameters
     */
    public List<ServiceParameterDTO> updateServiceParameters(long serviceId, List<AddOrUpdateServiceParameterDTO> serviceParameters) {
        URI uri = this.getUri(PRIVATE_AUTH + "/services/" + serviceId + "/parameters");
        final var method = HttpMethod.PATCH;
        log.info("Updating service parameters on service {} with DTO {} by {} on URL={}", serviceId, serviceParameters, method, uri);
        HttpEntity<List<AddOrUpdateServiceParameterDTO>> requestEntity = new HttpEntity<>(serviceParameters, authHeaders());
        return exchange(uri, method, requestEntity, new ParameterizedTypeReference<>() {
        });
    }

    private HttpHeaders patchHeader() {
        HttpHeaders headers = authHeaders();
        MediaType mediaType = new MediaType("application", "merge-patch+json");
        headers.setContentType(mediaType);
        return headers;
    }

    private HttpHeaders authHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authenticationManager.getTokenWithClientCredentials());
        return headers;
    }
}
