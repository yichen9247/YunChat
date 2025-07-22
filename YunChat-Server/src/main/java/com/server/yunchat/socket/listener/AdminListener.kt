package com.server.yunchat.socket.listener

import com.corundumstudio.socketio.AckRequest
import com.corundumstudio.socketio.SocketIOClient
import com.server.yunchat.admin.service.ServerDashService
import com.server.yunchat.admin.service.ServerLogService
import com.server.yunchat.builder.data.*
import com.server.yunchat.builder.types.EventType
import com.server.yunchat.builder.types.UserAuthType
import com.server.yunchat.socket.handler.AdminHandler
import com.server.yunchat.socket.service.SocketEventListener
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class AdminListener @Autowired constructor(
    private val adminHandler: AdminHandler,
    private val serverLogService: ServerLogService,
    private val serverDashService: ServerDashService
) {
    @SocketEventListener(
        eventClass = Map::class,
        event = EventType.ADMIN_GET_SYSTEM_LOGS,
        permission = UserAuthType.ADMIN_AUTHENTICATION
    )
    fun onGetSystemLogs(client: SocketIOClient, data: Map<String, Any>, ackSender: AckRequest) {
        adminHandler.handleAdminRequest(ackSender, serverLogService::getSystemLogs)
    }

    @SocketEventListener(
        eventClass = Map::class,
        event = EventType.ADMIN_DELETE_SYSTEM_LOGS,
        permission = UserAuthType.ADMIN_AUTHENTICATION
    )
    fun onDeleteSystemLogs(client: SocketIOClient, data: Map<String, Any>, ackSender: AckRequest) {
        adminHandler.handleAdminRequest(ackSender, serverLogService::deleteSystemLogs)
    }

    @SocketEventListener(
        eventClass = Map::class,
        event = EventType.ADMIN_GET_DASHBOARD_DATA,
        permission = UserAuthType.ADMIN_AUTHENTICATION
    )
    fun onGetDashboardData(client: SocketIOClient, data: Map<String, Any>, ackSender: AckRequest) {
        adminHandler.handleAdminRequest(ackSender, serverDashService::getDashboardData)
    }

    @SocketEventListener(
        eventClass = Map::class,
        event = EventType.ADMIN_CLEAR_HISTORY,
        permission = UserAuthType.ADMIN_AUTHENTICATION
    )
    fun onClearHistory(client: SocketIOClient, data: Map<String, Any>, ackSender: AckRequest) {
        adminHandler.handleAdminRequest(ackSender) {
            adminHandler.forceReloadClient("[RE:HISTORY:CLEAR]")
        }
    }

    @SocketEventListener(
        eventClass = CommonSearchPage::class,
        event = EventType.ADMIN_GET_USER_LIST,
        permission = UserAuthType.ADMIN_AUTHENTICATION
    )
    fun onGetUserList(client: SocketIOClient, data: CommonSearchPage, ackSender: AckRequest) {
        adminHandler.handleAdminRequest(ackSender) {
            adminHandler.getUserList(data)
        }
    }

    @SocketEventListener(
        event = EventType.ADMIN_DELETE_USER,
        eventClass = AdminDeleteUserModel::class,
        permission = UserAuthType.ADMIN_AUTHENTICATION
    )
    fun onDeleteUser(client: SocketIOClient, data: AdminDeleteUserModel, ackSender: AckRequest) {
        adminHandler.handleAdminRequest(ackSender) {
            adminHandler.deleteUser(data)
        }
    }

    @SocketEventListener(
        event = EventType.ADMIN_DELETE_CHAT,
        eventClass = AdminChatOperateModel::class,
        permission = UserAuthType.ADMIN_AUTHENTICATION
    )
    fun onDeleteChat(client: SocketIOClient, data: AdminChatOperateModel, ackSender: AckRequest) {
        adminHandler.handleAdminRequest(ackSender) {
            adminHandler.deleteChat(data)
        }
    }

    @SocketEventListener(
        event = EventType.ADMIN_DELETE_REPORT,
        eventClass = AdminDeleteReportModel::class,
        permission = UserAuthType.ADMIN_AUTHENTICATION
    )
    fun onDeleteReport(client: SocketIOClient, data: AdminDeleteReportModel, ackSender: AckRequest) {
        adminHandler.handleAdminRequest(ackSender) {
            adminHandler.deleteReport(data)
        }
    }

    @SocketEventListener(
        event = EventType.ADMIN_DELETE_MEMBER,
        eventClass = AdminDeleteMemberModel::class,
        permission = UserAuthType.ADMIN_AUTHENTICATION
    )
    fun onDeleteMember(client: SocketIOClient, data: AdminDeleteMemberModel, ackSender: AckRequest) {
        adminHandler.handleAdminRequest(ackSender) {
            adminHandler.deleteMember(data)
        }
    }

    @SocketEventListener(
        event = EventType.ADMIN_DELETE_GROUP,
        eventClass = AdminGroupOperateModel::class,
        permission = UserAuthType.ADMIN_AUTHENTICATION
    )
    fun onDeleteGroup(client: SocketIOClient, data: AdminGroupOperateModel, ackSender: AckRequest) {
        adminHandler.handleAdminRequest(ackSender) {
            adminHandler.deleteGroup(data)
        }
    }

    @SocketEventListener(
        event = EventType.ADMIN_DELETE_NOTICE,
        eventClass = AdminDeleteNoticeModel::class,
        permission = UserAuthType.ADMIN_AUTHENTICATION
    )
    fun onDeleteNotice(client: SocketIOClient, data: AdminDeleteNoticeModel, ackSender: AckRequest) {
        adminHandler.handleAdminRequest(ackSender) {
            adminHandler.deleteNotice(data)
        }
    }

    @SocketEventListener(
        event = EventType.ADMIN_DELETE_UPLOAD,
        eventClass = AdminDeleteUploadModel::class,
        permission = UserAuthType.ADMIN_AUTHENTICATION
    )
    fun onDeleteUpload(client: SocketIOClient, data: AdminDeleteUploadModel, ackSender: AckRequest) {
        adminHandler.handleAdminRequest(ackSender) {
            adminHandler.deleteUpload(data)
        }
    }

    @SocketEventListener(
        event = EventType.ADMIN_ADD_GROUP,
        eventClass = AdminCreateGroupModel::class,
        permission = UserAuthType.ADMIN_AUTHENTICATION
    )
    fun onCreateGroup(client: SocketIOClient, uid: Long, data: AdminCreateGroupModel, ackSender: AckRequest) {
        adminHandler.handleAdminRequest(ackSender) {
            adminHandler.createGroup(data, uid)
        }
    }

    @SocketEventListener(
        event = EventType.ADMIN_ADD_NOTICE,
        eventClass = AdminCreateNoticeModel::class,
        permission = UserAuthType.ADMIN_AUTHENTICATION
    )
    fun onCreateNotice(client: SocketIOClient, data: AdminCreateNoticeModel, ackSender: AckRequest) {
        adminHandler.handleAdminRequest(ackSender) {
            adminHandler.createNotice(data)
        }
    }

    @SocketEventListener(
        eventClass = CommonSearchPage::class,
        event = EventType.ADMIN_GET_LOGIN_LIST,
        permission = UserAuthType.ADMIN_AUTHENTICATION
    )
    fun onGetLoginList(client: SocketIOClient, data: CommonSearchPage, ackSender: AckRequest) {
        adminHandler.handleAdminRequest(ackSender) {
            adminHandler.getLoginList(data)
        }
    }

    @SocketEventListener(
        eventClass = CommonSearchPage::class,
        event = EventType.ADMIN_GET_NOTICE_LIST,
        permission = UserAuthType.ADMIN_AUTHENTICATION
    )
    fun onGetNoticeList(client: SocketIOClient, data: CommonSearchPage, ackSender: AckRequest) {
        adminHandler.handleAdminRequest(ackSender) {
            adminHandler.getNoticeList(data)
        }
    }

    @SocketEventListener(
        eventClass = CommonSearchPage::class,
        event = EventType.ADMIN_GET_CHAT_LIST,
        permission = UserAuthType.ADMIN_AUTHENTICATION
    )
    fun onGetChatList(client: SocketIOClient, data: CommonSearchPage, ackSender: AckRequest) {
        adminHandler.handleAdminRequest(ackSender) {
            adminHandler.getChatList(data)
        }
    }

    @SocketEventListener(
        eventClass = CommonSearchPage::class,
        event = EventType.ADMIN_GET_GROUP_LIST,
        permission = UserAuthType.ADMIN_AUTHENTICATION
    )
    fun onGetGroupList(client: SocketIOClient, data: CommonSearchPage, ackSender: AckRequest) {
        adminHandler.handleAdminRequest(ackSender) {
            adminHandler.getGroupList(data)
        }
    }

    @SocketEventListener(
        eventClass = CommonSearchPage::class,
        event = EventType.ADMIN_GET_REPORT_LIST,
        permission = UserAuthType.ADMIN_AUTHENTICATION
    )
    fun onGetReportList(client: SocketIOClient, data: CommonSearchPage, ackSender: AckRequest) {
        adminHandler.handleAdminRequest(ackSender) {
            adminHandler.getReportList(data)
        }
    }

    @SocketEventListener(
        eventClass = CommonSearchPage::class,
        event = EventType.ADMIN_GET_UPLOAD_LIST,
        permission = UserAuthType.ADMIN_AUTHENTICATION
    )
    fun onGetUploadList(client: SocketIOClient, data: CommonSearchPage, ackSender: AckRequest) {
        adminHandler.handleAdminRequest(ackSender) {
            adminHandler.getUploadList(data)
        }
    }

    @SocketEventListener(
        eventClass = AdminChatOperateModel::class,
        event = EventType.ADMIN_GET_CHAT_CONTENT,
        permission = UserAuthType.ADMIN_AUTHENTICATION
    )
    fun onGetChatContent(client: SocketIOClient, data: AdminChatOperateModel, ackSender: AckRequest) {
        adminHandler.handleAdminRequest(ackSender) {
            adminHandler.getChatContent(data)
        }
    }

    @SocketEventListener(
        event = EventType.ADMIN_UPDATE_GROUP,
        eventClass = AdminUpdateGroupInfoModel::class,
        permission = UserAuthType.ADMIN_AUTHENTICATION
    )
    fun onUpdateGroup(client: SocketIOClient, data: AdminUpdateGroupInfoModel, ackSender: AckRequest) {
        adminHandler.handleAdminRequest(ackSender) {
            adminHandler.updateGroupInfo(data)
        }
    }

    @SocketEventListener(
        event = EventType.ADMIN_UPDATE_NOTICE,
        eventClass = AdminUpdateNoticeModel::class,
        permission = UserAuthType.ADMIN_AUTHENTICATION
    )
    fun onUpdateNotice(client: SocketIOClient, data: AdminUpdateNoticeModel, ackSender: AckRequest) {
        adminHandler.handleAdminRequest(ackSender) {
            adminHandler.updateNoticeInfo(data)
        }
    }

    @SocketEventListener(
        event = EventType.ADMIN_UPDATE_USER,
        eventClass = AdminUpdateUserInfoModel::class,
        permission = UserAuthType.ADMIN_AUTHENTICATION
    )
    fun onUpdateUserInfo(client: SocketIOClient, data: AdminUpdateUserInfoModel, ackSender: AckRequest) {
        adminHandler.handleAdminRequest(ackSender) {
            adminHandler.updateUserInfo(data)
        }
    }

    @SocketEventListener(
        event = EventType.ADMIN_UPDATE_USER_PASSWORD,
        permission = UserAuthType.ADMIN_AUTHENTICATION,
        eventClass = AdminUpdateUserPasswordsModel::class
    )
    fun onUpdateUserPassword(client: SocketIOClient, data: AdminUpdateUserPasswordsModel, ackSender: AckRequest) {
        adminHandler.handleAdminRequest(ackSender) {
            adminHandler.updateUserPassword(data)
        }
    }

    @SocketEventListener(
        event = EventType.ADMIN_UPDATE_USER_STATUS,
        permission = UserAuthType.ADMIN_AUTHENTICATION,
        eventClass = AdminUpdateUserStatusModel::class
    )
    fun onUpdateUserStatus(client: SocketIOClient, data: AdminUpdateUserStatusModel, ackSender: AckRequest) {
        adminHandler.handleAdminRequest(ackSender) {
            adminHandler.updateUserStatus(data)
        }
    }

    @SocketEventListener(
        event = EventType.ADMIN_UPDATE_GROUP_STATUS,
        permission = UserAuthType.ADMIN_AUTHENTICATION,
        eventClass = AdminUpdateGroupStatusModel::class
    )
    fun onUpdateGroupStatus(client: SocketIOClient, data: AdminUpdateGroupStatusModel, ackSender: AckRequest) {
        adminHandler.handleAdminRequest(ackSender) {
            adminHandler.updateGroupStatus(data)
        }
    }
}