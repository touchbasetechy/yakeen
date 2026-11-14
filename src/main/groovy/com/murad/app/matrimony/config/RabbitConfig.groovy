package com.murad.app.matrimony.config

import groovy.util.logging.Slf4j
import org.aopalliance.aop.Advice
import org.springframework.amqp.core.*
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.connection.CorrelationData
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.retry.backoff.ExponentialBackOffPolicy

/**
 * RabbitMQ topology + production hardening for the chat module.
 *
 * ConnectionFactory is explicitly defined here because Spring Boot's
 * RabbitAutoConfiguration backs off once we define our own RabbitTemplate /
 * factory beans, meaning it never creates the ConnectionFactory bean either.
 * All connection settings are bound from spring.rabbitmq.* in application.yml.
 */
@Slf4j
@Configuration
class RabbitConfig {

  // ── Topology names ──────────────────────────────────────────────
  static final String EXCHANGE = "chat.exchange"
  static final String QUEUE = "chat.queue"
  static final String ROUTING_KEY = "chat.routingkey"

  static final String DLX = "chat.exchange.dlx"
  static final String DLQ = "chat.queue.dlq"
  static final String DLQ_ROUTING_KEY = "chat.routingkey.dlq"

  // ── Connection settings (spring.rabbitmq.* in application.yml) ──
  @Value('${spring.rabbitmq.host:localhost}')
  String host

  @Value('${spring.rabbitmq.port:5672}')
  int port

  @Value('${spring.rabbitmq.username:guest}')
  String username

  @Value('${spring.rabbitmq.password:guest}')
  String password

  @Value('${spring.rabbitmq.virtual-host:/}')
  String virtualHost

  // ── Listener settings (spring.rabbitmq.listener.simple.* in yml) ─
  @Value('${spring.rabbitmq.listener.simple.concurrency:2}')
  int concurrency

  @Value('${spring.rabbitmq.listener.simple.max-concurrency:10}')
  int maxConcurrency

  @Value('${spring.rabbitmq.listener.simple.prefetch:10}')
  int prefetch

  // ── ConnectionFactory ────────────────────────────────────────────
  // Explicitly defined so Boot's auto-config backing off does not
  // leave the application without a ConnectionFactory bean.

  @Bean
  ConnectionFactory connectionFactory() {
    CachingConnectionFactory factory = new CachingConnectionFactory()
    factory.host = host
    factory.port = port
    factory.username = username
    factory.password = password
    factory.virtualHost = virtualHost
    // Enable publisher confirms + returns (required for RabbitTemplate callbacks)
    factory.publisherConfirmType = CachingConnectionFactory.ConfirmType.CORRELATED
    factory.publisherReturns = true
    factory
  }

  // ── Main topology (quorum queue + dead-lettering) ───────────────

  @Bean
  TopicExchange chatExchange() {
    new TopicExchange(EXCHANGE, true, false)
  }

  @Bean
  Queue chatQueue() {
    QueueBuilder.durable(QUEUE)
      .quorum()
      .withArgument("x-dead-letter-exchange", DLX)
      .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
      .withArgument("x-message-ttl", 86_400_000)
      .build()
  }

  @Bean
  Binding chatBinding(Queue chatQueue, TopicExchange chatExchange) {
    BindingBuilder.bind(chatQueue).to(chatExchange).with(ROUTING_KEY)
  }

  // ── Dead-letter topology ────────────────────────────────────────

  @Bean
  TopicExchange chatDeadLetterExchange() {
    new TopicExchange(DLX, true, false)
  }

  @Bean
  Queue chatDeadLetterQueue() {
    QueueBuilder.durable(DLQ).build()
  }

  @Bean
  Binding chatDeadLetterBinding(Queue chatDeadLetterQueue, TopicExchange chatDeadLetterExchange) {
    BindingBuilder.bind(chatDeadLetterQueue).to(chatDeadLetterExchange).with(DLQ_ROUTING_KEY)
  }

  // ── Message converter ────────────────────────────────────────────

  @Bean
  Jackson2JsonMessageConverter jsonMessageConverter() {
    new Jackson2JsonMessageConverter()
  }

  // ── RabbitTemplate — publisher confirms + returns ───────────────

  @Bean
  RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                Jackson2JsonMessageConverter converter) {
    RabbitTemplate template = new RabbitTemplate(connectionFactory)
    template.messageConverter = converter
    template.mandatory = true

    template.setConfirmCallback { CorrelationData correlationData, boolean ack, String cause ->
      if (!ack) {
        log.error("Message NOT confirmed by broker: correlationId={}, cause={}",
          correlationData?.id, cause)
      }
    }

    template.setReturnsCallback { returned ->
      log.error("Message returned as unroutable: exchange={}, routingKey={}, replyText={}",
        returned.exchange, returned.routingKey, returned.replyText)
    }

    template
  }

  // ── Retry interceptor (exponential backoff → DLQ on exhaustion) ─

  @Bean
  ExponentialBackOffPolicy chatBackOffPolicy() {
    ExponentialBackOffPolicy policy = new ExponentialBackOffPolicy()
    policy.initialInterval = 1000
    policy.multiplier = 2.0
    policy.maxInterval = 30_000
    policy
  }

  @Bean
  Advice chatRetryInterceptor(ExponentialBackOffPolicy chatBackOffPolicy) {
    RetryInterceptorBuilder.stateless()
      .maxAttempts(5)
      .backOffPolicy(chatBackOffPolicy)
      .recoverer(new RejectAndDontRequeueRecoverer())
      .build()
  }

  // ── Listener container factory ───────────────────────────────────
  // Use: @RabbitListener(containerFactory = "chatListenerContainerFactory")

  @Bean
  SimpleRabbitListenerContainerFactory chatListenerContainerFactory(
    ConnectionFactory connectionFactory,
    Jackson2JsonMessageConverter converter,
    Advice chatRetryInterceptor) {

    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory()
    factory.setConnectionFactory(connectionFactory)
    factory.setMessageConverter(converter)
    factory.setConcurrentConsumers(concurrency)
    factory.setMaxConcurrentConsumers(maxConcurrency)
    factory.setPrefetchCount(prefetch)
    factory.setDefaultRequeueRejected(false)
    factory.setAdviceChain(chatRetryInterceptor)
    factory
  }
}
