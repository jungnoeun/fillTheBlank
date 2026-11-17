package com.code.fill_the_blank.controller;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.code.fill_the_blank.repository.IdleSpaceRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class ChatPageController {
	
	private final IdleSpaceRepository idleSpaceRepository;
	
	public ChatPageController(IdleSpaceRepository idleSpaceRepository) {
		this.idleSpaceRepository = idleSpaceRepository;
	}
	
	@GetMapping("/chat/{idleId}") // 채팅 페이지로 
	public String chatPage(@PathVariable Long idleId, HttpSession session, Model model) {
		
		Object loginMember = session.getAttribute("loginMember");
		
		if(loginMember == null) {
			return "redirect:/login";
		}
		
		try {
			String facilityName = idleSpaceRepository.findFacilityNameById(idleId);
			
			model.addAttribute("idleId",idleId);
			model.addAttribute("facilityName", facilityName);
			
			return "chat";
			
		}catch (EmptyResultDataAccessException e) {
			return "redirect:/idle-space-list";
		}
		
	}
	
	

}
