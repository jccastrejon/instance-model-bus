// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package mx.itesm.ecore.domain;

import java.lang.Long;
import java.lang.String;
import mx.itesm.ecore.domain.ImbNotificationType;

privileged aspect ImbNotification_Roo_JavaBean {
    
    public String ImbNotification.getUrl() {
        return this.url;
    }
    
    public void ImbNotification.setUrl(String url) {
        this.url = url;
    }
    
    public String ImbNotification.getEntity() {
        return this.entity;
    }
    
    public void ImbNotification.setEntity(String entity) {
        this.entity = entity;
    }
    
    public String ImbNotification.getEntityName() {
        return this.entityName;
    }
    
    public void ImbNotification.setEntityName(String entityName) {
        this.entityName = entityName;
    }
    
    public String ImbNotification.getReturnTypeClass() {
        return this.returnTypeClass;
    }
    
    public void ImbNotification.setReturnTypeClass(String returnTypeClass) {
        this.returnTypeClass = returnTypeClass;
    }
    
    public Long ImbNotification.getEntityId() {
        return this.entityId;
    }
    
    public void ImbNotification.setEntityId(Long entityId) {
        this.entityId = entityId;
    }
    
    public ImbNotificationType ImbNotification.getType() {
        return this.type;
    }
    
    public void ImbNotification.setType(ImbNotificationType type) {
        this.type = type;
    }
    
}
