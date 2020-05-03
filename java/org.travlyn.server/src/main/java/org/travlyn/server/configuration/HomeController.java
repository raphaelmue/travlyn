package org.travlyn.server.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.travlyn.server.service.TravlynService;

/**
 * Home redirection to swagger api documentation
 */
@Controller
public class HomeController {
    private final Logger logger = LoggerFactory.getLogger(HomeController.class);
    @GetMapping(value = "/")
    public String index() {
        logger.info("Swagger-UI accessed.");
        return "redirect:swagger-ui.html";
    }
}
