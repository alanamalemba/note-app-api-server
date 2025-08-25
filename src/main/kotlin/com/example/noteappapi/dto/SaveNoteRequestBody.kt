package com.example.noteappapi.dto

import jakarta.validation.constraints.NotBlank

data class SaveNoteRequestBody(
    @field:NotBlank(message = "Title cannot be blank")
    val title: String,
    val content: String,
    val color: String
)
