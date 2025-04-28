package com.server.handsock.global

import com.server.handsock.common.utils.ConsoleUtils
import com.server.handsock.common.utils.HandUtils
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.TypeMismatchException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.NoHandlerFoundException

@RestControllerAdvice
class GlobalExceptionHandler {

    // 处理客户端请求的异常（400）
    @ExceptionHandler(
        TypeMismatchException::class,
        MethodArgumentNotValidException::class,
        HttpMessageNotReadableException::class,
        MissingServletRequestParameterException::class
    )
    fun handleBadRequestExceptions(ex: Exception, request: HttpServletRequest): ResponseEntity<Map<String, Any>> {
        val message = when (ex) {
            is MethodArgumentNotValidException -> {
                ex.bindingResult.fieldErrors.joinToString {
                    "${it.field}: ${it.defaultMessage ?: "未知错误"}"
                }.let { "参数校验失败: $it" }
            }
            is TypeMismatchException ->
                "参数类型错误: ${ex.propertyName} 需要 ${ex.requiredType?.simpleName}"
            is MissingServletRequestParameterException ->
                "缺少必要参数: ${ex.parameterName}"
            else -> "无效的请求"
        }
        printCustomException("BadRequest", message, request)
        return ResponseEntity(
            HandUtils.handleResultByCode(HttpStatus.BAD_REQUEST, null, message),
            HttpStatus.BAD_REQUEST
        )
    }

    // 处理接口不存在的异常（404）
    @ExceptionHandler(NoHandlerFoundException::class)
    fun handleNoHandlerFound(request: HttpServletRequest): ResponseEntity<Map<String, Any>> {
        printCustomException("PageNotFound", "无", request)
        return ResponseEntity(
            HandUtils.handleResultByCode(HttpStatus.NOT_FOUND, null, "404 Not Found"),
            HttpStatus.NOT_FOUND
        )
    }

    // 处理请求方法不支持异常（405）
    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleMethodNotAllowed(request: HttpServletRequest): ResponseEntity<Map<String, Any>> {
        printCustomException("Method Not Allowed", "无", request)
        return ResponseEntity(
            HandUtils.handleResultByCode(HttpStatus.METHOD_NOT_ALLOWED, null, "不支持的请求方法"),
            HttpStatus.METHOD_NOT_ALLOWED
        )
    }

    // 处理媒体类型不支持异常（415）
    @ExceptionHandler(HttpMediaTypeNotSupportedException::class)
    fun handleMediaTypeNotSupported(request: HttpServletRequest): ResponseEntity<Map<String, Any>> {
        printCustomException("Unsupported Media Type", "无", request)
        return ResponseEntity(
            HandUtils.handleResultByCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE, null, "不支持的媒体类型"),
            HttpStatus.UNSUPPORTED_MEDIA_TYPE
        )
    }

    fun printCustomException(
        content: String,
        message: String,
        request: HttpServletRequest
    ) {
        ConsoleUtils.printInfoLog(
            "错误：${content} - IP: ${HandUtils.getHttpClientIp(request)} - Method: ${request.method} - Path: ${request.requestURI} - 详细: $message"
        )
    }
}