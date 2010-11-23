package mx.itesm.todolistbus.domain;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.roo.addon.entity.RooEntity;
import javax.persistence.Entity;
import mx.itesm.todolistbus.domain.ToolName;
import javax.persistence.Enumerated;

@RooJavaBean
@RooToString
@RooEntity
@Entity
public class Tool {

    @Enumerated
    private ToolName name;

    private String description;

    private String ipAddress;

    private String port;

    private String contextPath;
}
