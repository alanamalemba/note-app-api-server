package com.example.noteappapi.security

import com.example.noteappapi.model.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserPrincipal(val user: User) : UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority?>? {
        return listOf()
    }

    override fun getPassword(): String = user.hashedPassword

    override fun getUsername(): String = user.email

    fun getId(): Long = user.id
}