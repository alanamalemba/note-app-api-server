package com.example.noteappapi.controller

import com.example.noteappapi.constants.AUTHORIZATION
import com.example.noteappapi.constants.BEARER_PREFIX
import com.example.noteappapi.dto.SingUpRequestBody
import com.example.noteappapi.dto.UserDto
import com.example.noteappapi.service.AuthService
import com.example.noteappapi.service.JwtService
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import kotlin.text.removePrefix
import kotlin.text.startsWith

@RestController
@RequestMapping("/auth")
class AuthController(val authService: AuthService, val jwtService: JwtService) {

    data class SingUpResponse(val accessToken: String)
    data class SingInResponse(val accessToken: String)

    private fun setRefreshTokenCookie(
        tokenPair: AuthService.TokenPair,
        httpServletResponse: HttpServletResponse
    ) {
        val refreshTokenCookie: Cookie = Cookie(JwtService.JwtType.REFRESH.value, tokenPair.refreshToken).apply {
            isHttpOnly = true
            secure = false // set to true if using http or env is prod
            path = "/"
            maxAge = JwtService.REFRESH_TOKEN_VALIDITY_IN_MS.toInt()
        }

        httpServletResponse.addCookie(refreshTokenCookie)
    }

    @PostMapping("/sign-up")
    fun signUp(
        @Valid @RequestBody singUpRequestBody: SingUpRequestBody,
        httpServletResponse: HttpServletResponse
    ): SingUpResponse {
        val tokenPair = authService.signUp(singUpRequestBody.email, singUpRequestBody.password)
        setRefreshTokenCookie(tokenPair, httpServletResponse)

        return SingUpResponse(accessToken = tokenPair.accessToken)
    }

    @PostMapping("/sign-in")
    fun signIn(
        @Valid @RequestBody singUpRequestBody: SingUpRequestBody,
        httpServletResponse: HttpServletResponse
    ): SingUpResponse {
        val tokenPair = authService.singIn(singUpRequestBody.email, singUpRequestBody.password)
        setRefreshTokenCookie(tokenPair, httpServletResponse)

        return SingUpResponse(accessToken = tokenPair.accessToken)
    }

    @PostMapping("/refresh/tokens ")
    fun refreshTokens(
        httpServletRequest: HttpServletRequest,
        httpServletResponse: HttpServletResponse
    ) {

        // get refresh token
        val cookies = httpServletRequest.cookies ?: throw Exception("Invalid credentials")

        val refreshTokenCooke = cookies.find { cookie -> JwtService.JwtType.REFRESH.value == cookie.name }
            ?: throw Exception("Invalid credentials")


        // get access token
        val authHeader = httpServletRequest.getHeader(AUTHORIZATION)

        if (authHeader == null || !authHeader.startsWith("$BEARER_PREFIX ")) throw Exception("Invalid auth header")

        if (!jwtService.validateAccessToken(authHeader.removePrefix("$BEARER_PREFIX "))) throw Exception("Invalid auth header")


        // todo: refresh the tokens if access token is valid but expired and refresh token is valid and not expired
        // todo: if refreshing token requirements criteria are valid send new tokens by
        // todo: - appending refresh token to http only cookie
        // todo: - appending access token to response body
        authService.refreshTokens(refreshToken = refreshTokenCooke.value)


    }
}