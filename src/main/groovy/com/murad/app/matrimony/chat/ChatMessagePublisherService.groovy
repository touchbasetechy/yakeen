package com.murad.app.matrimony.chat

import com.murad.app.matrimony.config.RabbitConfig
import org.springframework.amqp.rabbit.connection.CorrelationData
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ChatMessagePublisherService {

    @Autowired
    RabbitTemplate rabbitTemplate

    /**
     * Publishes a chat message onto the durable chat.exchange topology declared in
     * RabbitConfig. Delivery success/failure is reported asynchronously via the
     * confirm/returns callbacks registered on the RabbitTemplate bean — this call
     * does not block waiting for broker acknowledgment.
     */
    void publish(Map chatMessage) {
        String correlationId = UUID.randomUUID().toString()
        CorrelationData correlationData = new CorrelationData(correlationId)

        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                RabbitConfig.ROUTING_KEY,
                chatMessage,
                correlationData
        )
    }
}
