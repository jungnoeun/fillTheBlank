package com.code.fill_the_blank.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.code.fill_the_blank.dto.Member;
import com.code.fill_the_blank.jwt.JwtTokenProvider;
import com.code.fill_the_blank.service.MemberService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class MemberController {
	private final MemberService memberService;
	private final JwtTokenProvider jwtTokenProvider;
	
	public MemberController(MemberService memberService, JwtTokenProvider jwtTokenProvider) {
		this.memberService = memberService;
		this.jwtTokenProvider = jwtTokenProvider;
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
	public String login(@RequestParam String username, @RequestParam String password, HttpSession session, HttpServletResponse response) {
		Member member = memberService.login(username, password);
		if(member == null) {
			return "redirect:/login?error=true";
		}
		// 기존 세션 로그인
		session.setAttribute("loginMember", member);
		
		// JWT 생성
		String accessToken = jwtTokenProvider.createAccessToken(member);
		
		// JWT를 쿠키에 저장
		Cookie cookie = new Cookie("accessToken", accessToken);
		cookie.setHttpOnly(true); // js 통한 쿠키 접근 차단
		cookie.setPath("/"); // 쿠키 전송 범위
		cookie.setMaxAge(30*60); // 쿠키 수명 30분
		
		response.addCookie(cookie);
		
		return "redirect:/introduction";
	}
	
	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/introduction";
	}
}
