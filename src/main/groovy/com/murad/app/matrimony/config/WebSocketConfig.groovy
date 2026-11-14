package com.murad.app.matrimony.config

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

/**
 * Standalone WebSocket configuration — does NOT extend DefaultWebSocketConfig
 * because that class registers its own @EnableWebSocketMessageBroker and endpoints,
 * which conflicts with ours and results in duplicate/competing broker registrations.
 *
 * Implementing WebSocketMessageBrokerConfigurer directly is the correct approach
 * for Grails 6 when you need full control over broker and endpoint setup.
 */
@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  @Override
  void configureMessageBroker(MessageBrokerRegistry registry) {
    // In-memory broker serving /topic/** and /queue/** destinations
    registry.enableSimpleBroker('/topic', '/queue')
    // Client sends to /app/**
    registry.setApplicationDestinationPrefixes('/app')
    // User-specific destinations (for private messages)
    registry.setUserDestinationPrefix('/user')
  }

  @Override
  void registerStompEndpoints(StompEndpointRegistry registry) {
    // SockJS fallback endpoint – browser connects here
    registry.addEndpoint('/ws/chat')
      .setAllowedOriginPatterns('*')
      .withSockJS()
  }
}
