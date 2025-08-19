package com.example.noteappapi.dto

import java.time.Instant

data class NoteDto(
    val id: Long,
    val title: String,
    val content: String,
    val color: Long,
    val createdAt: Instant
){

}
