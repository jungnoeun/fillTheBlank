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

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor{
	private final JwtTokenProvider jwtTokenProvider;
	
	public WebSocketHandshakeInterceptor(JwtTokenProvider jwtTokenProvider) {
		this.jwtTokenProvider = jwtTokenProvider;
	}
	
	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws Exception {
		
		if(!(request instanceof ServletServerHttpRequest servletRequest)) {
			response.setStatusCode(HttpStatus.UNAUTHORIZED);
			return false;
		}
		
		HttpServletRequest httpRequest = servletRequest.getServletRequest();
		String accessToekn = null;
		
		Cookie[] cookies = httpRequest.getCookies();
		
		if(cookies != null) {
			for(Cookie cookie : cookies) {
				if("accessToken".equals(cookie.getName())) {
					accessToekn = cookie.getValue();
					break;
				}
			}
		}
		
		if(accessToekn == null || !jwtTokenProvider.validateToken(accessToekn)) {
			response.setStatusCode(HttpStatus.UNAUTHORIZED);
			return false;
		}
		
		Long memberId = jwtTokenProvider.getMemeberIdFromToken(accessToekn);
		
		attributes.put("memberId", memberId); // 이후 STOMP 인증에서 사용할 회원 ID 저장
		
		return true;
	}
	
	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			@Nullable Exception exception) {
		// TODO Auto-generated method stub
		
	}
}
