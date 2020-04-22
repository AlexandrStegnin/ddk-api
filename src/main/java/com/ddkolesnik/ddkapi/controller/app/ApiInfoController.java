package com.ddkolesnik.ddkapi.controller.app;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import static com.ddkolesnik.ddkapi.util.Constant.API_INFO_URL;
import static com.ddkolesnik.ddkapi.util.Constant.BASE_URL;

/**
 * @author Alexandr Stegnin
 */

@Controller
public class ApiInfoController {

    @GetMapping(value = {BASE_URL, API_INFO_URL})
    public String apiInfoPage() {
        return "redirect:/api-info.html";
    }

}
