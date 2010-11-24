package mx.itesm.todolist.web;

import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import mx.itesm.todolist.domain.Item;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;

@RooWebScaffold(path = "items", formBackingObject = Item.class)
@RequestMapping("/items")
@Controller
public class ItemController {
}
