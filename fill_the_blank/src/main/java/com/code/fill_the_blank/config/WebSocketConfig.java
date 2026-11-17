package com.code.fill_the_blank.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

import com.code.fill_the_blank.jwt.StompAuthChannelInterceptor;
import com.code.fill_the_blank.jwt.WebSocketHandshakeInterceptor;
import com.code.fill_the_blank.websocket.ChatWebSocketHandlerDecoratorFactory;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
	
	private final WebSocketHandshakeInterceptor handshakeInterceptor;
	private final StompAuthChannelInterceptor stompAuthChannelInterceptor;
	private final ChatWebSocketHandlerDecoratorFactory webSocketHandlerDecoratorFactory;
	
	public WebSocketConfig(WebSocketHandshakeInterceptor handshakeInterceptor, StompAuthChannelInterceptor stompAuthChannelInterceptor, 
			ChatWebSocketHandlerDecoratorFactory webSocketHandlerDecoratorFactory) {
		this.handshakeInterceptor = handshakeInterceptor;
		this.stompAuthChannelInterceptor = stompAuthChannelInterceptor;
		this.webSocketHandlerDecoratorFactory = webSocketHandlerDecoratorFactory;
	}
	
	// STOMP 메시지 경로 설정
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.enableSimpleBroker("/topic"); // 서버에서 클라이언트에게 메시지를 전달할 경로
		registry.setApplicationDestinationPrefixes("/app"); // 클라이언트에서 서버로 메시지를 보낼때 사용할 경로
	}
	
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		
		registry.addEndpoint("/ws/chat")	// WebSocket 연결 시 WebSocketHandshakeInterceptor 실행됨
				.addInterceptors(handshakeInterceptor);
		
	}
	
	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(stompAuthChannelInterceptor); // 클라이언트가 보내는 STOMP 메시지를 처리하기 전에 우리가 만든 인터셉터가 실행되도록 등록
	}
	
	@Override
	public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
		registration.addDecoratorFactory(webSocketHandlerDecoratorFactory); // WebSocket transport/session 커스터마이징을 위한 기능
	}
}
