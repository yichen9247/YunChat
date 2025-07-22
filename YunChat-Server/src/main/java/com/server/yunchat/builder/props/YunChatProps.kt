package com.server.yunchat.builder.props

import lombok.Getter
import lombok.Setter
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Setter @Getter @Component
@ConfigurationProperties(prefix = "yunchat")
open class YunChatProps {
    var port: Int = 5120
    var host: String? = null
    var storageMod: Int? = 0
    var origin: String? = null
    var openapi: String? = null
    var secretKey: String = ""
    var pingTimeout: Int = 3000
    var pingInterval: Int = 5000
    var upgradeTimeout: Int = 10000
    var appVersion: String = "2.3.2-B21"
}
