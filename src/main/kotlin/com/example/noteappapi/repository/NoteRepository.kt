package com.example.noteappapi.repository

import com.example.noteappapi.model.Note
import org.springframework.data.jpa.repository.JpaRepository

interface NoteRepository : JpaRepository<Note, Long> {
    fun findByOwnerId(ownerId: Long): List<Note>
}