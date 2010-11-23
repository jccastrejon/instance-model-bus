// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package mx.itesm.todolistbus.web;

import java.io.UnsupportedEncodingException;
import java.lang.Long;
import java.lang.String;
import java.util.Arrays;
import java.util.Collection;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import mx.itesm.todolistbus.domain.Tool;
import mx.itesm.todolistbus.domain.ToolName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

privileged aspect ToolController_Roo_Controller {
    
    @Autowired
    private GenericConversionService ToolController.conversionService;
    
    @RequestMapping(method = RequestMethod.POST)
    public String ToolController.create(@Valid Tool tool, BindingResult result, Model model, HttpServletRequest request) {
        if (result.hasErrors()) {
            model.addAttribute("tool", tool);
            return "tools/create";
        }
        tool.persist();
        return "redirect:/tools/" + encodeUrlPathSegment(tool.getId().toString(), request);
    }
    
    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String ToolController.createForm(Model model) {
        model.addAttribute("tool", new Tool());
        return "tools/create";
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String ToolController.show(@PathVariable("id") Long id, Model model) {
        model.addAttribute("tool", Tool.findTool(id));
        model.addAttribute("itemId", id);
        return "tools/show";
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String ToolController.list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model model) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            model.addAttribute("tools", Tool.findToolEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));
            float nrOfPages = (float) Tool.countTools() / sizeNo;
            model.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            model.addAttribute("tools", Tool.findAllTools());
        }
        return "tools/list";
    }
    
    @RequestMapping(method = RequestMethod.PUT)
    public String ToolController.update(@Valid Tool tool, BindingResult result, Model model, HttpServletRequest request) {
        if (result.hasErrors()) {
            model.addAttribute("tool", tool);
            return "tools/update";
        }
        tool.merge();
        return "redirect:/tools/" + encodeUrlPathSegment(tool.getId().toString(), request);
    }
    
    @RequestMapping(value = "/{id}", params = "form", method = RequestMethod.GET)
    public String ToolController.updateForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("tool", Tool.findTool(id));
        return "tools/update";
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public String ToolController.delete(@PathVariable("id") Long id, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model model) {
        Tool.findTool(id).remove();
        model.addAttribute("page", (page == null) ? "1" : page.toString());
        model.addAttribute("size", (size == null) ? "10" : size.toString());
        return "redirect:/tools?page=" + ((page == null) ? "1" : page.toString()) + "&size=" + ((size == null) ? "10" : size.toString());
    }
    
    @ModelAttribute("toolnames")
    public Collection<ToolName> ToolController.populateToolNames() {
        return Arrays.asList(ToolName.class.getEnumConstants());
    }
    
    Converter<Tool, String> ToolController.getToolConverter() {
        return new Converter<Tool, String>() {
            public String convert(Tool tool) {
                return new StringBuilder().append(tool.getDescription()).append(" ").append(tool.getIpAddress()).append(" ").append(tool.getPort()).toString();
            }
        };
    }
    
    @PostConstruct
    void ToolController.registerConverters() {
        conversionService.addConverter(getToolConverter());
    }
    
    private String ToolController.encodeUrlPathSegment(String pathSegment, HttpServletRequest request) {
        String enc = request.getCharacterEncoding();
        if (enc == null) {
            enc = WebUtils.DEFAULT_CHARACTER_ENCODING;
        }
        try {
            pathSegment = UriUtils.encodePathSegment(pathSegment, enc);
        }
        catch (UnsupportedEncodingException uee) {}
        return pathSegment;
    }
    
}
