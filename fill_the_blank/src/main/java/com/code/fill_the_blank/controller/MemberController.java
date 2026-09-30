package com.code.fill_the_blank.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.code.fill_the_blank.dto.Member;
import com.code.fill_the_blank.service.MemberService;

import jakarta.servlet.http.HttpSession;

@Controller
public class MemberController {
	private final MemberService memberService;
	
	public MemberController(MemberService memberService) {
		this.memberService = memberService;
	}
	
	@GetMapping("/signup")
	public String signupPage() {
		return "idleSpace/signup";
	}
	
	@PostMapping("/signup")
	public String signup(Member member) {
		memberService.signup(member);
		return "redirect:/introduction";
	}
	
	@GetMapping("/login")
	public String loginPage() {
		return "idleSpace/login";
	}
	
	@PostMapping("/login")
	public String login(@RequestParam String username, @RequestParam String password, HttpSession session) {
		Member member = memberService.login(username, password);
		if(member == null) {
			return "redirect:/login?error=true";
		}
		session.setAttribute("loginMember", member);
		
		return "redirect:/introduction";
	}
	
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/introduction";
	}
}
