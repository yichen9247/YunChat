package com.server.yunchat.builder.data

data class UserMannerModel(
    val uid: Long?,
    val status: Int?,
    val nick: String?,
    val robot: Boolean?,
    val avatar: String?,
    val username: String?,
    val password: String?
)

// ----------- CREATE -----------//

data class AdminCreateGroupModel(
    val name: String? = null,
    val notice: String? = null,
    val avatar: String? = null
)

data class AdminCreateNoticeModel(
    val title: String? = null,
    val content: String? = null
)

// ----------- UPDATE -----------//

data class AdminUpdateUserInfoModel(
    val uid: Long? = null,
    val nick: String? = null,
    val robot: Boolean? = null,
    val avatar: String? = null
)

data class AdminUpdateUserStatusModel(
    val uid: Long? = null,
    val status: Int? = null
)

data class AdminUpdateUserPasswordsModel(
    val uid: Long? = null,
    val password: String? = null
)

data class AdminUpdateGroupInfoModel(
    val gid: Int? = null,
    val name: String? = null,
    val notice: String? = null,
    val avatar: String? = null
)

data class AdminUpdateGroupStatusModel(
    val gid: Int? = null,
    val status: Int? = null
)

data class AdminUpdateNoticeModel(
    val id: Int? = null,
    val title: String? = null,
    val content: String? = null
)

// ----------- DELETE -----------//

data class AdminDeleteUserModel(
    val uid: Long? = null
)

data class AdminChatOperateModel(
    val sid: String? = null
)

data class AdminGroupOperateModel(
    val gid: Int? = null
)

data class AdminDeleteMemberModel(
    val gid: Int? = null,
    val uid: Long? = null
)

data class AdminDeleteNoticeModel(
    val id: Int? = null
)

data class AdminDeleteReportModel(
    val rid: Int? = null
)

data class AdminDeleteUploadModel(
    val fid: String? = null
)