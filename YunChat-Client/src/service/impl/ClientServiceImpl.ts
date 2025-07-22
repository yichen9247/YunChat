import ua2obj from "ua2obj";
import utils from "@/scripts/utils";
import socket from "@/socket/config";
import { EventType } from "@/enums/EventType";
import { ToastType } from "@/enums/ToastType";
import { ClientService } from "../ClientService";
import { restfulType, userInfoType } from "../../../types";
import { encryptServiceInstance, socketIOClientInstance } from "../ServiceInstance";

/**
 * @name 客户服务实现类
 * @author yichen9247
 */
export class ClientServiceImpl implements ClientService {
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
     * @name 发送客户端心跳
     * @param callback 回调
     */
    clientPing(callback?: (response: restfulType<any>) => void): void {
        this.sendMessage({}, EventType.CLIENT_PING, callback);
    }

    /**
     * @name 客户端初始化
     * @param callback 回调
     */
    clientInit(callback: (response: restfulType<userInfoType>) => void): void {
        this.sendMessage({}, EventType.CLIENT_INIT, callback);
    }

    /**
     * @name 获取请求头
     * @returns 客户端的请求头
     */
    getClientHeader(): object {
        let headers = {
            authorization: "",
            version: socket.application.appVersion,
        }
        encryptServiceInstance.encryptAuthorization().then((res: string): void => {
            headers.authorization = `Bearer ${res}`;
        });
        return headers;
    }

    /**
     * @name 下载文件
     * @param url 下载地址
     */
    async downloadFile(url: string): Promise<void> {
        const link = document.createElement('a');
        try {
            const response = await fetch(url, { method: 'HEAD' });
            let filename = response.headers.get('content-disposition')?.match(/filename="?(.+?)"?$/i)?.[1];
            if (!filename) {
                filename = decodeURIComponent(url.split('/').pop().split('?')[0]) || 'download';
            }
            const fileResponse = await fetch(url);
            const blob = await fileResponse.blob();
            const blobUrl = URL.createObjectURL(blob);
            link.href = blobUrl;
            link.download = filename;
            URL.revokeObjectURL(blobUrl);
        } catch {
            const filename = decodeURIComponent(url.split('/').pop().split('?')[0]) || 'download';
            link.href = url;
            link.download = filename;
        } finally {
            link.click();
            link.remove();
        }
    }

    /**
     * @name 获取客户端平台
     */
    getClientPlatform(): string {
        const userAgentObj = ua2obj();
        return `${userAgentObj.osName} ${userAgentObj.osVersion}`;
    }
}