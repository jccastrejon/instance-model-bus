package mx.itesm.todolistbus.domain;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.roo.addon.entity.RooEntity;
import javax.persistence.Entity;
import mx.itesm.todolistbus.domain.ImbNotificationType;
import javax.persistence.Enumerated;

@RooJavaBean
@RooToString
@RooEntity
@Entity
public class ImbNotification {

    private String url;

    private String entity;

    private String entityName;

    private String returnTypeClass;

    private Long entityId;

    @Enumerated
    private ImbNotificationType type;
}
