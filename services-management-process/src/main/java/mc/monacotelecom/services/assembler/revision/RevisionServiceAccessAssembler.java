package mc.monacotelecom.services.assembler.revision;

import mc.monacotelecom.inventory.common.audit.AuditEnversInfo;
import mc.monacotelecom.services.assembler.ServiceAccessResourceAssembler;
import mc.monacotelecom.services.dto.RevisionDTO;
import mc.monacotelecom.services.dto.ServiceAccessDTO;
import mc.monacotelecom.services.entity.ServiceAccess;
import org.springframework.data.history.Revision;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;

import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class RevisionServiceAccessAssembler extends RepresentationModelAssemblerSupport<Revision<Integer, ServiceAccess>, RevisionDTO<ServiceAccessDTO>> {

    private final ServiceAccessResourceAssembler serviceAccessResourceAssembler;
    private final ZoneId zoneId;

    private static class RevisionServiceAccessDTO extends RevisionDTO<ServiceAccessDTO> {}

    private RevisionServiceAccessAssembler(Class<?> controllerClass, ZoneId zoneId) {
        super(controllerClass, (Class<RevisionDTO<ServiceAccessDTO>>) RevisionServiceAccessDTO.class.getSuperclass());
        this.serviceAccessResourceAssembler = ServiceAccessResourceAssembler.of(controllerClass);
        this.zoneId = zoneId;
    }

    public static RevisionServiceAccessAssembler of(Class<?> controllerClass, ZoneId zoneId) {
        return new RevisionServiceAccessAssembler(controllerClass, zoneId);
    }

    @Override
    public RevisionDTO<ServiceAccessDTO> toModel(Revision<Integer, ServiceAccess> revision) {
        RevisionDTO<ServiceAccessDTO> revisionServiceAccessDTO = new RevisionDTO<>();
        revisionServiceAccessDTO.setDate(revision.getRequiredRevisionInstant().atZone(zoneId).toLocalDateTime().truncatedTo(ChronoUnit.SECONDS));
        revisionServiceAccessDTO.setEntity(serviceAccessResourceAssembler.toModel(revision.getEntity()));
        revisionServiceAccessDTO.setAuthor(((AuditEnversInfo)revision.getMetadata().getDelegate()).getUserId());
        return revisionServiceAccessDTO;
    }
}