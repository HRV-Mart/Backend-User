package com.hrv.mart.user.controller

import com.hrv.mart.user.repository.UserRepository
import com.hrv.mart.user.service.UserService
import com.hrv.mart.userlibrary.model.User
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.testcontainers.containers.MongoDBContainer
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@DataMongoTest
class TestUserController (
    private val userRepository: UserRepository
){
    private val response = mock(ServerHttpResponse::class.java)

    private val userService = UserService(userRepository)
    private val userController = UserController(userService)
    private val user = User(
        emailId = "test@test.com",
        name = "Test User"
    )
    @BeforeEach
    fun cleanDatabase() {
        userRepository
            .deleteAll()
            .subscribe()
    }
    @Test
    fun `should return user if user already exist in database`() {
        userRepository
            .insert(user)
            .subscribe()
        StepVerifier.create(userController.getUserById(user.emailId, response))
            .expectNext(user)
            .verifyComplete()
    }
    @Test
    fun `should return empty response if user do not exist in database`() {
        StepVerifier.create(userController.getUserById(user.emailId, response))
            .expectComplete()
            .verify()
    }
    @Test
    fun `update user and return user if user exist in database`() {
        userRepository
            .insert(user)
            .subscribe()

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
    companion object {
        private lateinit var mongoDBContainer: MongoDBContainer

        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            mongoDBContainer = MongoDBContainer("mongo:latest")
                .apply { withExposedPorts(27_017) }
                .apply { start() }
            mongoDBContainer
                .withReuse(true)
                .withAccessToHost(true)
            System.setProperty("spring.data.mongodb.uri", "${mongoDBContainer.connectionString}/test")
            System.setProperty("spring.data.mongodb.auto-index-creation", "true")
        }

        @JvmStatic
        @AfterAll
        fun afterAll() {
            mongoDBContainer.stop()
        }
    }

}