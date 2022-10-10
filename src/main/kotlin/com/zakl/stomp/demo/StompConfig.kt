package com.zakl.stomp.demo

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer


@Configuration
@EnableWebSocketMessageBroker
class StompConfig : WebSocketMessageBrokerConfigurer {

    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        registration.interceptors(MyInboundChannelInterceptor())
    }

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        //订阅的前缀,一般点对点使用/queue,广播使用/topic
        registry.enableSimpleBroker("/topic", "/queue")
        //设置客户端访问前缀,此种模式,客户端无法接收到 发送到指定该用户的信息
        registry.setApplicationDestinationPrefixes("/app")
        //设置用户访问前缀,此种模式, 服务端可以发送到指定用户到客户端
        //且客户端可通过 /user/{userName}/queue/{queueName} 对指定用户发送信息
        registry.setUserDestinationPrefix("/user")
    }


    override fun registerStompEndpoints(registry: StompEndpointRegistry) {

        //配置webSocket端点,映射MessageController.kt 的MessageMapping
        registry
            .addEndpoint(
                "/sendToRoom1", "/sendToRoom2", "/sendToUser1", "/sendToUser2",
                "/testEndPoint"
            )
            .withSockJS()

    }


    /**
     * 标记客户端用户
     */
    class MyInboundChannelInterceptor : ChannelInterceptor {
        override fun preSend(message: Message<*>, channel: MessageChannel): Message<*>? {
            val accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)!!
            //1. 判断是否首次连接请求
            if (StompCommand.CONNECT == accessor.command) {
                val username: String = accessor.getNativeHeader("username")!!.get(0)
                val sessionId = accessor.sessionId
                println("$username:$sessionId-------->登入")
                accessor.setUser { username }
            } else if (StompCommand.DISCONNECT == accessor.command) {
                val username = accessor.user!!.name
                val sessionId = accessor.sessionId
                println("$username:$sessionId-------->登出")
            }
            return super.preSend(message, channel)
        }

    }

}