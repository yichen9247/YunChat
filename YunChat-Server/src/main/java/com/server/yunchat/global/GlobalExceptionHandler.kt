package com.server.yunchat.global

import com.server.yunchat.builder.data.ResultModel
import com.server.yunchat.builder.utils.ConsoleUtils
import com.server.yunchat.builder.utils.HandUtils
import jakarta.servlet.http.HttpServletRequest
import org.apache.catalina.connector.ClientAbortException
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
import org.springframework.web.multipart.MaxUploadSizeExceededException
import org.springframework.web.multipart.MultipartException
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
    fun handleBadRequestExceptions(ex: Exception, request: HttpServletRequest): ResponseEntity<ResultModel> {
        val message = when (ex) {
            is MethodArgumentNotValidException -> {
                ex.bindingResult.fieldErrors.joinToString {
                    it.defaultMessage ?: "未知错误"
                }
            }
            is TypeMismatchException -> "参数类型错误"
            is HttpMessageNotReadableException -> "请求格式错误"
            is MissingServletRequestParameterException -> "缺少必要参数"
            else -> "无效的请求"
        }
        ConsoleUtils.printCustomException("BadRequest", message, request)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            HandUtils.handleResultByCode(HttpStatus.BAD_REQUEST, null, message),
        )
    }

    // 处理接口不存在的异常（404）
    @ExceptionHandler(NoHandlerFoundException::class)
    fun handleNoHandlerFound(request: HttpServletRequest): ResponseEntity<ResultModel> {
        ConsoleUtils.printCustomException("PageNotFound", "无", request)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            HandUtils.handleResultByCode(HttpStatus.NOT_FOUND, null, "404 Not Found")
        )
    }

    // 处理请求方法不支持异常（405）
    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleMethodNotAllowed(request: HttpServletRequest): ResponseEntity<ResultModel> {
        ConsoleUtils.printCustomException("Method Not Allowed", "无", request)
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(
            HandUtils.handleResultByCode(HttpStatus.METHOD_NOT_ALLOWED, null, "不支持的请求方法")
        )
    }

    // 处理媒体类型不支持异常（415）
    @ExceptionHandler(HttpMediaTypeNotSupportedException::class)
    fun handleMediaTypeNotSupported(request: HttpServletRequest): ResponseEntity<ResultModel> {
        ConsoleUtils.printCustomException("Unsupported Media Type", "无", request)
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(
            HandUtils.handleResultByCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE, null, "不支持的媒体类型")
        )
    }

    // 处理请求头不合法异常（自定义）
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(request: HttpServletRequest): ResponseEntity<ResultModel> {
        ConsoleUtils.printCustomException("Invalid character found in method name", "Error parsing HTTP request header", request)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            HandUtils.handleResultByCode(HttpStatus.BAD_REQUEST, null, "请求头包含非法字符")
        )
    }

    // 处理客户端连接丢失异常（自定义）
    @ExceptionHandler(ClientAbortException::class)
    fun handleClientAbortException(request: HttpServletRequest) {
        // TODO: 2025/5/22 未找到合适的处理方式
    }

    // 处理上传大小超载的异常（自定义）
    @ExceptionHandler(MaxUploadSizeExceededException::class, MultipartException::class)
    fun handleMaxSizeException(request: HttpServletRequest): ResponseEntity<ResultModel> {
        ConsoleUtils.printCustomException("Payload too large", "无", request)
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(
            HandUtils.handleResultByCode(HttpStatus.PAYLOAD_TOO_LARGE, null, "上传大小超过限制")
        )
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneralException(ex: Exception): ResponseEntity<ResultModel> {
        ConsoleUtils.printErrorLog(ex)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, "服务端异常")
        )
    }
}