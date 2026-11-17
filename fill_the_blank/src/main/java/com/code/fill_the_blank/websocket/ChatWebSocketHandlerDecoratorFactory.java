package com.code.fill_the_blank.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory;

@Component
public class ChatWebSocketHandlerDecoratorFactory implements WebSocketHandlerDecoratorFactory {

	private final WebSocketSessionRegistry sessionRegistry;
	
	public ChatWebSocketHandlerDecoratorFactory(WebSocketSessionRegistry sessionRegistry) {
		this.sessionRegistry = sessionRegistry;
	}
	
	@Override
	public WebSocketHandler decorate(WebSocketHandler handler) {

		return new WebSocketHandlerDecorator(handler) {
			
			@Override
			public void afterConnectionEstablished(WebSocketSession session) throws Exception {
				// TODO Auto-generated method stub
				super.afterConnectionEstablished(session);
				sessionRegistry.register(session); // WebSocket이 연결되면
			}
			
			@Override
			public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
				
				try {
					sessionRegistry.remove(session);
				} finally {
					super.afterConnectionClosed(session, closeStatus);	// WebSocket이 끊기면 
				}
				
			}
			
		};
	}
	
	
}
