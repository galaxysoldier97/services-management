package mc.monacotelecom.services.projections;

import mc.monacotelecom.inventory.common.recycling.interfaces.BaseEntityProjection;
import mc.monacotelecom.services.enums.Status;

public interface ServiceLastStatusChangeProjection extends BaseEntityProjection<Status>  {
    String getCrmServiceId();

    @Override
    Status getStatus();

    Long getId();

    String getNumber();

    default String getIdentifier(){
        return String.valueOf(getId());
    }
}
