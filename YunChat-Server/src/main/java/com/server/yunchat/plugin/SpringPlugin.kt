package com.server.yunchat.plugin

import com.server.yunchat.service.exce.FieldValidException
import jakarta.validation.ConstraintViolation
import jakarta.validation.Validation
import jakarta.validation.Validator
import org.springframework.stereotype.Service

@Service
class SpringPlugin {
    fun validField(fieldModel: Any) {
        val validator: Validator = Validation.buildDefaultValidatorFactory().validator
        val violations: Set<ConstraintViolation<Any>> = validator.validate(fieldModel)
        if (violations.isNotEmpty()) throw FieldValidException(violations.first().message)
    }
}