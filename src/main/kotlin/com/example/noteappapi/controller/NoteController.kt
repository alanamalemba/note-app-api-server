package com.example.noteappapi.controller

import com.example.noteappapi.dto.NoteDto
import com.example.noteappapi.dto.SaveNoteRequestBody
import com.example.noteappapi.mapper.toNoteDto
import com.example.noteappapi.model.Note
import com.example.noteappapi.repository.NoteRepository
import jakarta.validation.Valid
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/notes")
@Validated
class NoteController(private val noteRepository: NoteRepository) {

    @PostMapping
    fun saveNote(@Valid @RequestBody saveNoteRequestBody: SaveNoteRequestBody): NoteDto {
        val savedNote = noteRepository.save(
            Note(
                title = saveNoteRequestBody.title,
                content = saveNoteRequestBody.content,
                color = saveNoteRequestBody.color,
                ownerId = 0
            )
        )

        return savedNote.toNoteDto()

    }

    @GetMapping("/{ownerId}")
    fun getNotesByOwnerId(@Valid @PathVariable ownerId: Long): List<NoteDto> {

        return noteRepository.findByOwnerId(ownerId).map {
            it.toNoteDto()
        }

    }

}