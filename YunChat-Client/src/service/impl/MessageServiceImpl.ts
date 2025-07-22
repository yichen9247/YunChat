import utils from "@/scripts/utils";
import { EventType } from "@/enums/EventType";
import { ToastType } from "@/enums/ToastType";
import { MessageService } from "../MessageService";
import { SocketIOClient } from "@/socket/SocketIOClient";
import { groupInfoType, messageType, restfulType } from "../../../types";
import { socketIOClientInstance } from "../ServiceInstance";

/**
 * @name 消息服务实现类
 * @author yichen9247
 */
export class MessageServiceImpl implements MessageService {
    // 统一发送消息目标
    private sendMessage(
        event: string,
        content: string,
        type: 'text' | 'image' | 'file' | 'video' | 'invite',
        callback?: (response: restfulType<any>) => void,
    ): void {
        if (!this.validateContent(content)) return;
        socketIOClientInstance.sendSocketEmit({
            event, data: { type, content, 
                obj: 0,
                tar: SocketIOClient.applicationStore.groupInfo.gid
            },
            callback: this.createResponseHandler(callback)
        });
    }

    // 统一消息内容校验
    private validateContent(content: string): boolean {
        const trimmedContent = content?.trim();

        if (!trimmedContent) {
            utils.showToasts(ToastType.WARNING, '内容不能为空');
            return false;
        }
        return true;
    }

    // 统一消息响应处理
    private createResponseHandler(callback?: (response: restfulType<messageType>) => void) {
        return async (response: restfulType<any>): Promise<void> => {
            if (response.code !== 200) {
                utils.showToasts(ToastType.ERROR, response.message);
                return;
            }
            callback?.(response);
        };
    }

    // 获取用户消息类型
    private readonly MESSAGE_TYPE_MAP: Map<string, string> = new Map([
		['text', '文本消息'],
		['file', '文件消息'],
		['image', '图片消息'],
		['video', '视频消息'],
        ['invite', '群聊邀请'],
	]);

    /**
     * @name 获取消息类型
     * @param type 消息类型
     */
    getMessageType(type: string): string {
        return this.MESSAGE_TYPE_MAP.get(type) || '未知消息';
    }

    /**
     * @name 获取消息类型
     * @param type 消息类型
     */
    getGroupMessageType(type: string, content: string): string {
        if (type === "text") return content.slice(0, 8);
        return `[${this.MESSAGE_TYPE_MAP.get(type) || '未知消息'}]`
    }

    /**
     * @name 发送文本消息
     * @param content 内容
     * @param callback 回调
     */
    sendTextMessage(content: string, callback?: (response: restfulType<messageType>) => void): void {
        this.sendMessage(EventType.SEND_MESSAGE, content, 'text', callback);
    }

    /**
     * @name 发送图片消息
     * @param content 内容
     * @param callback 回调
     */
    sendImageMessage(content: string, callback?: (response: restfulType<messageType>) => void): void {
        this.sendMessage(EventType.SEND_MESSAGE, content, 'image', callback);
    }

    /**
     * @name 发送文件消息
     * @param content 内容
     * @param callback 回调
     */
    sendFileMessage(content: string, callback?: (response: restfulType<messageType>) => void): void {
        this.sendMessage(EventType.SEND_MESSAGE, content, 'file', callback);
    }

    /**
     * @name 发送视频消息
     * @param content 内容
     * @param callback 回调
     */
    sendVideoMessage(content: string, callback?: (response: restfulType<messageType>) => void): void {
        this.sendMessage(EventType.SEND_MESSAGE, content, 'video', callback);
    }

    /**
     * @name 发送智能消息
     * @param content 内容
     * @param callback 回调
     */
    sendAgentMessage(content: string, callback?: (response: restfulType<messageType>) => void): void {
        this.sendMessage(EventType.SEND_AI_MESSAGE, content, 'text', callback);
    }

    /**
     * @name 发送群聊邀请
     * @param gid 群聊id
     * @param callback 回调
     */
    sendGroupInvitedMessage(group: groupInfoType, callback?: (response: restfulType<messageType>) => void): void {
        group.notice = null;
        group.member.length = 0;
        this.sendMessage(EventType.SEND_MESSAGE, JSON.stringify(group), 'invite', callback);
    }

    getNewMessageType( length: number, type: string, content: string): string {
        switch (type) {
            case "text":
                return content.substring(0, length);
            case "file":
                return "发送了文件消息";
            case "image":
                return "发送了图片消息";
            case "video":
                return "发送了视频消息";
            case "invite":
                return "发送了分享群聊";
            default:
                return "发送了未知消息";
        }
    }
}