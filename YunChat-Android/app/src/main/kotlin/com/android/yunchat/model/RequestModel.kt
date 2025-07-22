package com.android.yunchat.model

data class ResultModel<out T>(
    val code: Int,
    val data: T? = null,
    val timestamp: Long,
    val message: String? = null
)

data class ResultListModel<out T>(
    val total: Int,
    val items: List<T>
)

data class UserInfoModel(
    val uid: Long,
    val status: Int,
    var nick: String,
    var avatar: String,
    val permission: Int,
    val regTime: String,
    val username: String
)

data class GroupInfoModel(
    val gid: Int,
    val status: Int,
    val time: String,
    val name: String,
    val avatar: String,
    val notice: String,
    val member: List<UserInfoModel>,
    val message: List<MessageInfoModel>
)

data class UserAuthModel(
    val token: String,
    val userinfo: UserInfoModel
)

data class UserAuthSendModel(
    val username: String,
    val password: String
)

data class UserAuthEncryptModel(
    val data: String
)

data class UserInfoUpdateModel(
    val data: String
)

data class UserScanLoginModel(
    val qid: String,
    val status: Int
)

data class QrcodeScanResultModel(
    val gid: String?,
    val qid: String?,
    val type: String,
    val address: String?,
    val platform: String?
)

data class MessageInfoModel(
    val tar: Int,
    val obj: Long,
    val uid: Long,
    val sid: String,
    val type: String,
    val time: String,
    val deleted: Int,
    var content: String
)

data class UploadResultModel(
    val path: String
)

data class SocketGroupOperate(
    val gid: Int
)

data class CheckUpdateOperate(
    val version: String
)

data class CreateAiMessageModel(
    val event: String,
    val eventId: String,
    var content: String,
    val result: MessageInfoModel
)

data class DashboardModel(
    val chanTotal: Int,
    val userTotal: Int,
    val todayRegUser: Int,
    val todayChatTotal: Int,
    val systemOsInfo: SystemOsInfo

) {
    data class SystemOsInfo(
        val osInfo: String,
        val osArch: String,
        val locale: String,
        val hostName: String,
        val appVersion: String,
        val timeZoneId: String,
        val hostAddress: String,
        val systemUptime: String,
        val logicalCount: Number,
        val memoryUsageInfo: String
    )
}

data class NoticeItemModel(
    val nid: Int,
    val time: String,
    val title: String,
    val content: String
)

data class UpdateInfoModel(
    val version: String,
    val download: String
) {}

data class TencentLoginModel(
    val openid: String,
    val access_token: String
)

data class UserBindModel(
    val qq: Boolean
)