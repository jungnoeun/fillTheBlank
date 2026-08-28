package com.code.fill_the_blank.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.code.fill_the_blank.dto.IdleSpace;
import com.code.fill_the_blank.service.IdleSpaceService;

@Controller
public class IdleSpaceController {
	
	private final IdleSpaceService service;
	
	public IdleSpaceController(IdleSpaceService service) {
		this.service = service;
	}

	@GetMapping("/idle-space-list")
	public String listIdleSpace(Model model) {
		List<IdleSpace> idleSpaces = service.findAll();
		
		model.addAttribute("idleSpaces", idleSpaces);
		return "idleSpace/list";
	}
	
	
}
