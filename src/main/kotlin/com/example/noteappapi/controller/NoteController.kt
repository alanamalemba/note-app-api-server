package com.example.noteappapi.controller

import com.example.noteappapi.dto.NoteDto
import com.example.noteappapi.dto.SaveNoteRequestBody
import com.example.noteappapi.mapper.toNoteDto
import com.example.noteappapi.model.Note
import com.example.noteappapi.repository.NoteRepository
import com.example.noteappapi.security.UserPrincipal
import jakarta.validation.Valid
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/notes")
@Validated
class NoteController(private val noteRepository: NoteRepository) {

    private fun getOwnerIdFromSecurityContext(): Long {

        return (SecurityContextHolder.getContext().authentication.principal as UserPrincipal).getId()


    }

    @PostMapping
    fun saveNote(@Valid @RequestBody saveNoteRequestBody: SaveNoteRequestBody): NoteDto {
        val ownerId: Long = getOwnerIdFromSecurityContext()

        val savedNote = noteRepository.save(
            Note(
                title = saveNoteRequestBody.title,
                content = saveNoteRequestBody.content,
                color = saveNoteRequestBody.color,
                ownerId = ownerId
            )
        )

        return savedNote.toNoteDto()

    }

    @GetMapping("/{ownerId}")
    fun getNotesByOwnerId(@PathVariable ownerId: Long): List<NoteDto> {

        return noteRepository.findByOwnerId(ownerId).map {
            it.toNoteDto()
        }

    }

    @GetMapping("/me")
    fun getNotesByAuthenticatedUser(): List<NoteDto> {

        val ownerId: Long = getOwnerIdFromSecurityContext()

        println("ownerId in /me: $ownerId")


        return noteRepository.findByOwnerId(ownerId).map {
            it.toNoteDto()
        }

    }

    @DeleteMapping("/{noteId}")
    fun deleteNoteById(@PathVariable noteId: Long): String {
        val ownerId: Long = getOwnerIdFromSecurityContext()

        val targetNote = noteRepository.findById(ownerId).orElseThrow { Exception("Note does not exist") }

        if (ownerId != targetNote.ownerId) throw Exception("Not allowed")

        noteRepository.deleteById(noteId)

        return "Note of id $noteId deleted successfully!"
    }

}