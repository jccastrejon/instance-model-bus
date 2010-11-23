package mx.itesm.todolistbus.web;

import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import mx.itesm.todolistbus.domain.Tool;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;

@RooWebScaffold(path = "tools", formBackingObject = Tool.class)
@RequestMapping("/tools")
@Controller
public class ToolController {
}
