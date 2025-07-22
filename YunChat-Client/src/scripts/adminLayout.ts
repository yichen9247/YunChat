import MsgManner from "../frame/admin/MsgManner.vue"
import LoginLogs from "../frame/admin/LoginLogs.vue"
import ChanManner from "../frame/admin/GroupManner.vue"
import UserManner from "../frame/admin/UserManner.vue"
import DashManner from "../frame/admin/DashManner.vue"
import SystemLogs from "../frame/admin/SystemLogs.vue"
import ReportManner from "../frame/admin/ReportManner.vue"
import NoticeManner from "../frame/admin/NoticeManner.vue"
import UploadManner from "../frame/admin/UploadManner.vue"
import SystemManner from "../frame/admin/SystemManner.vue"

export const adminViews = reactive([{
    key: 'dash',
    name: '系统概览',
    component: markRaw(DashManner)
}, {
    key: 'user',
    name: '用户管理',
    component: markRaw(UserManner)
}, {
    key: 'msg',
    name: '消息管理',
    component: markRaw(MsgManner)
}, {
    key: 'group',
    name: '群聊管理',
    component: markRaw(ChanManner)
}, {
    key: 'notice',
    name: '公告管理',
    component: markRaw(NoticeManner)
}, {
    key: 'report',
    name: '举报管理',
    component: markRaw(ReportManner)
}, {
    key: 'upload',
    name: '上传管理',
    component: markRaw(UploadManner)
}, {
    key: 'logs',
    name: '系统日志',
    component: markRaw(SystemLogs)
}, {
    key: 'login',
    name: '登录日志',
    component: markRaw(LoginLogs)
}, {
    key: 'system',
    name: '系统管理',
    component: markRaw(SystemManner)
}]);