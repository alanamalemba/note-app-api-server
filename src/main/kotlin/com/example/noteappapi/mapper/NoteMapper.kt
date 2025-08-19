package com.example.noteappapi.mapper

import com.example.noteappapi.dto.NoteDto
import com.example.noteappapi.model.Note

fun Note.toNoteDto(): NoteDto = NoteDto(
    id = this.id!!,
    title = this.title,
    content = this.content,
    color = this.color,
    createdAt = this.createdAt!!
)