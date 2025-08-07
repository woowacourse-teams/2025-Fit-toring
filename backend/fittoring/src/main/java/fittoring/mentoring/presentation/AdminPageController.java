package fittoring.mentoring.presentation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminPageController {

    @GetMapping({"/web-admin", "/web-admin/"})
    public String redirectRoot() {
        return "forward:/web-admin/index.html";
    }

    @GetMapping("/web-admin/**/{path:[^\\.]*}")
    public String forward() {
        return "forward:/web-admin/index.html";
    }
}
