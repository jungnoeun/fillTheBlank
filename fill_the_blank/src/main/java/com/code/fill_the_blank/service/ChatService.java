package com.code.fill_the_blank.service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.code.fill_the_blank.dto.ChatMessage;
import com.code.fill_the_blank.dto.ChatRoom;
import com.code.fill_the_blank.repository.ChatRepositry;

@Service
public class ChatService {
	private final ChatRepositry chatRepositry;
	
	public ChatService(ChatRepositry chatRepositry) {
		this.chatRepositry = chatRepositry;
	}
	
	// 유휴공간별 채팅방 조회 및 생성 - 처음 생성하거나 이미 있는 방에 들어가거나
	public ChatRoom getOrCreateRoom(Long idleId) {
		
		try {
			return chatRepositry.findRoomByIdleId(idleId);
		} catch (EmptyResultDataAccessException e) {
			String roomId = UUID.randomUUID().toString();
			
			try {
				chatRepositry.createRoom(roomId, idleId);
			} catch (DataIntegrityViolationException e2) {
				// 동시 요청으로 다른 사용자가 채팅방을 먼저 생성했을 수도 있음
			}
			
			return chatRepositry.findRoomByIdleId(idleId);
		}
	}
	
	// 채팅방 참여
	@Transactional
	public void joinRoom(String roomId, Long memberId) {
		
		// 이미 참여중이라면 중복 처리하지 않음
		if(chatRepositry.isRoomMember(roomId, memberId)) {
			return;
		}
		
		// 기존에 참여했다가 나간 회원인지 확인
		int updated = chatRepositry.rejoinRoom(roomId, memberId);
		
		if(updated > 0) {
			return;
		}
		
		// 처음 참여했다가 나간 회원
		try {
			chatRepositry.joinRoom(roomId, memberId);
		} catch (DataIntegrityViolationException e) {
			// 동시 참여 요청으로 이미 등록됐는지 확인
			if(!chatRepositry.isRoomMember(roomId, memberId)) {
				throw e;
			}
		}
	}
	
	// 채팅방 나가기
	@Transactional
	public void leaveRoom(String roomId, Long memberId) {
		chatRepositry.leaveRoom(roomId, memberId);
	}
	
	// 채팅 메시지 저장
	@Transactional
	public ChatMessage sendMessage(String roomId, Long memberId, String content) {
		
	    System.out.println("[SERVICE] roomId: " + roomId);
		
		// 채팅방 참여 여부 확인
		if(!chatRepositry.isRoomMember(roomId, memberId)) {
			throw new IllegalStateException("채팅방에 참여한 사용자만 메시지를 보낼 수 있습니다.");
		}
		
		// 빈 메시지 방지
		if(content == null || content.isBlank()) {
			throw new IllegalArgumentException("메시지를 입력해주세요.");
		}
		
		if(content.length() > 2000) {
			throw new IllegalArgumentException("메시지는 2000자를 초과할 수 없습니다.");
		}
		
		ChatMessage message = new ChatMessage();
		message.setMessageId(UUID.randomUUID().toString());
		message.setRoomId(roomId);
		message.setSenderId(memberId);
		message.setContent(content.trim());
		
		chatRepositry.saveMessage(message);
		
		// 저장된 메시지를 다시 조회해서 senderName, createAt까지 포함해서 반환
		return chatRepositry.findMessageById(message.getMessageId());
	}
	
	@Transactional(readOnly = true)
	public List<ChatMessage> getRecentMessages(String roomId, Long memberId) {
		
		if(!chatRepositry.isRoomMember(roomId, memberId)) {
			throw new IllegalStateException("채팅방에 참여한 사용자만 메시지를 조회할 수 있습니다.");
		}
		
		List<ChatMessage> messages = chatRepositry.findRecentMessages(roomId);
		
		// 최신순으로 조회된 메시지를 시간순으로 변경 - DB에서는 최신순으로 조회해왔지만 화면에는 오래된 메시지부터 표시하도록 변경함
		Collections.reverse(messages);
		
		return messages;
	}
}

