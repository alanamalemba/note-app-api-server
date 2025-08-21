package com.example.noteappapi.mapper

import com.example.noteappapi.dto.UserDto
import com.example.noteappapi.model.User

fun User.toUserDto(): UserDto = UserDto(id = this.id, email = this.email)