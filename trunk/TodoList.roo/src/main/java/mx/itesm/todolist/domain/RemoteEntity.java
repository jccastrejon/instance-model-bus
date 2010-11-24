package mx.itesm.todolist.domain;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.roo.addon.entity.RooEntity;
import javax.persistence.Entity;
import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.format.annotation.DateTimeFormat;

@RooJavaBean
@RooToString
@Entity
@RooEntity(finders = { "findRemoteEntitysByImbId", "findRemoteEntitysByEntityId" })
public class RemoteEntity {

    private String entityName;

    private Long entityId;

    private Long imbId;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "S-")
    private Date validity;

    private String entity;
}
