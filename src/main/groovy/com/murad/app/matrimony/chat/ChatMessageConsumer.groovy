package com.murad.app.matrimony.chat

import com.rabbitmq.client.Channel
import groovy.util.logging.Slf4j
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

/**
 * Consumes chat.queue (declared in RabbitConfig). Uses the chatListenerContainerFactory
 * so messages get the retry/backoff -> DLQ behavior on persistent failures, on top of
 * manual ack mode configured via spring.rabbitmq.listener.simple.* in application.yml.
 */
@Slf4j
@Component
class ChatMessageConsumer {

    @RabbitListener(queues = "chat.queue", containerFactory = "chatListenerContainerFactory")
    void receive(Map payload, Message message, Channel channel) {
        long tag = message.messageProperties.deliveryTag
        try {
            process(payload)
            channel.basicAck(tag, false)
        } catch (TransientChatException e) {
            log.warn("Transient failure processing chat message, will retry: {}", e.message)
            // Retry/backoff is handled by the chatRetryInterceptor advice chain on the
            // listener container — rethrow so the container's retry logic can intercept it
            // rather than nacking here, which would bypass the interceptor entirely.
            throw e
        } catch (Exception e) {
            log.error("Permanent failure processing chat message, routing to DLQ", e)
            channel.basicNack(tag, false, false) // false,false = don't requeue -> goes to DLX/DLQ
        }
    }

    private void process(Map payload) {
        // business logic: persist message, broadcast to STOMP subscribers, etc.
    }
}

class TransientChatException extends RuntimeException {
    TransientChatException(String msg) { super(msg) }
    TransientChatException(String msg, Throwable cause) { super(msg, cause) }
}
