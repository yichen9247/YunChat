package com.server.yunchat.socket.handler

import com.corundumstudio.socketio.AckRequest
import com.server.yunchat.admin.service.*
import com.server.yunchat.builder.data.*
import com.server.yunchat.builder.service.NoticeObjService
import com.server.yunchat.builder.utils.HandUtils
import com.server.yunchat.builder.utils.ResultUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class AdminHandler @Autowired constructor(
    private val noticeObjService: NoticeObjService,
    private val serverUserService: ServerUserService,
    private val serverChatService: ServerChatService,
    private val serverGroupService: ServerGroupService,
    private val serverLoginService: ServerLoginService,
    private val serverNoticeService: ServerNoticeService,
    private val serverUploadService: ServerUploadService,
    private val serverReportService: ServerReportService
) {
    fun handleAdminRequest(ackSender: AckRequest, call: () -> Any) {
        ackSender.sendAckData(call())
    }

    fun deleteUser(data: AdminDeleteUserModel): ResultModel {
        return if (data.uid == null || data.uid <= 0) {
            ResultUtils.printParamMessage()
        } else serverUserService.deleteUser(data.uid)
    }

    fun deleteGroup(data: AdminGroupOperateModel): ResultModel {
        return if (data.gid == null || data.gid < 0) {
            ResultUtils.printParamMessage()
        } else serverGroupService.deleteGroup(data.gid)
    }

    fun deleteReport(data: AdminDeleteReportModel): ResultModel {
        return if (data.rid == null || data.rid <= 0) {
            ResultUtils.printParamMessage()
        } else serverReportService.deleteReport(data.rid)
    }

    fun deleteUpload(data: AdminDeleteUploadModel): ResultModel {
        return if (data.fid.isNullOrEmpty()) {
            ResultUtils.printParamMessage()
        } else serverUploadService.deleteUpload(data.fid)
    }

    fun deleteNotice(data: AdminDeleteNoticeModel): ResultModel {
        return if (data.id == null || data.id <= 0) {
            ResultUtils.printParamMessage()
        } else serverNoticeService.deleteNotice(data.id)
    }

    fun deleteChat(data: AdminChatOperateModel): ResultModel {
        return if (data.sid.isNullOrEmpty()) {
            ResultUtils.printParamMessage()
        } else serverChatService.deleteChat(data.sid)
    }

    fun deleteMember(data: AdminDeleteMemberModel): ResultModel {
        return if (data.gid == null || data.gid < 0 || data.uid == null || data.uid <= 0) {
            ResultUtils.printParamMessage()
        } else serverGroupService.deleteGroupMember(data.gid, data.uid)
    }

    fun getChatContent(data: AdminChatOperateModel): ResultModel {
        return if (data.sid.isNullOrEmpty()) {
            ResultUtils.printParamMessage()
        } else serverChatService.getChatContent(data.sid)
    }

    private fun handleListRequest(serviceCall: (Int, Int) -> Any, data: CommonSearchPage): Any {
        val page = data.page
        val limit = data.limit
        return if (page == null || limit == null || page <= 0 || limit <= 0) {
            ResultUtils.printParamMessage()
        } else serviceCall(page, limit)
    }

    fun getUserList(data: CommonSearchPage) = handleListRequest(serverUserService::getUserList, data)
    fun getChatList(data: CommonSearchPage) = handleListRequest(serverChatService::getChatList, data)
    fun getLoginList(data: CommonSearchPage) = handleListRequest(serverLoginService::getLoginList, data)
    fun getGroupList(data: CommonSearchPage) = handleListRequest(serverGroupService::getGroupList, data)
    fun getReportList(data: CommonSearchPage) = handleListRequest(serverReportService::getReportList, data)
    fun getNoticeList(data: CommonSearchPage) = handleListRequest(noticeObjService::searchSystemNotice, data)
    fun getUploadList(data: CommonSearchPage) = handleListRequest(serverUploadService::getUploadList, data)

    fun updateUserPassword(data: AdminUpdateUserPasswordsModel): ResultModel {
        return if (data.uid == null || data.password.isNullOrEmpty() || data.uid <= 0) {
            ResultUtils.printParamMessage()
        } else serverUserService.updateUserPassword(
            uid = data.uid,
            password = data.password
        )
    }

    fun updateGroupStatus(data: AdminUpdateGroupStatusModel): ResultModel {
        return if (data.gid == null || data.status == null || data.gid < 0) {
            ResultUtils.printParamMessage()
        } else serverGroupService.updateGroupStatus(
            gid = data.gid,
            status = data.status
        )
    }

    fun updateUserStatus(data: AdminUpdateUserStatusModel): ResultModel {
        return if (data.uid == null || data.status == null || data.uid <= 0) {
            ResultUtils.printParamMessage()
        } else serverUserService.updateUserStatus(
            uid = data.uid,
            status = data.status.toString().toInt()
        )
    }

    fun createGroup(data: AdminCreateGroupModel, uid: Long): Any {
        return if (data.name.isNullOrEmpty() || data.avatar.isNullOrEmpty() || data.notice.isNullOrEmpty()) {
            ResultUtils.printParamMessage()
        } else serverGroupService.createGroup(
            uid = uid,
            name = data.name,
            avatar = data.avatar,
            notice = data.notice
        )
    }

    fun createNotice(data: AdminCreateNoticeModel): Any {
        return if (data.title.isNullOrEmpty() || data.content.isNullOrEmpty()) {
            ResultUtils.printParamMessage()
        } else serverNoticeService.createNotice(
            title = data.title,
            content = data.content
        )
    }

    fun updateUserInfo(data: AdminUpdateUserInfoModel): Any {
        return if (data.uid == null || data.nick.isNullOrEmpty() || data.avatar.isNullOrEmpty() || data.robot == null || data.uid <= 0) {
            ResultUtils.printParamMessage()
        } else serverUserService.updateUserInfo(
            uid = data.uid,
            nick = data.nick,
            robot = data.robot,
            avatar = data.avatar
        )
    }

    fun updateGroupInfo(data: AdminUpdateGroupInfoModel): Any {
        return if (data.gid == null || data.name.isNullOrEmpty() || data.avatar.isNullOrEmpty() || data.notice.isNullOrEmpty() || data.gid <= 0) {
            ResultUtils.printParamMessage()
        } else serverGroupService.updateGroup(
            gid = data.gid,
            name = data.name,
            avatar = data.avatar,
            notice = data.notice
        )
    }

    fun updateNoticeInfo(data: AdminUpdateNoticeModel): Any {
        return if (data.id == null || data.title.isNullOrEmpty() || data.content.isNullOrEmpty() || data.id <= 0) {
            ResultUtils.printParamMessage()
        } else serverNoticeService.updateNotice(
            id = data.id,
            title = data.title,
            content = data.content
        )
    }

    fun forceReloadClient(event: String): ResultModel {
        if (event == "[RE:HISTORY:CLEAR]") serverChatService.clearAllChatHistory()
        return HandUtils.handleResultByCode(HttpStatus.OK, null, "操作成功")
    }
}