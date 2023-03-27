package com.hrv.mart.user.controller

import com.hrv.mart.userlibrary.User
import com.hrv.mart.user.repository.UserRepository
import com.hrv.mart.user.service.UserService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.springframework.http.server.reactive.ServerHttpResponse
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class TestUserController {
    private val userRepository = mock(UserRepository::class.java)
    private val response = mock(ServerHttpResponse::class.java)

    private val userService = UserService(userRepository)
    private val userController = UserController(userService)
    private val user = User(
        emailId = "test@test.com",
        name = "Test User"
    )
    @Test
    fun `should create user and return OK() message when user is not present in database`() {
        doReturn(Mono.just(user))
            .`when`(userRepository)
            .insert(user)

        StepVerifier.create(userController.createUser(user, response))
            .expectNext("User Created successfully")
            .verifyComplete()

    }
    @Test
    fun `should return Error() message when user already exist`() {
        doReturn(Mono.error<Exception>(Exception("Duplicate Id")))
            .`when`(userRepository)
            .insert(user)
        StepVerifier.create(userController.createUser(user, response))
            .expectNext("Something went wrong")
            .verifyComplete()
    }
    @Test
    fun `should return Error() message when userRepository_save() return empty response`() {
        doReturn(Mono.empty<User>())
            .`when`(userRepository)
            .insert(user)
        StepVerifier.create(userController.createUser(user, response))
            .expectNext("Something went wrong")
            .verifyComplete()
    }
    @Test
    fun `should return user if user already exist in database`() {
        doReturn(Mono.just(user))
            .`when`(userRepository)
            .findById(user.emailId)
        StepVerifier.create(userController.getUserById(user.emailId, response))
            .expectNext(user)
            .verifyComplete()
    }
    @Test
    fun `should return empty response if user do not exist in database`() {
        doReturn(Mono.empty<User>())
            .`when`(userRepository)
            .findById(user.emailId)
        StepVerifier.create(userController.getUserById(user.emailId, response))
            .expectComplete()
            .verify()
    }
    @Test
    fun `update user and return user if user exist in database`() {
        val updatedUser = User(
            emailId = user.emailId,
            name = "Updated Test User"
        )
        doReturn(Mono.just(user))
            .`when`(userRepository)
            .findById(user.emailId)
        doReturn(Mono.just(updatedUser))
            .`when`(userRepository)
            .save(updatedUser)
        StepVerifier.create(userController.updateUser(updatedUser, response))
            .expectNext(updatedUser)
            .verifyComplete()
    }
    @Test
    fun `should not update user and return empty response`() {
        val updatedUser = User(
            emailId = user.emailId,
            name = "Updated Test User"
        )
        doReturn(Mono.empty<User>())
            .`when`(userRepository)
            .findById(user.emailId)
        StepVerifier.create(userController.updateUser(updatedUser, response))
            .expectComplete()
            .verify()
    }
    @Test
    fun `should delete user and return OK() message when user exist in database`() {
        doReturn(Mono.just(true))
            .`when`(userRepository)
            .existsById(user.emailId)
        doReturn(Mono.empty<Void>())
            .`when`(userRepository)
            .deleteById(user.emailId)
        StepVerifier.create(userController.deleteUser(user.emailId, response))
            .expectNext("User Deleted successfully")
            .verifyComplete()
    }
    @Test
    fun `should return error() message while deleting user when user does not exist in database `() {
        doReturn(Mono.just(false))
            .`when`(userRepository)
            .existsById(user.emailId)
        StepVerifier.create(userController.deleteUser(user.emailId, response))
            .expectNext("User not found")
            .verifyComplete()
    }
}