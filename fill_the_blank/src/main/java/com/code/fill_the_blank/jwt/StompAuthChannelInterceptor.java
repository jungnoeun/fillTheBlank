package com.code.fill_the_blank.jwt;

import java.util.Map;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import com.code.fill_the_blank.repository.AccessTokenBlacklistRepository;
import com.code.fill_the_blank.repository.ChatRepositry;

@Component
public class StompAuthChannelInterceptor implements ChannelInterceptor{ //STOMP 메시지가 서버의 메시지 처리 채널을 통과할 때 가로채서 검사할 수 있는 인터페이스
	private final ChatRepositry chatRepositry;
	private final JwtTokenProvider jwtTokenProvider;
	private final AccessTokenBlacklistRepository accessTokenBlacklistRepository;
	
	public StompAuthChannelInterceptor(ChatRepositry chatRepositry, JwtTokenProvider jwtTokenProvider, AccessTokenBlacklistRepository accessTokenBlacklistRepository) {
		this.chatRepositry = chatRepositry;
		this.jwtTokenProvider = jwtTokenProvider;
		this.accessTokenBlacklistRepository = accessTokenBlacklistRepository;
	}
	
	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) { // 메시지가 처리되기 전에 호출
		
		StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
		
		if(accessor == null || accessor.getCommand() == null) {
			return message;
		}
		
		StompCommand command = accessor.getCommand();
		
		if(StompCommand.CONNECT.equals(command)) {
			checkLogin(accessor);
		}
		
		if(StompCommand.SUBSCRIBE.equals(command)) {
				
			Long memberId = checkLogin(accessor);			
			String destination = accessor.getDestination();
		
			//채팅창 구독 권한 확인
			String roomId = extractRoomId(destination, "/topic/chat/rooms/");
			
			checkRoomMember(accessor, roomId);
		}
		
		if(StompCommand.SEND.equals(command)) {
			checkLogin(accessor);
			
			String destination = accessor.getDestination();
			
			//채팅창 메시지 전송 권한 확인
			String roomId = extractRoomId(destination, "/app/chat/rooms/");
			checkRoomMember(accessor, roomId);
			
		}
		
		
		return message;
	}

	
	// 채팅방 참여 여부 확인
	public void checkRoomMember(StompHeaderAccessor accessor, String roomId) {
		Long memberId = checkLogin(accessor);
	    
	    boolean isMember = chatRepositry.isRoomMember(roomId, memberId);
	    
		if(!chatRepositry.isRoomMember(roomId, memberId)) {
			throw new IllegalStateException("해당 채팅방에 참여한 사용자만 접근할 수 있습니다.");
		}
		
	}
	
	// WebSocket 핸드쉐이크에서 저장한 회원인지 ID로 확인
	private Long checkLogin(StompHeaderAccessor accessor) {
		
		Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
		
		if(sessionAttributes == null) {
			throw new IllegalStateException("WebSocket 세션 정보가 없습니다.");
		}
		
		
		Object memberIdValue = sessionAttributes.get("memberId");
		Object accessTokenValue = sessionAttributes.get("accessToken");
		
		
		if(!(memberIdValue instanceof Number memberIdNumber)) {
			throw new IllegalStateException("인증되지 않은 사용자입니다.");
		}
		
		if(!(accessTokenValue instanceof String accessToken)) {
			throw new IllegalStateException("Access Token 정보가 없습니다.");
		}
		
		boolean valid = jwtTokenProvider.validateToken(accessToken);

		boolean blacklisted = accessTokenBlacklistRepository.existsByToken(accessToken);

		if(!valid) {
			throw new IllegalStateException("Access Token이 만료되었거나 유효하지 않습니다.");
		}
		if(blacklisted) {
			throw new IllegalStateException("로그아웃된 사용자입니다.");
		}
		
		// JWT 만료 / 위조 검사 -> 로그인 후에도 WebSocket 연결이 되지 않도록
		if(!jwtTokenProvider.validateToken(accessToken)) {
			throw new IllegalStateException("Access Token이 만료되었거나 유효하지 않습니다.");
		}
		
		// 로그아웃으로 폐기된 토큰 검사
		if(accessTokenBlacklistRepository.existsByToken(accessToken)) {
			throw new IllegalStateException("로그아웃된 사용자입니다.");
		}
		
		return memberIdNumber.longValue();
	}
	
	// 목적지 주소에서 roomId 추출 -> /topic/chat/rooms/를 제거하고 roomId만 추출
	private String extractRoomId(String destination, String prefix) {
		
		if(destination == null || !destination.startsWith(prefix)) {
			throw new IllegalArgumentException("허용되지 않은 채팅경로입니다.");
		}
		
		String roomId = destination.substring(prefix.length());
		
		if(roomId.isBlank() || roomId.contains("/")) {
			throw new IllegalArgumentException("올바르지 않은 채팅방 ID입니다.");
		}
		
		return roomId;
	}

}




