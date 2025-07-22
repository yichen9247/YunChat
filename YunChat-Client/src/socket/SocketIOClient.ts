import Swal from "sweetalert2"
import { isDefined } from "@vueuse/core"
import { Socket } from "socket.io-client"
import HandUtils from "@/scripts/HandUtils"
import { EventType } from "@/enums/EventType"
import { useApplicationStore } from "@/stores/applicationStore"
import { DialogServiceImpl } from "@/service/impl/DialogServiceImpl"
import { groupInfoType, messageType, restfulType, userInfoType } from "../../types"
import { clientServiceInstance, socketHandlerInstance, socketServiceInstance, userServiceInstance } from "@/service/ServiceInstance"

/**
 * @name Socket.IO实例核心类
 * @author yichen9247
 */
export class SocketIOClient {
    private pingInterval = null;
    static socketIO: Socket = null;
    static applicationStore = null;
    static tempToken: string = null;
    static dialogServiceImpl: DialogServiceImpl;

    startSocketIo(): void {
        const socketIo = SocketIOClient.socketIO;
        if (socketIo) SocketIOClient.socketIO.disconnect();
        if (this.pingInterval) clearInterval(this.pingInterval);
        SocketIOClient.applicationStore = useApplicationStore();
        SocketIOClient.dialogServiceImpl = new DialogServiceImpl();
        SocketIOClient.socketIO = socketServiceInstance.startSocketIo();
        this.addSocketEventListener()
        this.addCustomEventListener()
    }

    /**
     * @name 发送事件包
     * @param param0 参数列表
     */
    sendSocketEmit({ event, data, callback }): void {
        SocketIOClient.socketIO.emit(event, data, async (response: restfulType<any>): Promise<void> => {
            if (response.code === 403) return HandUtils.removeClientLoginStatus();
            if (response.code === 426) {
                SocketIOClient.socketIO.disconnect();
                return socketHandlerInstance.onClientVersionError();
            }
            await callback(response);
        });
    }

    /**
     * @name 加入聊天群聊
     * @param gid 群聊
     */
    joinRoom(gid: number, callback?: () => void,): void {
        socketServiceInstance.joinGroupRoom(gid, (response: restfulType<groupInfoType>) => {
            SocketIOClient.applicationStore.groupInfo = response.data;
            this.initClientMessageList(0, response.data.gid);
            callback?.();
        });
    }

    /**
     * @name 切换聊天群聊
     * @param gid 群聊
     */
    toggleChatObject(gid: number): void {
        const { groupInfo, unReadMessage } = SocketIOClient.applicationStore
        const currentGid = groupInfo.gid;
        // if (currentGid === gid) return;
        socketServiceInstance.leaveGroupRoom(currentGid, () => {
            this.joinRoom(gid);
        });
        SocketIOClient.applicationStore.unReadMessage = unReadMessage.filter(
            item => item.gid !== gid
        ); // 重置未读消息
    }

    /**
     * @name 程序初始化
     */
    private socketClientInit(): void {
        if (isDefined(localStorage.getItem("yun_uid")) && isDefined(localStorage.getItem("yun_token"))) {
            clientServiceInstance.clientInit((response: restfulType<userInfoType>): void => {
                SocketIOClient.applicationStore.setLoginStatus(true);
                SocketIOClient.applicationStore.userInfo = response.data;
                userServiceInstance.userOnlineLogin(1);
            });
        }
    }

    /**
     * @name 初始化用户列表
     */
    initClientUserList(): void {
        socketServiceInstance.getUserList((response: restfulType<userInfoType[]>): void => {
            SocketIOClient.applicationStore.userList = response.data;
        });
    }

    /**
     * @name 初始化群聊列表
     */
    initClientGroupList(): void {
        socketServiceInstance.getGroupList((response: restfulType<groupInfoType[]>): void => {
            let roomList = [];
            const groupList = response.data;
            for(let i in groupList) roomList.push(groupList[i].gid);
            socketServiceInstance.joinMessageRoom(roomList);
            SocketIOClient.applicationStore.chatGroupList = response.data;
            this.toggleChatObject(1);
        });
    }

    /**
     * @name 初始化消息列表
     */
    initClientMessageList(obj: number, tar: number): void {
        socketServiceInstance.getMessageList(obj, tar, (response: restfulType<messageType[]>): void => {
            SocketIOClient.applicationStore.messageList = response.data;
        });
    }

    /**
     * @name 启动内置事件监听
     */
    private addSocketEventListener(): void {
        SocketIOClient.socketIO.on(EventType.CONNECT, (): void => this.onSocketIoConnect());
        SocketIOClient.socketIO.on(EventType.DISCONNECT, (): void => SocketIOClient.applicationStore.setConnection(false));
    }

    private onSocketIoConnect(): void {
        Promise.all([
            Swal.close(),
            this.socketClientInit(),
            this.initClientUserList(),
            this.initClientGroupList(),
        ]);
        SocketIOClient.applicationStore.setConnection(true);
        this.pingInterval = setInterval((): void => clientServiceInstance.clientPing(), 3000);
    }

    /**
     * @name 启动自定义监听器
     */
    private addCustomEventListener(): void {
        socketServiceInstance.addSocketEventListener({
            [EventType.RECE_RE_USER_ALL]: this.initClientUserList,
            [EventType.RECE_CLIENT_TOKEN]: socketHandlerInstance.onReceiveClientToken,
            [EventType.RECE_RE_USER_NICK]: socketHandlerInstance.onReceiveUserNickUpdate,
            [EventType.RECE_CLIENT_ONLINE]: socketHandlerInstance.onReceiveOnlineUserList,
            [EventType.RECE_CLIENT_MESSAGE]: socketHandlerInstance.onReceviceClientMessage,
            [EventType.RECE_RE_USER_AVATAR]: socketHandlerInstance.onReceiveUserAvatarUpdate,
            [EventType.RECE_AI_CREATE_MESSAGE]: socketHandlerInstance.onReceviceAiChatMessage,
            [EventType.RECE_CLIENT_NEW_MESSAGE]: socketHandlerInstance.onReceviceNewClientMessage,
        });
    }
}