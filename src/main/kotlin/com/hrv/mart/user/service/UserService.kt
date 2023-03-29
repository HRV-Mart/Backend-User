package com.hrv.mart.user.service

import com.hrv.mart.user.repository.UserRepository
import com.hrv.mart.userlibrary.model.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UserService (
    @Autowired
    private val userRepository: UserRepository
)
{
    fun createUser(user: User) =
        userRepository.insert(user)
    fun getUser(id: String) =
        userRepository.findById(id)
    fun updateUser(user: User) =
        getUser(user.emailId)
            .flatMap {
                userRepository.save(user)
            }
    fun deleteUser(userId: String) =
        userRepository.existsById(userId)
            .flatMap {
                if (it) {
                    userRepository.deleteById(userId)
                        .then(Mono.just("User Deleted successfully"))
                }
                else {
                    Mono.error(Exception("User not found"))
                }
            }
}