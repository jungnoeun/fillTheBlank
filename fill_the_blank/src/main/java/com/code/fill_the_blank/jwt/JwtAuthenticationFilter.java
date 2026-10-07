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
			filterChain.doFilter(request, response);
			return;
		}
		
		// jwt가 있으면 검증
		if(!jwtTokenProvider.validateToken(accessToken)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("text/plain;charset=UTF-8");
			response.getWriter().write("유효하거나 만료된 JWT입니다.");
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

}
