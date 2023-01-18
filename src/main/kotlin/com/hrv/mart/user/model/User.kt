package com.hrv.mart.user.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("User")
data class User (
    @Id
    val emailId: String,
    val name: String
)