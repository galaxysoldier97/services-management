package mc.monacotelecom.services.assembler.revision;

import mc.monacotelecom.inventory.common.audit.AuditEnversInfo;
import mc.monacotelecom.services.assembler.ServiceComponentResourceAssembler;
import mc.monacotelecom.services.dto.RevisionDTO;
import mc.monacotelecom.services.dto.ServiceComponentDTO;
import mc.monacotelecom.services.entity.ServiceComponent;
import org.springframework.data.history.Revision;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;

import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class RevisionServiceComponentAssembler extends RepresentationModelAssemblerSupport<Revision<Integer, ServiceComponent>, RevisionDTO<ServiceComponentDTO>> {

    private final ServiceComponentResourceAssembler serviceComponentResourceAssembler;
    private final ZoneId zoneId;

    private static class RevisionServiceComponentDTO extends RevisionDTO<ServiceComponentDTO> {}

    private RevisionServiceComponentAssembler(Class<?> controllerCLass, ZoneId zoneId) {
        super(controllerCLass, (Class<RevisionDTO<ServiceComponentDTO>>) RevisionServiceComponentDTO.class.getSuperclass());
        this.serviceComponentResourceAssembler = ServiceComponentResourceAssembler.of(controllerCLass);
        this.zoneId = zoneId;
    }

    public static RevisionServiceComponentAssembler of(Class<?> controllerClass, ZoneId zoneId) {
        return new RevisionServiceComponentAssembler(controllerClass, zoneId);
    }

    @Override
    public RevisionDTO<ServiceComponentDTO> toModel(Revision<Integer, ServiceComponent> revision) {
        RevisionDTO<ServiceComponentDTO> revisionServiceComponentDTO = new RevisionDTO<>();
        revisionServiceComponentDTO.setDate(revision.getRequiredRevisionInstant().atZone(zoneId).toLocalDateTime().truncatedTo(ChronoUnit.SECONDS));
        revisionServiceComponentDTO.setEntity(serviceComponentResourceAssembler.toModel(revision.getEntity()));
        revisionServiceComponentDTO.setAuthor(((AuditEnversInfo)revision.getMetadata().getDelegate()).getUserId());
        return revisionServiceComponentDTO;
    }
}
