package com.example.noteappapi.service

import com.example.noteappapi.dto.UserDto
import com.example.noteappapi.mapper.toUserDto
import com.example.noteappapi.model.User
import com.example.noteappapi.repository.UserRepository
import org.springframework.http.HttpStatusCode
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class AuthService(
    private val jwtService: JwtService,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    data class TokenPair(
        val accessToken: String, val refreshToken: String
    )

    fun signUp(email: String, password: String): TokenPair {
        val hashedPassword = passwordEncoder.encode(password)
        val savedUser = userRepository.save(User(email = email, hashedPassword = hashedPassword))
        val tokenPair = generateTokenPair(savedUser.id)

        return TokenPair(accessToken = tokenPair.accessToken, refreshToken = tokenPair.refreshToken)
    }

    fun singIn(email: String, password: String): TokenPair {
        val targetUser = userRepository.findByEmail(email) ?: throw BadCredentialsException("Invalid credentials")

        if (!passwordEncoder.matches(
                password,
                targetUser.hashedPassword
            )
        ) throw BadCredentialsException("Invalid credentials")

        val tokenPair = generateTokenPair(targetUser.id)

        return TokenPair(accessToken = tokenPair.accessToken, refreshToken = tokenPair.refreshToken)
    }

    private fun generateTokenPair(userId: Long): TokenPair {
        val newAccessToken = jwtService.generateAccessToken(userId)
        val newRefreshToken = jwtService.generateRefreshToken(userId)
        return TokenPair(accessToken = newAccessToken, refreshToken = newRefreshToken)
    }

    fun refreshTokens(refreshToken: String): TokenPair {
        if (!jwtService.validateRefreshToken(refreshToken)) throw ResponseStatusException(
            HttpStatusCode.valueOf(401),
            "Invalid refresh Token"
        )

        val userId = jwtService.getUserIdFromToken(refreshToken)

        return generateTokenPair(userId)
    }
}