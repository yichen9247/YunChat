package com.server.handsock.global

import com.google.gson.Gson
import com.server.handsock.common.utils.HandUtils
import com.server.handsock.service.AuthService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Order(1)
@Component
class GlobalAuthHandler: OncePerRequestFilter() {
    @Autowired
    private lateinit var authService: AuthService

    private val excludePaths = listOf(
        "/api/upload/**",
        "/api/android/user/login",
        "/api/android/user/register",
        "/api/android/user/login/qq",
    )

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if (isExcludePath(request.requestURI)) {
            filterChain.doFilter(request, response)
            return
        }

        when {
            isOpenPath(request.requestURI) -> if (!authService.validApiRequestTime(request)) {
                sendFrequentResponse(request, response)
                return
            } else if (!authService.validOpenApiRequestLimit(request)) {
                sendForbiddenResponse(request, response)
                return
            }
            isAdminPath(request.requestURI) -> if (!authService.validClientTokenByRequest(request)) {
                sendForbiddenResponse(request, response)
                return
            }
            else -> if (!authService.validClientTokenByRequest(request)) {
                sendForbiddenResponse(request, response)
                return
            }
        }
        filterChain.doFilter(request, response)
    }

    private fun sendResponse(
        request: HttpServletRequest,
        response: HttpServletResponse,
        httpStatus: HttpStatus,
        message: String
    ) {
        response.apply {
            contentType = "application/json;charset=UTF-8"
            status = httpStatus.value()
            writer.write(
                Gson().toJson(
                    HandUtils.handleResultByCode(httpStatus, null, message).toMap()
                )
            )
        }
        GlobalExceptionHandler().printCustomException(message, "无", request)
    }

    private fun sendForbiddenResponse(
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        sendResponse(request, response, HttpStatus.FORBIDDEN, "请求未被授权")
    }

    private fun sendFrequentResponse(
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        sendResponse(request, response, HttpStatus.TOO_MANY_REQUESTS, "请求过于频繁")
    }

    private fun isOpenPath(path: String): Boolean {
        return listOf(
            "/api/openapi/**"
        ).any { path.matches(it.replace("**", ".*").toRegex()) }
    }

    private fun isAdminPath(path: String): Boolean {
        return listOf(
            "/api/admin/**"
        ).any { path.matches(it.replace("**", ".*").toRegex()) }
    }

    private fun isExcludePath(path: String): Boolean {
        return excludePaths.any { path.matches(it.replace("**", ".*").toRegex()) }
    }
}