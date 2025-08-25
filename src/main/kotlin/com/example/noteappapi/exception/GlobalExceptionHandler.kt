package com.example.noteappapi.exception

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice


@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationError(e: MethodArgumentNotValidException): ResponseEntity<Map<String, List<String>>?> {
        val errors = e.bindingResult.allErrors.map { it.defaultMessage ?: "Invalid value" }

        return ResponseEntity.status(400).body(mapOf("errors" to errors))
    }


    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadable(ex: HttpMessageNotReadableException?): ResponseEntity<String?> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or missing request body.")
    }

    @ExceptionHandler(Exception::class)
    fun handleUnhandledException(ex: Exception): ResponseEntity<String?> {

        return ResponseEntity.status(500).body(ex.message)
    }

}