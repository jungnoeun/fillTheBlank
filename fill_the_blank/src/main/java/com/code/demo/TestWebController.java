package com.code.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/test")
public class TestWebController {
	
	@GetMapping("/hello")
	public String hello() {
		return "hello";
	}
}
