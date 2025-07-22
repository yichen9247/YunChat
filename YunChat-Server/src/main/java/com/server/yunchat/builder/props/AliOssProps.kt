package com.server.yunchat.builder.props

import lombok.Getter
import lombok.Setter
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Setter @Getter @Component
@ConfigurationProperties(prefix = "storage.ali")
open class AliOssProps {
    var bucket: String? = null
    var endPoint: String? = null
    var secretId: String? = null
    var secretKey: String? = null
}