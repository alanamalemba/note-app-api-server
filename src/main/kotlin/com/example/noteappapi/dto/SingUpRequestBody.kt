package com.example.noteappapi.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class SingUpRequestBody(
    @field:Email(message = "Invalid email format") val email: String,
    @field:Pattern(
        regexp = "^.{6,}$",
        message = "Password must be at least  6 characters"
    ) val password: String
)
