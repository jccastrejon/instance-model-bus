package mx.itesm.ecore.web;

import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import mx.itesm.ecore.domain.ImbNotification;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;

@RooWebScaffold(path = "imbnotifications", formBackingObject = ImbNotification.class)
@RequestMapping("/imbnotifications")
@Controller
public class ImbNotificationController {
}
