package com.code.fill_the_blank.jwt;

import java.util.Map;

import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.code.fill_the_blank.repository.AccessTokenBlacklistRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor{
	private final JwtTokenProvider jwtTokenProvider;
	private final AccessTokenBlacklistRepository accessTokenBlacklistRepository;
	
	public WebSocketHandshakeInterceptor(JwtTokenProvider jwtTokenProvider, AccessTokenBlacklistRepository accessTokenBlacklistRepository) {
		this.jwtTokenProvider = jwtTokenProvider;
		this.accessTokenBlacklistRepository = accessTokenBlacklistRepository;
	}
	
	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws Exception {
		
		if(!(request instanceof ServletServerHttpRequest servletRequest)) {
			response.setStatusCode(HttpStatus.UNAUTHORIZED);
			return false;
		}
		
		HttpServletRequest httpRequest = servletRequest.getServletRequest();
		String accessToken = null;
		
		Cookie[] cookies = httpRequest.getCookies();
		
		if(cookies != null) {
			for(Cookie cookie : cookies) {
				if("accessToken".equals(cookie.getName())) {
					accessToken = cookie.getValue();
					break;
				}
			}
		}
		
		if(accessToken == null || !jwtTokenProvider.validateToken(accessToken) || accessTokenBlacklistRepository.existsByToken(accessToken)) {
			response.setStatusCode(HttpStatus.UNAUTHORIZED);
			return false;
		}
		
		Long memberId = jwtTokenProvider.getMemeberIdFromToken(accessToken);
		
		attributes.put("memberId", memberId); // 이후 STOMP 인증에서 사용할 회원 ID 저장
		attributes.put("accessToken", accessToken); // 웹 소켓 세션에 토큰 자체를 저장
		
		return true;
	}
	
	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			@Nullable Exception exception) {
		// TODO Auto-generated method stub
		
	}
}
