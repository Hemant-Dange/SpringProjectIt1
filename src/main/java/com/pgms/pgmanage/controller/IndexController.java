package com.pgms.pgmanage.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    @GetMapping("/")
    public String getIndex() {
        logger.info("Accessed Home Page");
        return "index";
    }

    @GetMapping("/contactus")
    public String getContactUs() {
        logger.info("Accessed Contact Us Page");
        return "aboutUs";
    }

    @GetMapping("/login")
    public String getLoginPage() {
        logger.info("Accessed Login Page");
        return "login";
    }
}
