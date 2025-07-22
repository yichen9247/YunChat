import utils from "@/scripts/utils";
import socket from "@/socket/config";
import { EventType } from "@/enums/EventType";
import { io, Socket } from "socket.io-client";
import { ToastType } from "@/enums/ToastType";
import { SocketService } from "../SocketService";
import { ClientServiceImpl } from "./ClientServiceImpl";
import { SocketIOClient } from "@/socket/SocketIOClient";
import { socketIOClientInstance } from "../ServiceInstance";
import { restfulType, groupInfoType, userInfoType, messageType } from "../../../types";

/**
 * @name 通讯服务实现类
 * @author yichen9247 
 */
export class SocketServiceImpl implements SocketService {
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

    /**
     * @name 启动socket实例
     * @returns Socket
     */
    startSocketIo(): Socket {
        const clientServiceImpl = new ClientServiceImpl();
        const tokenHeaders = clientServiceImpl.getClientHeader();
        return io(socket.server.config.serverIP, {
			auth: (token) => {
				token(tokenHeaders);
			}
		});
    }
    
    /**
     * @name 获取频道信息
     * @param gid 频道
     * @param callback 回调
     */
    getChanInfo(gid: number, callback: (response: restfulType<groupInfoType>) => void): void {
        this.sendMessage({ gid }, EventType.SEARCH_GROUP, callback);
    }

    /**
     * @name 获取用户列表
     * @param callback 回调
     */
    getUserList(callback: (response: restfulType<userInfoType[]>) => void): void {
        this.sendMessage({}, EventType.GET_USER_LIST, callback);
    }

    /**
     * @name 获取群聊列表
     * @param callback 回调
     */
    getGroupList(callback: (response: restfulType<groupInfoType[]>) => void): void {
        this.sendMessage({}, EventType.GET_GROUP_LIST, callback);
    }

    /**
     * @name 获取消息列表
     * @param gid 频道
     * @param callback 回调 
     */
    getMessageList(obj: number, tar: number, callback: (response: restfulType<messageType[]>) => void): void {
        this.sendMessage({ obj, tar }, EventType.GET_MESSAGE_LIST, callback);
    }

    /**
     * @name 批量添加SocketIO事件监听
     * @param eventHandlers 事件处理器
     */
    addSocketEventListener(eventHandlers: Record<string, (response: restfulType<any>) => void | Promise<void>>): void {
        for (const [event, handler] of Object.entries(eventHandlers)) {
            SocketIOClient.socketIO.on(event, handler);
        }
    }

    /**
     * @name 加入群聊房间
     * @param gid 群聊
     * @param callback 回调 
     */
    joinGroupRoom(gid: number, callback: (response: restfulType<groupInfoType>) => void): void {
        this.sendMessage({ 
            data: gid,
            type: 'group'
        }, EventType.JOIN_ROOM, callback);
    }

    /**
     * @name 离开群聊房间
     * @param gid 群聊
     * @param callback 回调
     */
    leaveGroupRoom(gid: number, callback: (response: restfulType<groupInfoType>) => void): void {
        this.sendMessage({ 
            data: gid,
            type: 'group'
        }, EventType.LEAVE_ROOM, callback);
    }

    /**
     * @name 加入消息房间
     * @param list 房间列表
     */
    joinMessageRoom(list: number[], callback?: (response: restfulType<any>) => void): void {
        this.sendMessage({ list, type: 'group' }, EventType.JOIN_MSG_ROOM, callback);
    }
}