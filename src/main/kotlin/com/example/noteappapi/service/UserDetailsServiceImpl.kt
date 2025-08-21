package com.example.noteappapi.service

import com.example.noteappapi.repository.UserRepository
import com.example.noteappapi.security.UserPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl(val userRepository: UserRepository) : UserDetailsService {
    override fun loadUserByUsername(email: String): UserDetails {
        val targetUser = userRepository.findByEmail(email) ?: throw Exception("User not found")

        return UserPrincipal(targetUser)
    }

    fun loadUserById(userId: Long): UserDetails? {
        val targetUser = userRepository.findById(userId).orElseThrow { Exception("User not found") }

        return UserPrincipal(targetUser)
    }
}