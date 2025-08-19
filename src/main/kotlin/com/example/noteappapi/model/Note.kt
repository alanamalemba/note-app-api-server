package com.example.noteappapi.model

import com.example.noteappapi.dto.NoteDto
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant

@Entity(name = "notes")
data class Note(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val title: String,
    val content: String,
    val color: Long,

    @Column(name = "owner_id")
    val ownerId: Long,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant? = null,
)