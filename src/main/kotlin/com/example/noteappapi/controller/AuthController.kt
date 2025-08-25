package com.example.noteappapi.controller

import com.example.noteappapi.constants.AUTHORIZATION
import com.example.noteappapi.constants.BEARER_PREFIX
import com.example.noteappapi.dto.SingUpRequestBody
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

@RestController
@RequestMapping("/auth")
class AuthController(val authService: AuthService, val jwtService: JwtService) {

    data class SignUpResponse(val accessToken: String)
    data class SignInResponse(val accessToken: String)

    private fun setRefreshTokenCookie(
        tokenPair: AuthService.TokenPair, httpServletResponse: HttpServletResponse
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
        @Valid @RequestBody singUpRequestBody: SingUpRequestBody, httpServletResponse: HttpServletResponse
    ): SignUpResponse {
        val tokenPair = authService.signUp(singUpRequestBody.email, singUpRequestBody.password)
        setRefreshTokenCookie(tokenPair, httpServletResponse)

        return SignUpResponse(accessToken = tokenPair.accessToken)
    }

    @PostMapping("/sign-in")
    fun signIn(
        @Valid @RequestBody singUpRequestBody: SingUpRequestBody, httpServletResponse: HttpServletResponse
    ): SignInResponse {
        val tokenPair = authService.singIn(singUpRequestBody.email, singUpRequestBody.password)
        setRefreshTokenCookie(tokenPair, httpServletResponse)

        return SignInResponse(accessToken = tokenPair.accessToken)
    }

    @PostMapping("/refresh/tokens")
    fun refreshTokens(
        httpServletRequest: HttpServletRequest, httpServletResponse: HttpServletResponse
    ): AuthService.TokenPair {

        // get refresh token
        val cookies = httpServletRequest.cookies ?: throw Exception("Invalid credentials")

        val refreshTokenCooke = cookies.find { cookie -> JwtService.JwtType.REFRESH.value == cookie.name }
            ?: throw Exception("Invalid credentials")


        // if both access and refresh tokens are valid, append refresh and access token
        return authService.refreshTokens(refreshToken = refreshTokenCooke.value)
    }
}