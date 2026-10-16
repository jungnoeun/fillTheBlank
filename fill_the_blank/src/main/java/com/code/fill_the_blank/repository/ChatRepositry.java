package com.code.fill_the_blank.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.code.fill_the_blank.dto.ChatMessage;
import com.code.fill_the_blank.dto.ChatRoom;

@Repository
public class ChatRepositry {
	private final JdbcTemplate jdbcTemplate;
	
	public ChatRepositry(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	// 유휴공간에 연결된 채팅방 조회
	public ChatRoom findRoomByIdleId(Long idleId) {
		String sql = """
				SELECT * room_id, idle_id, created_at
				FROM CHATROOM
				WHERE idle_id = ?
				""";
		
		return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
			ChatRoom room = new ChatRoom();
			room.setRoomId(rs.getString("room_id"));
			room.setIdleId(rs.getLong("idle_id"));
			room.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
			
			return room;
		}, idleId);
	}
	
	// 채팅방 생성
	public int createRoom(String roomId, Long IdleId) {
		String sql = """
				INSERT INTO chat_room(
					room_id, idle_id
				) VALUES (?,?)
				""";
		
		return jdbcTemplate.update(sql, roomId, IdleId);
	}
	
	// 채팅방 참여
	public int joinRoom(String roomId, Long memberId) {
		String sql = """
				INSERT INTO chat_room_member (
					room_id, member_id
				) VALUES (?,?)
				""";
		
		return jdbcTemplate.update(sql, roomId, memberId);
	}
	
	// 채팅방 재참여
	public int rejoinRoom(String roomId, Long memberId) {
		String sql = """
				UPDATE chat_room_member
				SET joined_at = CURRENT_TIMESTAMP
					left_at = NULL
				WHERE room_id = ?
				AND member_id = ?
				AND left_at IS NOT NULL
				""";
		
		return jdbcTemplate.update(sql, roomId, memberId);
	}
	
	// 현재 참여 여부 확인
	public boolean isRoomMember(String roomId, Long memberId) {
		String sql = """
				SELECT COUNT(*)
				FROM chat_room_member
				WHERE room_id = ?
				AND left_at IS NULL
				""";
		
		Integer count = jdbcTemplate.queryForObject(sql, Integer.class, roomId, memberId);
		
		return count != null && count > 0;
	}
	
	// 채팅방 나가기
	public int leaveRoom(String roomId, Long memberId) {
		String sql = """
				UPDATE chat_room_member
				SET left_at = CURRENT_TIMESTAMP
				WHERE room_id = ?
				AND member_id = ?
				AND left_at IS NULL
				""";
		return jdbcTemplate.update(sql, roomId, memberId);
	}
	
	// 메시지 저장
	public int saveMessage(ChatMessage message) {
		String sql = """
				INSERT INTO chat_message (
					message_id,
					room_id,
					sender_id,
					content
				) VALUES (?,?,?,?)
				""";
		
		return jdbcTemplate.update(sql, message.getMessageId(), message.getRoomId(), message.getSenderId(), message.getContent());
	}
	
	// 최근 메시지 30개 조회
	public List<ChatMessage> findRecentMessages(String roomId) {
		String sql = """
				SELECT
					cm.message_id,
					cm.room_id,
					cm.sender_id,
					m.name AS sender_name,
					cm.content,
					cm.created_at
				FROM chat_message cm
				JOIN member m
					ON cm.sender_id = m.member_id
				WHERE cm.room_id = ?
				ORDER BY cm.created_at DESC, cm.message_id DESC
				FETCH FIRST 30 ROWS ONLY
				""";
		
		return jdbcTemplate.query(sql,  (rs, rowNum) ->{
			ChatMessage message = new ChatMessage();
			message.setMessageId(rs.getString("message_id"));
			message.setRoomId(rs.getString("room_id"));
			message.setSenderId(rs.getLong("sender_id"));
			message.setSenderName(rs.getString("sender_name"));
			message.setContent(rs.getString("content"));
			message.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
			
			return message;
		}, roomId);
		
	}
	
}
