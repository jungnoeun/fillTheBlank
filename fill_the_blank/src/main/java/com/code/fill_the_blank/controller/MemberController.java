package com.code.fill_the_blank.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.code.fill_the_blank.dto.Member;
import com.code.fill_the_blank.service.MemberService;

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
}
