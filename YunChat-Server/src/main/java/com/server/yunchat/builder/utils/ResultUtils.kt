package com.server.yunchat.builder.utils

import com.server.yunchat.builder.data.ResultModel
import org.springframework.http.HttpStatus

/**
 * @name 结果工具类
 * @author yichen9247
 */
object ResultUtils {
    fun printParamMessage(): ResultModel {
        return HandUtils.handleResultByCode(HttpStatus.NOT_ACCEPTABLE, null, "参数有错误")
    }

    fun printForbiddenMessage(): ResultModel {
        return HandUtils.handleResultByCode(HttpStatus.FORBIDDEN, null, "非法访问")
    }

    fun printVersionMessage(): ResultModel {
        return HandUtils.handleResultByCode(HttpStatus.UPGRADE_REQUIRED, null, "前后端版本不一致")
    }

    fun printErrorMessage(e: Exception): ResultModel {
        return HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, e.message ?: "服务端异常")
    }
}