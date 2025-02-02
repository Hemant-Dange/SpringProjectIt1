package com.pgms.pgmanage.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

	@GetMapping("/")
	public String getIndex() {
		return "index";
	}

	@GetMapping("/contactus")
	public String getMethodName() {
		return "aboutUs";
	}

	@GetMapping("/login")
	public String getLoginPage() {
		return "login";
	}

}
