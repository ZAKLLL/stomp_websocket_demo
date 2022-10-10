package com.zakl.stomp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class StompWebsocketDemoApplication

fun main(args: Array<String>) {
    runApplication<StompWebsocketDemoApplication>(*args)
}
