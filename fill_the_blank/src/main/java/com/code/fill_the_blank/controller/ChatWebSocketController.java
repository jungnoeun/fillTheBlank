package com.code.fill_the_blank.controller;

import java.util.Map;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.code.fill_the_blank.dto.ChatMessage;
import com.code.fill_the_blank.dto.ChatSendRequest;
import com.code.fill_the_blank.service.ChatService;

@Controller
public class ChatWebSocketController {
	private final ChatService chatService;
	private final SimpMessagingTemplate messagingTemplate;
	
	public ChatWebSocketController(ChatService chatService, SimpMessagingTemplate messagingTemplate) {
		this.chatService = chatService;
		this.messagingTemplate = messagingTemplate;
	}
	
	@MessageMapping("/chat/rooms/{roomId}")
	public void sendMessage(@DestinationVariable String roomId, ChatSendRequest request,SimpMessageHeaderAccessor accessor) {
		
		System.out.println("[CHAT] roomId: " + roomId);
		
		// 세션 정보 확인
		Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
		if(sessionAttributes == null) {
			throw new IllegalStateException("WebSocket 세션 정보가 없습니다.");
		}
		
		Object memberIdValue = sessionAttributes.get("memberId");
		
		if(!(memberIdValue instanceof Number memberIdNumber)) {
			throw new IllegalStateException("인증되지 않은 사용자입니다.");
		}
		
		Long memberId = memberIdNumber.longValue();
		
		// DB 저장
		ChatMessage savedMessage = chatService.sendMessage(roomId, memberId, request.getContent());
		
        System.out.println(
                "[CHAT] messageId: "
                + savedMessage.getMessageId()
        );

		
		// 같은 채팅방을 구독 중인 사람들에게 전달
		messagingTemplate.convertAndSend("/topic/chat/rooms/" + roomId, savedMessage);
		
		System.out.println("[CHAT] Broadcast 완료");
		
	}

}
