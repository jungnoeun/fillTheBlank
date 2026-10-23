package com.code.fill_the_blank.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.code.fill_the_blank.dto.ChatMessage;
import com.code.fill_the_blank.dto.ChatRoom;
import com.code.fill_the_blank.jwt.JwtAuthenticationFilter;
import com.code.fill_the_blank.jwt.JwtTokenProvider;
import com.code.fill_the_blank.service.ChatService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
	
	private final ChatService chatService;
	private final JwtTokenProvider jwtTokenProvider;
	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	
	public ChatController(ChatService chatService, JwtTokenProvider jwtTokenProvider, JwtAuthenticationFilter jwtAuthenticationFilter)
	{
		this.chatService = chatService;
		this.jwtTokenProvider = jwtTokenProvider;
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
	}
	
	// 채팅방 조회 및 생성
	@PostMapping("/rooms/space/{idleId}")
	public ResponseEntity<ChatRoom> getOrCreateRoom(@PathVariable Long idleId) {
		ChatRoom chatRoom = chatService.getOrCreateRoom(idleId);
		return ResponseEntity.ok(chatRoom);
	}
	
	// 채팅방 참여
	@PostMapping("/rooms/{roomId}/join")
	public ResponseEntity<Map<String, String>> joinRoom(@PathVariable String roomId, HttpServletRequest request) {
		Long memberId = getLoginMemberId(request);
		chatService.joinRoom(roomId, memberId);
		return ResponseEntity.ok(Map.of("message", "채팅방 참여가 완료되었습니다."));
		
	}
	
	// 채팅방 나가기
    @PostMapping("/rooms/{roomId}/leave")
    public ResponseEntity<Map<String, String>> leaveRoom(@PathVariable String roomId, HttpServletRequest request) {
    	Long memberId = getLoginMemberId(request);
    	chatService.leaveRoom(roomId, memberId);
    	return ResponseEntity.ok(Map.of("message", "채팅방 나가기가 완료되었습니다."));
    }
    
    
    // 최근 메시지 조회
    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<List<ChatMessage>> getRecentMessage(@PathVariable String roomId, HttpServletRequest request) {
    	Long memberId = getLoginMemberId(request);
    	List<ChatMessage> messages = chatService.getRecentMessages(roomId, memberId);
    	return ResponseEntity.ok(messages);
    }
	
    // JWT에서 로그인한 회원 ID 추출
	private Long getLoginMemberId(HttpServletRequest request) {
		String accessToken = jwtAuthenticationFilter.getAccessToken(request);
		
		if(accessToken == null || !jwtTokenProvider.validateToken(accessToken)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
		}
		
		return jwtTokenProvider.getMemeberIdFromToken(accessToken);
	}
	
	@GetMapping("/test")
	public ResponseEntity<String> test() {
	    return ResponseEntity.ok("Chat Controller 정상 연결");
	}

}
