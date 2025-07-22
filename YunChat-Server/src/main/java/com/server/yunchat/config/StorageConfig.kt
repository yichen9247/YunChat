package com.server.yunchat.config

import com.aliyun.oss.OSS
import com.aliyun.oss.OSSClientBuilder
import com.qcloud.cos.COSClient
import com.qcloud.cos.ClientConfig
import com.qcloud.cos.auth.BasicCOSCredentials
import com.qcloud.cos.auth.COSCredentials
import com.qcloud.cos.http.HttpProtocol
import com.qcloud.cos.region.Region
import com.server.yunchat.builder.props.AliOssProps
import com.server.yunchat.builder.props.TencentCosProps
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class StorageConfig @Autowired constructor(
    private val aliOssProps: AliOssProps,
    private val tencentCosProps: TencentCosProps
) {
    @Bean
    open fun tencentCosClient(): COSClient {
        val cred: COSCredentials = BasicCOSCredentials(tencentCosProps.secretId, tencentCosProps.secretKey)
        val regionConfig = Region(tencentCosProps.region)
        val clientConfig = ClientConfig(regionConfig)
        clientConfig.httpProtocol = HttpProtocol.https
        return COSClient(cred, clientConfig)
    }

    @Bean
    open fun aliOssClient(): OSS {
        return OSSClientBuilder().build(aliOssProps.endPoint, aliOssProps.secretId, aliOssProps.secretKey)
    }
}