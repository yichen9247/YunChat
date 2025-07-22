export interface restfulType<T> {
    data: T,
    code: number,
    message: string,
    timestamp: number
}

export interface arrayDataType<T> {
    total: number,
    items: Array<T>
}

export interface userInfoType {
    uid: string,
    nick: string,
    avatar: string,
    status: number,
    username: string
    permission: number
}

export interface ChatItem {
    gid: number;
    uid: number;
    type: string;
    num?: number;
    content: string;
}

export interface groupInfoType {
    gid: number,
    name: string,
    time: string,
    status: number,
    avatar: string,
    notice: string,
    message: messageType[],
    member: userInfoType[]
}

export interface uploadInfoType {
    uid: number,
    time: string,
    name: string,
    size: string,
    type: string,
    path: string
}

export interface messageType {
    obj: number,
    tar: number,
    uid: string,
    sid: string,
    type: string,
    time: string,
    deleted: number,
    content: string
}

export interface onlineUserType {
    uid: string,
    platform: string
}

export interface loginFormType {
    username: string,
    password: string
}

export interface loginLogType {
    id: number,
    uid: number,
    time: string,
    address: string,
    platform: string,
    username: string
}

export interface reportFormType {
    sid: string,
    reason: string,
    reportedId: string
}

export interface addPostFormType {
    title: string,
    image: string[],
    content: string
}

export interface addPostCommentType {
    pid: number,
    content: string,
    parent: null | number
}

export interface sidebarListType {
    call: any,
    key: string,
    name: string,
    icon: string
}

export interface adminGroupFormType {
    gid: number,
    name: string,
    notice: string,
    avatar: string,
}

export interface adminNoticeFormType {
    id: number,
    title: string,
    content: string
}

export interface adminUserFormType {
    uid: string,
    nick: string,
    robot: boolean,
    avatar: string,
    username: string,
    permission: number
}

export interface adminDashInfoType {
    userTotal: number,
    groupTotal: number,
    todayRegUser: number,
    todayChatTotal: number,
    systemOsInfo: adminSystemInfoType
}

export interface adminSystemInfoType {
    osInfo: string,
    osArch: string,
    locale: string,
    hostName: string,
    appVersion: string,
    timeZoneId: string,
    hostAddress: string,
    systemUptime: string,
    logicalCount: number,
    memoryUsageInfo: string
}

export interface adminSystemMannerType {
    taboo: boolean,
    upload: boolean,
    register: boolean,
    version: string,
    download: string,
    welcome: boolean,
    ai_url: string,
    ai_role: string,
    ai_model: string,
    ai_token: string,
}

export interface aiEventHandleType {
    event: string,
    eventId: string,
    content: string,
    result: messageType
}

export interface systemInfoType {
    name: string,
    value: string,
    status: string
}

export interface noticeBoardType {
    id: number,
    time: string,
    title: string,
    content: string
}

export interface forumPostType {
    pid: number,
    uid: number,
    time: string,
    title: string,
    image: string,
    content: string,
    user: userInfoType
}

export interface uploadStatusType {
    path: string
}

export interface userAuthStatus {
    token: string,
    userinfo: userInfoType
}

export interface forumCommentType {
    pid: number,
    cid: number,
    time: string,
    content: string,
    user: userInfoType,
    children: forumCommentType[]
}

export interface loginLogType {
    uid: number,
    time: string,
    address: string,
    platform: string,
    location: string,
    username: string
}

export interface lastMessageType {
    gid: number,
    content: messageType
}

export interface unReadMesageType {
    gid: number,
    num: number
}