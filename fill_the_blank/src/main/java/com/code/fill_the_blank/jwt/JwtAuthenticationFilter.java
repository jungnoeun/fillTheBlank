package com.code.fill_the_blank.jwt;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component // spring bean으로 생성
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtTokenProvider jwtTokenProvider;
	
	public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
		this.jwtTokenProvider = jwtTokenProvider;
	}
	

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String accessToken = getAccessToken(request);
		
		// jwt가 없으면 일단 요청을 계속 진행 - 로그인 하지 않은 사용자도 홈페이지 입장 가능
		if(accessToken == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("text/plain;charset=UTF-8");
			response.getWriter().write("Access Token이 없습니다");
			return;
		}
		
		// access token 검증
		if(!jwtTokenProvider.validateToken(accessToken)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("text/plain;charset=UTF-8");
			response.getWriter().write("유효하지 않거나 만료된 JWT입니다.");
			return;
		}
		
		// jwt가 정상이라면 요청 계속 진행
		filterChain.doFilter(request, response);
		
	}
	
	public String getAccessToken(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		
		if(cookies == null) {
			return null;
		}
		
		for(Cookie cookie: cookies) {
			if("accessToken".equals(cookie.getName())) {
				return cookie.getValue();
			}
		}
		
		return null;
	}

	// /token/refresh 요청은 Access Token 검증 필터를 건너뛰고 컨트롤러에서 별도로 refresh token을 검증
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getServletPath();
		
		// refresh token 재발급은 컨트롤러에서 별도로 검증
		if("/token/refresh".equals(path)) {
			return true;
		}
		
		return !path.startsWith("/recommendation/");
	}
}
