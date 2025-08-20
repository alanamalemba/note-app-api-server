package com.example.noteappapi.service

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.Date

@Service
class JwtService(
    @param:Value("\${jwt.secret}")
    private val jwtSecret: String
) {

    private val secretKey = Keys.hmacShaKeyFor(jwtSecret.toByteArray())

    companion object {
        private const val ACCESS_TOKEN_VALIDITY_IN_MS = 15L * 60L * 1000L
        const val REFRESH_TOKEN_VALIDITY_IN_MS = 30L * 24 * 60 * 60L * 1000L
        private const val JWT_CLAIM_TYPE = "type"
    }

    enum class JwtType(val value: String) { ACCESS("accessToken"), REFRESH("refreshToken") }

    private fun generateToken(
        userId: Long,
        jwtType: JwtType,
        expiry: Long
    ): String {
        val now = Date()
        val expiryDate = Date(now.time + expiry)
        return Jwts.builder()
            .subject(userId.toString())
            .claim(JWT_CLAIM_TYPE, jwtType.value)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(secretKey, Jwts.SIG.HS256)
            .compact()

    }

    fun generateAccessToken(userId: Long): String {
        return generateToken(userId, JwtType.ACCESS, ACCESS_TOKEN_VALIDITY_IN_MS)
    }

    fun generateRefreshToken(userId: Long): String {
        return generateToken(userId, JwtType.REFRESH, REFRESH_TOKEN_VALIDITY_IN_MS)
    }

    private fun parseAllClaims(token: String): Claims? {
        return try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .payload
        } catch (e: Exception) {

            null

        }
    }

    fun validateAccessToken(token: String): Boolean {
        val claims = parseAllClaims(token) ?: return false

        val tokenTypeValue = claims[JWT_CLAIM_TYPE] as? String ?: return false

        return tokenTypeValue === JwtType.ACCESS.value

    }

    fun validateRefreshToken(token: String): Boolean {
        val claims = parseAllClaims(token) ?: return false

        val tokenTypeValue = claims[JWT_CLAIM_TYPE] as? String ?: return false

        return tokenTypeValue === JwtType.REFRESH.value

    }

    fun getUserIdFromToken(token: String): Long {
        val claims = parseAllClaims(token) ?: throw IllegalArgumentException("Invalid Token")
        return claims.subject.toLong()

    }


}