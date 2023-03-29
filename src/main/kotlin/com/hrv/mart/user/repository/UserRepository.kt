package com.hrv.mart.user.repository

import com.hrv.mart.userlibrary.model.User
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: ReactiveMongoRepository<User, String>