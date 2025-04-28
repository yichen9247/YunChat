package com.server.handsock.socket.handler

import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.corundumstudio.socketio.SocketIOServer
import com.server.handsock.admin.service.*
import com.server.handsock.common.data.AdminMannerModel
import com.server.handsock.common.service.ForumService
import com.server.handsock.common.service.NoticeService
import com.server.handsock.common.utils.HandUtils
import com.server.handsock.service.AuthService
import com.server.handsock.socket.eventer.OnlineEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AdminHandler @Autowired constructor(
    private val authService: AuthService,
    private val forumService: ForumService,
    private val noticeService: NoticeService,
    private val serverUserService: ServerUserService,
    private val serverChatService: ServerChatService,
    private val serverForumService: ServerForumService,
    private val serverNoticeService: ServerNoticeService,
    private val serverUploadService: ServerUploadService,
    private val serverReportService: ServerReportService,
    private val serverBannerService: ServerBannerService,
    private val serverChannelService: ServerChannelService,
    private val serverCommentService: ServerCommentService
) {
    fun handleAdminRequest(client: SocketIOClient, ackSender: AckRequest, call: () -> Any) {
        ackSender.sendAckData(authService.validAdminStatusBySocket(client) {
            call()
        })
    }

    fun deleteUser(data: AdminMannerModel): Any {
        return if (data.uid == null || data.uid <= 0) {
            HandUtils.printParamError()
        } else serverUserService.deleteUser(data.uid)
    }

    fun deleteChan(data: AdminMannerModel): Any {
        return if (data.gid == null || data.gid < 0) {
            HandUtils.printParamError()
        } else serverChannelService.deleteChan(data.gid)
    }

    fun deleteRepo(data: AdminMannerModel): Any {
        return if (data.rid == null || data.rid <= 0) {
            HandUtils.printParamError()
        } else serverReportService.deleteReport(data.rid)
    }

    fun deletePost(data: AdminMannerModel): Any {
        return if (data.pid == null || data.pid <= 0) {
            HandUtils.printParamError()
        } else serverForumService.deleteForumPost(data.pid)
    }

    fun deleteComment(data: AdminMannerModel): Any {
        return if (data.cid == null || data.cid <= 0) {
            HandUtils.printParamError()
        } else serverCommentService.deleteForumComment(data.cid)
    }

    fun deleteUpload(data: AdminMannerModel): Any {
        return if (data.fid.isNullOrEmpty()) {
            HandUtils.printParamError()
        } else serverUploadService.deleteUpload(data.fid)
    }

    fun deleteBanner(data: AdminMannerModel): Any {
        return if (data.bid == null || data.bid <= 0) {
            HandUtils.printParamError()
        } else serverBannerService.deleteBanner(data.bid)
    }

    fun deleteNotice(data: AdminMannerModel): Any {
        return if (data.nid == null || data.nid <= 0) {
            HandUtils.printParamError()
        } else serverNoticeService.deleteNotice(data.nid)
    }

    fun deleteChat(data: AdminMannerModel): Any {
        return if (data.sid.isNullOrEmpty()) {
            HandUtils.printParamError()
        } else serverChatService.deleteChat(data.sid)
    }

    fun getChatContent(data: AdminMannerModel): Any {
        return if (data.sid.isNullOrEmpty()) {
            HandUtils.printParamError()
        } else serverChatService.getChatContent(data.sid)
    }

    private fun handleListRequest(serviceCall: (Int, Int) -> Any, data: AdminMannerModel): Any {
        val page = data.page
        val limit = data.limit
        return if (page == null || limit == null || page <= 0 || limit <= 0) {
            HandUtils.printParamError()
        } else serviceCall(page, limit)
    }

    fun getPostList(data: AdminMannerModel) = handleListRequest(forumService::searchForumPost, data)
    fun getUserList(data: AdminMannerModel) = handleListRequest(serverUserService::getUserList, data)
    fun getChatList(data: AdminMannerModel) = handleListRequest(serverChatService::getChatList, data)
    fun getChanList(data: AdminMannerModel) = handleListRequest(serverChannelService::getChanList, data)
    fun getRepoList(data: AdminMannerModel) = handleListRequest(serverReportService::getReportList, data)
    fun getNoticeList(data: AdminMannerModel) = handleListRequest(noticeService::searchSystemNotice, data)
    fun getBannerList(data: AdminMannerModel) = handleListRequest(serverBannerService::getBannerList, data)
    fun getUploadList(data: AdminMannerModel) = handleListRequest(serverUploadService::getUploadList, data)
    fun getCommentList(data: AdminMannerModel) = handleListRequest(serverCommentService::getCommentList, data)

    fun updateUserPassword(data: AdminMannerModel): Any {
        return if (data.uid == null || data.password.isNullOrEmpty() || data.uid <= 0) {
            HandUtils.printParamError()
        } else serverUserService.updateUserPassword(
            uid = data.uid,
            password = data.password
        )
    }

    fun updateChanOpenStatus(data: AdminMannerModel): Any {
        return if (data.gid == null || data.status == null || data.gid < 0) {
            HandUtils.printParamError()
        } else serverChannelService.updateChanOpenStatus(
            gid = data.gid,
            status = data.status.toString().toInt()
        )
    }

    fun updateChanActiveStatus(data: AdminMannerModel): Any {
        return if (data.gid == null || data.status == null || data.gid < 0) {
            HandUtils.printParamError()
        } else serverChannelService.updateChanActiveStatus(
            gid = data.gid,
            status = data.status.toString().toInt()
        )
    }

    fun updateUserStatus(data: AdminMannerModel): Any {
        return if (data.uid == null || data.status == null || data.uid <= 0) {
            HandUtils.printParamError()
        } else serverUserService.updateUserStatus(
            uid = data.uid,
            status = data.status.toString().toInt()
        )
    }

    fun createChan(data: AdminMannerModel): Any {
        return if (data.gid == null || data.name.isNullOrEmpty() || data.avatar.isNullOrEmpty() || data.notice.isNullOrEmpty() || data.aiRole == null || data.gid < 0) {
            HandUtils.printParamError()
        } else serverChannelService.createChan(
            gid = data.gid,
            name = data.name,
            avatar = data.avatar,
            notice = data.notice,
            aiRole = data.aiRole
        )
    }

    fun createBanner(data: AdminMannerModel): Any {
        return if (data.name.isNullOrEmpty() || data.href.isNullOrEmpty() || data.image.isNullOrEmpty()) {
            HandUtils.printParamError()
        } else serverBannerService.createBanner(
            name = data.name,
            href = data.href,
            image = data.image
        )
    }

    fun createNotice(data: AdminMannerModel): Any {
        return if (data.title.isNullOrEmpty() || data.content.isNullOrEmpty()) {
            HandUtils.printParamError()
        } else serverNoticeService.createNotice(
            title = data.title,
            content = data.content
        )
    }

    fun updateUserInfo(data: AdminMannerModel): Any {
        return if (data.uid == null || data.nick.isNullOrEmpty() || data.avatar.isNullOrEmpty() || data.username.isNullOrEmpty() || data.robot == null || data.uid <= 0) {
            HandUtils.printParamError()
        } else serverUserService.updateUserInfo(
            uid = data.uid,
            nick = data.nick,
            robot = data.robot,
            avatar = data.avatar,
            username = data.username
        )
    }

    fun updateChanInfo(data: AdminMannerModel): Any {
        return if (data.gid == null || data.name.isNullOrEmpty() || data.avatar.isNullOrEmpty() || data.notice.isNullOrEmpty() || data.aiRole == null || data.gid < 0) {
            HandUtils.printParamError()
        } else serverChannelService.updateChan(
            gid = data.gid,
            name = data.name,
            avatar = data.avatar,
            notice = data.notice,
            aiRole = data.aiRole
        )
    }

    fun updateBannerInfo(data: AdminMannerModel): Any {
        return if (data.bid == null || data.name.isNullOrEmpty() || data.href.isNullOrEmpty() || data.image.isNullOrEmpty() || data.bid <= 0) {
            HandUtils.printParamError()
        } else serverBannerService.updateBanner(
            bid = data.bid,
            name = data.name,
            href = data.href,
            image = data.image,
        )
    }

    fun updateNoticeInfo(data: AdminMannerModel): Any {
        return if (data.nid == null || data.title.isNullOrEmpty() || data.content.isNullOrEmpty() || data.nid <= 0) {
            HandUtils.printParamError()
        } else serverNoticeService.updateNotice(
            nid = data.nid,
            title = data.title,
            content = data.content
        )
    }

    fun forceReloadClient(server: SocketIOServer?, event: String, onlineEvent: OnlineEvent): Any {
        if (event == "[RE:HISTORY:CLEAR]") serverChatService.clearAllChatHistory()
        onlineEvent.clearClient()
        HandUtils.sendGlobalMessage(server!!, event, null)
        return mapOf(
            "code" to 200,
            "message" to "指令以发出"
        )
    }
}