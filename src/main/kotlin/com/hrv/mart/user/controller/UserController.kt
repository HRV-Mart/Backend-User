package com.hrv.mart.user.controller

import com.hrv.mart.user.service.UserService
import com.hrv.mart.userlibrary.model.User
import com.hrv.mart.userlibrary.model.UserTopics
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.kafka.annotation.KafkaListener

import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@RestController
@RequestMapping("/user")
class UserController (
    @Autowired
    private val userService: UserService
)
{
    @KafkaListener(
        topics = [UserTopics.createUserTopic],
        groupId = "\${spring.kafka.consumer.group-id}"
    )
    fun createUser(user: User) {
        userService.createUser(user)
            .hasElement()
            .map {
                if (it) {
                    return@map "User Created successfully"
                } else {
                    return@map "Something went wrong"
                }
            }
            .onErrorResume {
                return@onErrorResume Mono.just("Something went wrong")
            }
            .block()
    }
    @GetMapping("/{userId}")
    fun getUserById(@PathVariable userId: String, response: ServerHttpResponse) =
        userService.getUser(userId)
            .switchIfEmpty {
                response.statusCode = HttpStatus.NOT_FOUND
                Mono.empty()
            }
    @PutMapping
    fun updateUser(@RequestBody user: User, response: ServerHttpResponse) =
        userService.updateUser(user)
            .switchIfEmpty {
                response.statusCode = HttpStatus.NOT_FOUND
                Mono.empty()
            }
    @KafkaListener(
        topics = [UserTopics.deleteUserTopic],
        groupId = "\${spring.kafka.consumer.group-id}"
    )
    fun deleteUser(@PathVariable userId: String) {
        userService.deleteUser(userId)
            .onErrorResume {
                return@onErrorResume Mono.empty()
            }
            .switchIfEmpty {
                Mono.just("User not found")
            }
            .block()
    }
}