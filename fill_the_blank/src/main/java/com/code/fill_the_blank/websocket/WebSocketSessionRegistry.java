package com.code.fill_the_blank.websocket;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import com.code.fill_the_blank.jwt.JwtTokenProvider;

@Component
public class WebSocketSessionRegistry {

	private static final Logger log = LoggerFactory.getLogger(WebSocketSessionRegistry.class);
	private final Map<String, Set<WebSocketSession>> sessionsByAccessToken = new ConcurrentHashMap<>();
	private final Map<String, ScheduledFuture<?>> expirationTasksByAccessToken = new ConcurrentHashMap<>();
	private final JwtTokenProvider jwtTokenProvider;
	private final TaskScheduler taskScheduler;
	private static final CloseStatus TOKEN_EXPIRED = new CloseStatus(4001, "TOKEN_EXPIRED");
	private static final CloseStatus LOGGED_OUT = new CloseStatus(4002, "LOGGED_OUT");
	
	public WebSocketSessionRegistry(JwtTokenProvider jwtTokenProvider, @Qualifier("websocketTaskScheduler") TaskScheduler taskScheduler) {
		this.jwtTokenProvider = jwtTokenProvider;
		this.taskScheduler = taskScheduler;
	}
	
	public void register(WebSocketSession session) {
		Object tokenValue = session.getAttributes().get("accessToken");
		
		if(!(tokenValue instanceof String accessToken) || accessToken.isBlank()) {
			return;
		}
		
		// websocket 세션 등록
		sessionsByAccessToken.computeIfAbsent(accessToken, key -> ConcurrentHashMap.newKeySet()).add(session);
		// 해당 토큰의 만료 작업이 없다면 등록
		expirationTasksByAccessToken.computeIfAbsent(accessToken, this::scheduleExpiration);
		
	}
	
	private ScheduledFuture<?> scheduleExpiration(String accessToken) {
		Instant expirationTime = jwtTokenProvider.getExpirationInsatnt(accessToken);
		
		return taskScheduler.schedule(
				() -> {
					log.info("JWT 만료로 WebSocket 연결 종료");
					closeByAccessToken(accessToken, TOKEN_EXPIRED);
				}
				, expirationTime);
	}
	
	public void remove(WebSocketSession session) {
		Object tokenValue = session.getAttributes().get("accessToken");
		
		if(!(tokenValue instanceof String accessToken)) {
			return;
		}
		
		Set<WebSocketSession> sessions = sessionsByAccessToken.get(accessToken);
		
		if(sessions == null) {
			return;
		}
		
		sessions.remove(session);
		
		if(sessions.isEmpty()) { // 이 accessToken으로 연결된 WebSocket이 더이상 없다면 예약 작업도 제거
			sessionsByAccessToken.remove(accessToken);
			cancelExpirationTask(accessToken);
		}
		
	}
	
	public void closeByAccessToken(String accessToken) {
		closeByAccessToken(accessToken, LOGGED_OUT);
	}
	
	private void closeByAccessToken(String accessToken, CloseStatus closeStatus) {
		if(accessToken == null || accessToken.isBlank()) {
			return;
		}
		
		Set<WebSocketSession> sessions = sessionsByAccessToken.remove(accessToken);
		cancelExpirationTask(accessToken);
		
		if(sessions == null) {
			return;
		}
		
		for(WebSocketSession session : sessions) {
			try {
				if(session.isOpen()) {
					session.close(closeStatus);
				}
			}catch (IOException e) {
				log.warn("WebSocket 세션 종료 실패 - sessionId={}", session.getId(), e);
			}
		}
		
	}
	
	
	
	private void cancelExpirationTask(String accessToken) {
		ScheduledFuture<?> task = expirationTasksByAccessToken.remove(accessToken);
		if(task != null && !task.isDone()) {
			task.cancel(false);
		}
	}
	
	
}
