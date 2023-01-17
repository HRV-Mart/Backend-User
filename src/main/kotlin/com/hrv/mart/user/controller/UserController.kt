package com.hrv.mart.user.controller

import com.hrv.mart.user.model.User
import com.hrv.mart.user.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@RestController
@RequestMapping("/user")
class UserController (
    @Autowired
    private val userService: UserService
)
{
    @PostMapping
    fun createUser(@RequestBody user: User, response: ServerHttpResponse) =
        userService.createUser(user)
            .hasElement()
            .map {
                if (it) {
                    response.statusCode = HttpStatus.OK
                    return@map "User Created successfully"
                }
                else {
                    response.statusCode = HttpStatus.INTERNAL_SERVER_ERROR
                    return@map "Something went wrong"
                }
            }
            .onErrorResume{
                response.statusCode = HttpStatus.INTERNAL_SERVER_ERROR
                return@onErrorResume Mono.just("Something went wrong")
            }
    @GetMapping("/{userId}")
    fun getUserById(@PathVariable userId: String, response: ServerHttpResponse) =
        userService.getUser(userId)
            .switchIfEmpty {
                response.statusCode = HttpStatus.NOT_FOUND
                Mono.empty()
            }
}