package com.code.fill_the_blank.controller;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.code.fill_the_blank.dto.Member;
import com.code.fill_the_blank.jwt.JwtTokenProvider;
import com.code.fill_the_blank.repository.AccessTokenBlacklistRepository;
import com.code.fill_the_blank.repository.RefreshTokenRepository;
import com.code.fill_the_blank.service.MemberService;
import com.code.fill_the_blank.websocket.WebSocketSessionRegistry;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class MemberController {
	private final MemberService memberService;
	private final JwtTokenProvider jwtTokenProvider;
	private final RefreshTokenRepository refreshTokenRepository;
	private final AccessTokenBlacklistRepository accessTokenBlacklistRepository;
	private final WebSocketSessionRegistry webSocketSessionRegistry;
	
	public MemberController(MemberService memberService, JwtTokenProvider jwtTokenProvider, RefreshTokenRepository refreshTokenRepository,
			AccessTokenBlacklistRepository accessTokenBlacklistRepository, WebSocketSessionRegistry webSocketSessionRegistry) {
		this.memberService = memberService;
		this.jwtTokenProvider = jwtTokenProvider;
		this.refreshTokenRepository = refreshTokenRepository;
		this.accessTokenBlacklistRepository = accessTokenBlacklistRepository;
		this.webSocketSessionRegistry = webSocketSessionRegistry;
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
		// Access Token 생성
		String accessToken = jwtTokenProvider.createAccessToken(member);
		// Refresh Toekn 생성
		String refreshToken = jwtTokenProvider.createRefreshToken(member);
		
		// Refresh Token을 DB에 저장
		refreshTokenRepository.save(member.getMemberId(), refreshToken, LocalDateTime.now().plusDays(7));
		
		// JWT를 쿠키에 저장
		// Access Token Cookie
		Cookie accessCookie = new Cookie("accessToken", accessToken);
		accessCookie.setHttpOnly(true); // js 통한 쿠키 접근 차단
		accessCookie.setPath("/"); // 쿠키 전송 범위
		accessCookie.setMaxAge(30*60); // 쿠키 수명 30분
		
		response.addCookie(accessCookie);
		
		// Refresh Token Cookie
		Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
		refreshCookie.setHttpOnly(true);
		refreshCookie.setPath("/");
		refreshCookie.setMaxAge(7*24*60*60);
		response.addCookie(refreshCookie);
		
		return "redirect:/introduction";
	}
	
	@GetMapping("/logout")
	public String logout(@CookieValue(value = "accessToken", required = false) String accessToken ,
			@CookieValue(value = "refreshToken", required = false) String refreshToken, HttpSession session, HttpServletResponse response) {
		
		// access token을 블랙리스트에 등록
		if(accessToken!=null && !accessToken.isBlank() && jwtTokenProvider.validateToken(accessToken)) {
			accessTokenBlacklistRepository.save(accessToken, jwtTokenProvider.getExpireationFromToken(accessToken));
			
			webSocketSessionRegistry.closeByAccessToken(accessToken); // 해당 accessToken으로 열린 WebSocket모두 종료
		}
		
		// 1. DB에 저장된 refresh token 삭제
		if(refreshToken != null && !refreshToken.isBlank()) {
			refreshTokenRepository.deleteByToken(refreshToken);
		}
		
		// 2. 세션 무효화
		session.invalidate();
		
		// 3. access token 쿠키 삭제
		Cookie accessCookie = new Cookie("accessToken", "");
		accessCookie.setHttpOnly(true);
		accessCookie.setPath("/");
		accessCookie.setMaxAge(0);
		response.addCookie(accessCookie);
		
		// 4. refresh token 쿠키 삭제 - 쿠키의 수명을 0으로 설정하여 브라우저에서 제거
		Cookie refreshCookie = new Cookie("refreshToken", "");
		refreshCookie.setHttpOnly(true);
		refreshCookie.setPath("/");
		refreshCookie.setMaxAge(0);
		response.addCookie(refreshCookie);
		
		return "redirect:/introduction";
	}
	
	@PostMapping("/token/refresh")
	@ResponseBody
	public ResponseEntity<String> refreshAccessToken(@CookieValue(value = "refreshToken",required = false) String refreshToken, HttpServletResponse response) {
		
		// 1. Refresh Token 쿠키 확인 - refresh token이 없으면 실패
		if(refreshToken == null || refreshToken.isBlank()) {
			return ResponseEntity
					.status(HttpStatus.UNAUTHORIZED)
					.body("Refresh Token이 없습니다.");
		}
		
		// 2. JWT 서명 및 만료 여부 확인 
		if(!jwtTokenProvider.validateToken(refreshToken)) {
			refreshTokenRepository.deleteByToken(refreshToken);
			
			return ResponseEntity
					.status(HttpStatus.UNAUTHORIZED)
					.body("Refresh Token이 만료되었거나 유효하지 않습니다.");
		}
		
		// 3. DB에 저장된 토큰인지 확인
		if(!refreshTokenRepository.existsByToken(refreshToken)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body("등록되지 않은 Refresh Token입니다.");
		}
		
		// 4. refresh token에서 username 추출
		String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
		
		// 5. 회원 조회
		Member member = memberService.findByUsername(username);
		if(member == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body("회원 정보를 찾을 수 없습니다.");
		}
		
		// 6. 새 Access Token 발급 및 쿠키 갱신
		String newAccessToken = jwtTokenProvider.createAccessToken(member);
		Cookie accessCookie = new Cookie("accessToken", newAccessToken);
		
		accessCookie.setHttpOnly(true);
		accessCookie.setPath("/");
		accessCookie.setMaxAge(30*60);
		response.addCookie(accessCookie);
		
		return ResponseEntity.ok("Access Token이 재발급되었습니다.");
				
	}
}
