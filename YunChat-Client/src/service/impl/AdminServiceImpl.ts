import utils from '@/scripts/utils';
import { EventType } from '@/enums/EventType';
import { ToastType } from '@/enums/ToastType';
import { AdminService } from '../AdminService';
import { socketIOClientInstance } from '../ServiceInstance';
import { restfulType, adminDashInfoType, arrayDataType, messageType, userInfoType, groupInfoType, reportFormType, uploadInfoType, noticeBoardType, adminGroupFormType, adminNoticeFormType, adminUserFormType, systemInfoType, loginLogType } from '../../../types';

/**
 * 管理服务实现类
 * @author yichen9247
 */
export class AdminServiceImpl implements AdminService {
    // 统一消息内容发送
    private sendMessage(
        data: object,
        event: string,
        callback?: (response: restfulType<any>) => void,
    ): void {
        socketIOClientInstance.sendSocketEmit({
            event, data,
            callback: this.createResponseHandler(callback)
        });
    }

    // 统一消息响应处理
    private createResponseHandler(callback?: (response: restfulType<any>) => void) {
        return async (response: restfulType<any>): Promise<void> => {
            if (response.code !== 200) {
                utils.showToasts(ToastType.ERROR, response.message);
                return;
            }
            callback?.(response);
        };
    }

    getLogsList(callback?: (response: restfulType<string>) => void): void {
        this.sendMessage({}, EventType.GET_ADMIN_SYSTEM_LOGS, callback);
    }
    getDashData(callback?: (response: restfulType<adminDashInfoType>) => void): void {
        this.sendMessage({}, EventType.GET_ADMIN_DASH_DATA, callback);
    }
    getSystemConfigList(callback?: (response: restfulType<systemInfoType[]>) => void): void {
        this.sendMessage({}, EventType.GET_SYSTEM_CONFIG, callback);
    }
    getChatContent(sid: string, callback?: (response: restfulType<messageType>) => void): void {
        this.sendMessage({ sid }, EventType.GET_ADMIN_CHAT_CONTENT, callback);
    }

    getChatList(page: number, limit: number, callback?: (response: restfulType<arrayDataType<messageType>>) => void): void {
        this.sendMessage({ page, limit }, EventType.GET_ADMIN_CHAT_LIST, callback);
    }
    getUserList(page: number, limit: number, callback?: (response: restfulType<arrayDataType<userInfoType>>) => void): void {
        this.sendMessage({ page, limit }, EventType.GET_ADMIN_USER_LIST, callback);
    }
    getGroupList(page: number, limit: number, callback?: (response: restfulType<arrayDataType<groupInfoType>>) => void): void {
        this.sendMessage({ page, limit }, EventType.GET_ADMIN_GROUP_LIST, callback);
    }
    getLoginList(page: number, limit: number, callback?: (response: restfulType<arrayDataType<loginLogType>>) => void): void {
        this.sendMessage({ page, limit }, EventType.GET_ADMIN_LOGIN_LIST, callback);
    }
    getReportList(page: number, limit: number, callback?: (response: restfulType<arrayDataType<reportFormType>>) => void): void {
        this.sendMessage({ page, limit }, EventType.GET_ADMIN_REPORT_LIST, callback);
    }
    getUploadList(page: number, limit: number, callback?: (response: restfulType<arrayDataType<uploadInfoType>>) => void): void {
        this.sendMessage({ page, limit }, EventType.GET_ADMIN_UPLIOAD_LIST, callback);
    }
    getNoticeList(page: number, limit: number, callback?: (response: restfulType<arrayDataType<noticeBoardType>>) => void): void {
        this.sendMessage({ page, limit }, EventType.GET_ADMIN_NOTICE_LIST, callback);
    }
    
    deleteLogs(callback?: (response: restfulType<any>) => void): void {
        this.sendMessage({}, EventType.DELTE_ADMIN_SYSTEM_LOGS, callback);
    }
    systemConnectSend(event: string, callback?: (response: restfulType<any>) => void): void {
        this.sendMessage({}, event, callback);
    }
    deleteUser(uid: number, callback?: (response: restfulType<any>) => void): void {
        this.sendMessage({ uid }, EventType.DELTE_ADMIN_USER, callback);
    }
    deleteGroup(gid: number, callback?: (response: restfulType<any>) => void): void {
        this.sendMessage({ gid }, EventType.DELTE_ADMIN_GROUP, callback);
    }
    deleteChat(sid: string, callback?: (response: restfulType<any>) => void): void {
        this.sendMessage({ sid }, EventType.DELTE_ADMIN_CHAT, callback);
    }
    deleteReport(rid: number, callback?: (response: restfulType<any>) => void): void {
        this.sendMessage({ rid }, EventType.DELTE_ADMIN_REPORT, callback);
    }
    deleteNotice(id: number, callback?: (response: restfulType<any>) => void): void {
        this.sendMessage({ id }, EventType.DELTE_ADMIN_NOTICE, callback);
    }
    deleteUpload(fid: string, callback?: (response: restfulType<any>) => void): void {
        this.sendMessage({ fid }, EventType.DELTE_ADMIN_UPLOAD, callback); 
    }
    deleteMember(uid: number, gid: number, callback?: (response: restfulType<any>) => void): void {
        this.sendMessage({ uid, gid }, EventType.DELTE_ADMIN_MEMBER, callback); 
    }

    createGroup(info: adminGroupFormType, callback?: (response: restfulType<any>) => void): void {
        this.sendMessage(info, EventType.CREATE_ADMIN_GROUP, callback);
    }
    createNotice(info: adminNoticeFormType, callback?: (response: restfulType<any>) => void): void {
        this.sendMessage(info, EventType.CREATE_ADMIN_NOTICE, callback);
    }

    //----------- Update:User -----------//
    updateUserInfo(info: adminUserFormType, callback?: (response: restfulType<any>) => void): void {
        this.sendMessage(info, EventType.UPDATE_USER, callback);
    }
    updateUserPassword(uid: number, password: string, callback?: (response: restfulType<any>) => void): void {
        this.sendMessage({ uid, password }, EventType.UPDATE_ADMIN_USER_PASSWORD, callback);
    }
    updateUserTabooStatus(uid: number, status: number, callback?: (response: restfulType<any>) => void): void {
        this.sendMessage({ uid, status }, EventType.UPDATE_ADMIN_USER_STATUS, callback);
    }
    
    //----------- Update:Group -----------//
    updateGroupInfo(info: adminGroupFormType, callback?: (response: restfulType<any>) => void): void {
        this.sendMessage(info, EventType.UPDATE_ADMIN_GROUP, callback);
    }
    updateGroupTabooStatus(gid: number, status: number, callback?: (response: restfulType<any>) => void): void {
        this.sendMessage({ gid, status }, EventType.UPDATE_ADMIN_GROUP_STATUS, callback);
    }

    //----------- Update:Notice -----------//
    updateNoticeInfo(info: adminNoticeFormType, callback?: (response: restfulType<any>) => void): void {
        this.sendMessage(info, EventType.UPDATE_ADMIN_NOTICE, callback);
    }

    //----------- Update:System -----------//
    updateSystemConfig(name: string, value: string, callback?: (response: restfulType<systemInfoType>) => void): void {
        this.sendMessage({ name, value }, EventType.UPDATE_SYSTEM_CONFIG_VALUE, callback);
    }
}