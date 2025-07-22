package com.server.yunchat.builder.props

import lombok.Getter
import lombok.Setter
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Setter @Getter @Component
@ConfigurationProperties(prefix = "storage.tencent")
open class TencentCosProps {
    var region: String? = null
    var bucket: String? = null
    var secretId: String? = null
    var secretKey: String? = null
}