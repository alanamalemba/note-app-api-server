package com.example.noteappapi.security

import com.example.noteappapi.constants.AUTHORIZATION
import com.example.noteappapi.constants.BEARER_PREFIX
import com.example.noteappapi.service.JwtService
import com.example.noteappapi.service.UserDetailsServiceImpl
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
class JwtAuthFilter(private val jwtService: JwtService, val userDetailsServiceImpl: UserDetailsServiceImpl) :
    OncePerRequestFilter() {
    private val excludedPaths = listOf(
        "/auth/sign-up",
        "/auth/sign-in",
        "/auth/refresh/tokens"
    )

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.servletPath
        return excludedPaths.any { path.startsWith(it) }
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        val authHeader = request.getHeader(AUTHORIZATION)

        if (authHeader == null || !authHeader.startsWith("$BEARER_PREFIX ")) throw Exception("Invalid credentials")

        val token = authHeader.removePrefix("$BEARER_PREFIX ")

        println("Access Token: $token")
        if (!jwtService.validateAccessToken(token)) throw Exception("Invalid credentials")

        val userId = jwtService.getUserIdFromToken(token)


        val userDetails = userDetailsServiceImpl.loadUserById(userId)

        println("User id form token: $userId")
        val auth = UsernamePasswordAuthenticationToken(userDetails, null, emptyList())
        println("Auth: $auth")
        SecurityContextHolder.getContext().authentication = auth

        filterChain.doFilter(request, response)
    }
}