package com.example.noteappapi.dto

data class SaveNoteRequestBody(
    val title: String,
    val content: String,
    val color: Long
)
