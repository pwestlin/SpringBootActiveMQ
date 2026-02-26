package nu.westlin.springbootactivemq

import jakarta.jms.ConnectionFactory
import org.apache.activemq.ActiveMQConnectionFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jms.annotation.JmsListener
import org.springframework.jms.config.DefaultJmsListenerContainerFactory
import org.springframework.jms.config.JmsListenerContainerFactory
import org.springframework.jms.connection.CachingConnectionFactory
import org.springframework.jms.core.JmsTemplate
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}

@Configuration
class JmsConfig {

    @Bean
    fun connectionFactory(@Value($$"${jms.clientId}") jmsClientId: String): CachingConnectionFactory {
        val activeMqConnectionFactory = ActiveMQConnectionFactory("tcp://localhost:61616")
        val cachingConnectionFactory = CachingConnectionFactory(activeMqConnectionFactory)
        cachingConnectionFactory.setClientId(jmsClientId)
        return cachingConnectionFactory
    }

    @Bean
    fun nonDurableTopicListenerFactory(connectionFactory: ConnectionFactory): JmsListenerContainerFactory<*> {
        return DefaultJmsListenerContainerFactory().apply {
            setConnectionFactory(connectionFactory)
            setPubSubDomain(true)
        }
    }

    @Bean
    fun durableTopicListenerFactory(connectionFactory: ConnectionFactory) : JmsListenerContainerFactory<*> {
        return DefaultJmsListenerContainerFactory().apply {
            setConnectionFactory(connectionFactory)
            setPubSubDomain(true)
            setSubscriptionDurable(true)
        }
    }

    @Bean
    fun queueJmsTemplate(connectionFactory: ConnectionFactory): JmsTemplate {
        return JmsTemplate(connectionFactory)
    }

    @Bean
    fun topicJmsTemplate(connectionFactory: ConnectionFactory): JmsTemplate = JmsTemplate(connectionFactory).apply {
        isPubSubDomain = true
    }
}

@Service
class MessagingService(
    private val queueJmsTemplate: JmsTemplate, // default, queue
    private val topicJmsTemplate: JmsTemplate,  // separat, topic
    @Value($$"${jms.queueName}") private val queueName: String,
    @Value($$"${jms.topicName}") private val topicName: String,
) {

    fun sendToQueue(message: String) {
        queueJmsTemplate.convertAndSend(queueName, message)
    }

    fun sendToTopic(message: String) {
        topicJmsTemplate.convertAndSend(topicName, message)
    }
}

@Component
class MessageListeners {

    @JmsListener(destination = $$"${jms.queueName}")
    fun onQueueMessage(msg: String) {
        println("1: Queue Received: $msg")
    }

    @JmsListener(destination = $$"${jms.queueName}")
    fun onQueueMessage2(msg: String) {
        println("2: Queue Received: $msg")
    }

    @JmsListener(destination = $$"${jms.topicName}", containerFactory = "nonDurableTopicListenerFactory")
    fun onTopicMessage(msg: String) {
        println("1: Topic Received: $msg")
    }

    @JmsListener(destination = $$"${jms.topicName}", containerFactory = "nonDurableTopicListenerFactory")
    fun onTopicMessage2(msg: String) {
        println("2: Topic Received: $msg")
    }

    @JmsListener(
        destination = $$"${jms.topicName}",
        containerFactory = "durableTopicListenerFactory",
        subscription = $$"${jms.durable.subscription}"
    )
    fun onMessage(message: String) {
        println("Durable Topic received: $message")
    }
}

@RestController
class MessagingController(
    private val messagingService: MessagingService
) {

    @PostMapping("/send/queue")
    fun sendToQueue(@RequestParam message: String): String {
        messagingService.sendToQueue(message)
        return "Sent to queue: $message"
    }

    @PostMapping("/send/topic")
    fun sendToTopic(@RequestParam message: String): String {
        messagingService.sendToTopic(message)
        return "Sent to topic: $message"
    }
}

