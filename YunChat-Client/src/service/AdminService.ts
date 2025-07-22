import { adminGroupFormType, adminDashInfoType, adminNoticeFormType, adminUserFormType, arrayDataType, forumPostType, groupInfoType, loginLogType, messageType, noticeBoardType, reportFormType, restfulType, systemInfoType, uploadInfoType, userInfoType } from "../../types";

export interface AdminService {
    getLogsList(callback?: (response: restfulType<string>) => void): void;
    getDashData(callback?: (response: restfulType<adminDashInfoType>) => void): void;
    getChatContent(sid: string, callback?: (response: restfulType<messageType>) => void): void;
    getSystemConfigList(callback?: (response: restfulType<systemInfoType[]>) => void): void;

    getChatList(page: number, limit: number, callback?: (response: restfulType<arrayDataType<messageType>>) => void): void;
    getUserList(page: number, limit: number, callback?: (response: restfulType<arrayDataType<userInfoType>>) => void): void;
    getGroupList(page: number, limit: number, callback?: (response: restfulType<arrayDataType<groupInfoType>>) => void): void;
    getLoginList(page: number, limit: number, callback?: (response: restfulType<arrayDataType<loginLogType>>) => void): void;
    getReportList(page: number, limit: number, callback?: (response: restfulType<arrayDataType<reportFormType>>) => void): void;
    getUploadList(page: number, limit: number, callback?: (response: restfulType<arrayDataType<uploadInfoType>>) => void): void;
    getNoticeList(page: number, limit: number, callback?: (response: restfulType<arrayDataType<noticeBoardType>>) => void): void;

    deleteLogs(callback?: (response: restfulType<any>) => void): void;
    deleteUser(uid: number, callback?: (response: restfulType<any>) => void): void;
    deleteChat(sid: string, callback?: (response: restfulType<any>) => void): void;
    deleteGroup(gid: number, callback?: (response: restfulType<any>) => void): void;
    deleteReport(rid: number, callback?: (response: restfulType<any>) => void): void;
    deleteNotice(id: number, callback?: (response: restfulType<any>) => void): void;
    deleteUpload(fid: string, callback?: (response: restfulType<any>) => void): void;
    deleteMember(uid: number, gid: number, callback?: (response: restfulType<any>) => void): void
    
    systemConnectSend(event: string, callback?: (response: restfulType<any>) => void): void;

    createGroup(info: adminGroupFormType, callback?: (response: restfulType<any>) => void): void;
    createNotice(info: adminNoticeFormType, callback?: (response: restfulType<any>) => void): void;

    //----------- Update:User -----------//
    updateUserInfo(info: adminUserFormType, callback?: (response: restfulType<any>) => void): void;
    updateUserPassword(uid: number, password: string, callback?: (response: restfulType<any>) => void): void;
    updateUserTabooStatus(uid: number, status: number, callback?: (response: restfulType<any>) => void): void;

    //----------- Update:Group -----------//
    updateGroupInfo(info: adminGroupFormType, callback?: (response: restfulType<any>) => void): void;
    updateGroupTabooStatus(gid: number, status: number, callback?: (response: restfulType<any>) => void): void;

    //----------- Update:Notice -----------//
    updateNoticeInfo(info: adminNoticeFormType, callback?: (response: restfulType<any>) => void): void;

    //----------- Update:System -----------//
    updateSystemConfig(name: string, value: string, callback?: (response: restfulType<systemInfoType>) => void): void;
}