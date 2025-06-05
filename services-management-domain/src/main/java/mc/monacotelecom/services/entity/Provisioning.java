package mc.monacotelecom.services.entity;

import mc.monacotelecom.inventory.common.importer.domain.entity.IEntity;

import java.io.Serializable;

public class Provisioning implements IEntity, Serializable {

    @Override
    public <T> T getInstance() {
        return null;
    }

    @Override
    public String getDatabaseId() {
        return null;
    }
}
