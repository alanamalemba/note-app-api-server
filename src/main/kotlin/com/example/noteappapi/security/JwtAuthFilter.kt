package com.example.noteappapi.security

import com.example.noteappapi.constants.AUTHORIZATION
import com.example.noteappapi.constants.BEARER_PREFIX
import com.example.noteappapi.service.JwtService
import io.jsonwebtoken.Header
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.hibernate.action.spi.Executable
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthFilter(private val jwtService: JwtService) : OncePerRequestFilter() {
    private val excludedPaths = listOf(
        "/sign-up",
        "/sign-in",
        "/auth/refresh/tokens"
    )

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.servletPath
        return excludedPaths.any { path.equals(it) }
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        val authHeader = request.getHeader(AUTHORIZATION)

        if (authHeader == null || !authHeader.startsWith("$BEARER_PREFIX ")) throw Exception("Invalid auth header")

        if (!jwtService.validateAccessToken(authHeader.removePrefix("$BEARER_PREFIX "))) throw Exception("Invalid auth header")

        val userId = jwtService.getUserIdFromToken(authHeader.removePrefix("$BEARER_PREFIX "))
        val auth = UsernamePasswordAuthenticationToken(userId, null)

        SecurityContextHolder.getContext().authentication = auth

        filterChain.doFilter(request, response)
    }
}