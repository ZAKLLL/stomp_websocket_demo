package com.zakl.stomp.demo

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.messaging.simp.annotation.SendToUser
import org.springframework.stereotype.Controller
import org.springframework.util.StringUtils
import java.lang.RuntimeException
import java.util.*


@Controller
class MessageController {

    @Autowired
    lateinit var simpMessagingTemplate: SimpMessagingTemplate


    /**
     * 将消息发送到订阅到 /topic/chatRoom 的用户
     */
    @MessageMapping("/sendToRoom1")
    @SendTo("/topic/chatRoom1")
    fun sendToRoom(message: Message): RspMsg {
        println("sendToRoom1---------->$message")
        return RspMsg(message.from, message.text, Date())
    }

    /**
     * 将消息发送到订阅到 /topic/chatRoom 的用户
     */
    @MessageMapping("/sendToRoom2")
    fun sendToRoom2(message: Message) {
        println("sendToRoom2---------->$message")
        simpMessagingTemplate.convertAndSend("/topic/chatRoom2", RspMsg(message.from, message.text, Date()))
    }


    /**
     * 将消息发送到订阅到 /queue/userChat 的用户
     */
    @MessageMapping("/sendToUser1")
    fun sendToUser(message: Message) {
        println("sendToUser1---------->$message")
        if (!StringUtils.hasText(message.to)) {
            throw RuntimeException("message.to 不能为空")
        }
        val queueName = "/queue/userChat1"
        simpMessagingTemplate.convertAndSendToUser(
            message.to!!,
            queueName,
            RspMsg(message.from, message.text, Date())
        )
    }

    /**
     * 将消息发送到订阅到 /queue/userChat2 的用户
     */
    @MessageMapping("/sendToUser2")
    @SendToUser("/queue/userChat2")
    fun sendToUser2(message: Message): RspMsg {
        println("sendToUser2---------->$message")
        return RspMsg(message.from, message.text, Date())
    }


}


data class RspMsg(val from: String, val text: String, val time: Date)

data class Message(val from: String, val to: String?, val text: String)


