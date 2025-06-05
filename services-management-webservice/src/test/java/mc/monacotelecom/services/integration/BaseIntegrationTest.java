package mc.monacotelecom.services.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import mc.monacotelecom.importer.ImporterProcessStatus;
import mc.monacotelecom.inventory.common.importer.domain.repository.ImportHistoryRepository;
import mc.monacotelecom.services.process.*;
import mc.monacotelecom.services.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public abstract class BaseIntegrationTest {

    @Autowired
    public MockMvc mockMvc;

    // Mock authentication

    @Mock
    protected Authentication authentication;

    @Mock
    protected SecurityContext securityContext;

    // Spy process classes

    @SpyBean
    public ActivationCodeProcess activationCodeProcess;

    @SpyBean
    public ProvisioningActionParameterProcess provisioningActionParameterProcess;

    @SpyBean
    public ProvisioningProductProcess provisioningProductProcess;

    @SpyBean
    public ProvisioningTagProcess provisioningTagProcess;

    @SpyBean
    public TagActivationProcess tagActivationProcess;

    @SpyBean
    public ServiceProcess serviceProcess;

    // Spy repositories

    @SpyBean
    public ActivationCodeRepository activationCodeRepository;

    @SpyBean
    public TagActivationRepository tagActivationRepository;

    @SpyBean
    public ProvisioningTagRepository provisioningTagRepository;

    @SpyBean
    public ProvisioningActionRepository provisioningActionRepository;

    @SpyBean
    public ProvisioningProductRepository provisioningProductRepository;

    @SpyBean
    public TechnicalParameterRepository technicalParameterRepository;

    @SpyBean
    public ProvisioningActionParameterRepository provisioningActionParameterRepository;

    @SpyBean
    public ServiceRepository<?> serviceRepository;

    @SpyBean
    public ServiceAccessRepository serviceAccessRepository;

    @SpyBean
    public ServiceComponentRepository serviceComponentRepository;

    @SpyBean
    public ServiceTagRepository serviceTagRepository;

    @SpyBean
    public ServiceActivationRepository serviceActivationRepository;

    @SpyBean
    public ServiceParameterRepository serviceParameterRepository;

    @SpyBean
    public ImportHistoryRepository importHistoryRepository;

    @SpyBean
    public JobConfigurationRepository jobConfigurationRepository;


    // Miscellaneous

    @Autowired
    public ObjectMapper objectMapper;

    public final Pageable PAGEABLE = PageRequest.of(0, 20);

    @BeforeEach
    public void setup() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(null);
    }

    public boolean allImportsCompleted() {
        return importHistoryRepository.findAll().stream().allMatch(x -> ImporterProcessStatus.ImportStatus.COMPLETED.equals(x.getImportStatus()));
    }
}
