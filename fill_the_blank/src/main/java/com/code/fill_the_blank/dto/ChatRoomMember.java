package com.code.fill_the_blank.dto;

import java.time.LocalDateTime;

public class ChatRoomMember {
	
	private String roomId;
	private Long memberId;
	private LocalDateTime joinedAt;
	private LocalDateTime leftAt;
	
	public ChatRoomMember() {
		
	}

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public Long getMemberId() {
		return memberId;
	}

	public void setMemberId(Long memberId) {
		this.memberId = memberId;
	}

	public LocalDateTime getJoinedAt() {
		return joinedAt;
	}

	public void setJoinedAt(LocalDateTime joinedAt) {
		this.joinedAt = joinedAt;
	}

	public LocalDateTime getLeftAt() {
		return leftAt;
	}

	public void setLeftAt(LocalDateTime leftAt) {
		this.leftAt = leftAt;
	}
	
	

}
