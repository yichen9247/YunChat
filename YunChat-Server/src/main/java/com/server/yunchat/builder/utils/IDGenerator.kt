package com.server.yunchat.builder.utils

import java.util.*

object IDGenerator {
    fun generateRandomFileId(uid: Long, name: String, path: String, time: String): String {
        return HandUtils.encodeStringToMD5(
            UUID.randomUUID().toString() + "-" + HandUtils.encodeStringToMD5(
                uid.toString() + name + path + time
            )
        )
    }
}