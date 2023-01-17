package com.hrv.mart.user.service

import com.hrv.mart.user.model.User
import com.hrv.mart.user.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

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
        userRepository.save(user)
}