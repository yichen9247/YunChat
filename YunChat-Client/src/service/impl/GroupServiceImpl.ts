import utils from "@/scripts/utils";
import { restfulType } from "../../../types";
import { EventType } from "@/enums/EventType";
import { GroupService } from "../GroupService";
import { ToastType } from "@/enums/ToastType";
import { socketIOClientInstance } from "../ServiceInstance";

/**
 * @name 群聊服务实现类
 * @author yichen9247
 */
export class GroupServiceImpl implements GroupService {
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
     * @name 退出群聊
     * @param gid 群聊id
     * @param callback 回调函数
     */
    userExitGroup(gid: number, callback: (response: restfulType<any>) => void): void {
        this.sendMessage({ gid }, EventType.DEL_GROUP_EXIT, callback);
    }

    /**
     * @name 加入群聊
     * @param gid 群聊id
     * @param callback 回调函数
     */
    userJoinGroup(gid: number, callback: (response: restfulType<any>) => void): void {
        this.sendMessage({ gid }, EventType.ADD_GROUP_JOIN, callback);
    }

    /**
     * @name 搜索群聊
     * @param name 群聊名称
     * @param callback 回调函数
     */
    searchGroup(name: string, callback: (response: restfulType<any>) => void): void {
        this.sendMessage({ name }, EventType.SEARCH_GROUP, callback);
    }
}