package com.code.fill_the_blank.dto;

import java.time.LocalDateTime;

public class ChatRoom {
	private String roomId;
	private Long idleId;
	private LocalDateTime createdAt;
	
	public ChatRoom() {
		
	}

	public String getRoomId() {
		return roomId;
	}

	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}

	public Long getIdleId() {
		return idleId;
	}

	public void setIdleId(Long idleId) {
		this.idleId = idleId;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	
	
	

}
