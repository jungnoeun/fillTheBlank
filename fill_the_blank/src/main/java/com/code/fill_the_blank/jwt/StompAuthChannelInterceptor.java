package com.code.fill_the_blank.jwt;

import java.util.Map;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import com.code.fill_the_blank.repository.ChatRepositry;

@Component
public class StompAuthChannelInterceptor implements ChannelInterceptor{ //STOMP 메시지가 서버의 메시지 처리 채널을 통과할 때 가로채서 검사할 수 있는 인터페이스
	private final ChatRepositry chatRepositry;
	
	public StompAuthChannelInterceptor(ChatRepositry chatRepositry) {
		this.chatRepositry = chatRepositry;
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
			//
			System.out.println("[STOMP] SUBSCRIBE 요청 수신");
			
			Long memberId = checkLogin(accessor);
			System.out.println("[STOMP] 회원 ID: " + memberId);
			
			String destination = accessor.getDestination();
			System.out.println("[STOMP] 구독 경로: " + destination);
			
			//채팅창 구독 권한 확인
			String roomId = extractRoomId(destination, "/topic/chat/rooms/");
			System.out.println("[STOMP] 채팅방 ID: " + roomId);
			
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
		
		System.out.println("[STOMP] 권한 검사 시작");
	    System.out.println("[STOMP] 검사 roomId: " + roomId);
	    System.out.println("[STOMP] 검사 memberId: " + memberId);
	    
	    boolean isMember = chatRepositry.isRoomMember(roomId, memberId);
	    
	    System.out.println("[STOMP] isRoomMember 결과: " + isMember);
		
		if(!chatRepositry.isRoomMember(roomId, memberId)) {
			throw new IllegalStateException("해당 채팅방에 참여한 사용자만 접근할 수 있습니다.");
		}
		
		System.out.println("[STOMP] 권한 검사 통과");
		
	}
	
	// WebSocket 핸드쉐이크에서 저장한 회원인지 ID로 확인
	private Long checkLogin(StompHeaderAccessor accessor) {
		
		Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
		
		if(sessionAttributes == null) {
			throw new IllegalStateException("WebSocket 세션 정보가 없습니다.");
		}
		
		Object memberId = sessionAttributes.get("memberId");
		
		if(!(memberId instanceof Long)) {
			throw new IllegalStateException("인증되지 않은 사용자입니다.");
		}
		
		return (Long) memberId;
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




