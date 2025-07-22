import utils from "@/scripts/utils";
import { UserService } from "../UserService";
import { EventType } from "@/enums/EventType";
import { ToastType } from "@/enums/ToastType";
import { SocketIOClient } from "@/socket/SocketIOClient";
import { loginFormType, restfulType } from "../../../types";
import { clientServiceInstance, encryptServiceInstance, socketIOClientInstance } from "../ServiceInstance";

/**
 * @name 用户服务实现类
 * @author yichen9247
 */
export class UserServiceImpl implements UserService {
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
     * @name 用户主动登录
     * @param formData 表单
     * @param callback 回调
     */
    userLogin(formData: loginFormType, callback: (response: restfulType<any>) => void): void {
        encryptServiceInstance.encryptLogin(formData.username, formData.password).then((data: string): void => {
            this.sendMessage({
                data, tempToken: SocketIOClient.tempToken
            }, EventType.USER_LOGIN, callback);
        });
    }

    /**
     * @name 用户主动注册
     * @param formData 表单
     * @param callback 回调
     */
    userRegister(formData: loginFormType, callback: (response: restfulType<any>) => void): void {
        encryptServiceInstance.encryptLogin(formData.username, formData.password).then((data: string): void => {
            this.sendMessage({
                data, tempToken: SocketIOClient.tempToken
            }, EventType.USER_REGISTER, callback);
        });
    }

    /**
     * @name 用户扫码登录
     * @param callback 回调
     */
    userScanLogin(callback: (response: restfulType<any>) => void): void {
        this.sendMessage({ 
            qid: "",
            tempToken: SocketIOClient.tempToken
         }, EventType.USER_SCAN_LOGIN, callback);
    }
    
    /**
     * @name 修改用户昵称
     * @param nick 昵称
     * @param callback 回调
     */
    updateUserNick(nick: string, callback: (response: restfulType<any>) => void): void {
        this.sendMessage({ nick }, EventType.EDIT_USER_NICK, callback);
    }

    /**
     * @name 修改用户头像
     * @param avatar 头像
     * @param callback 回调
     */
    updateUserAvatar(path: string, callback: (response: restfulType<any>) => void): void {
        this.sendMessage({ path }, EventType.EDIT_USER_AVATAR, callback);
    }

    /**
     * @name 修改用户密码
     * @param password 密码
     * @param callback 回调
     */
    updateUserPassword(password: string, callback: (response: restfulType<any>) => void): void {
        this.sendMessage({ password }, EventType.EDIT_USER_PASSWORD, callback);
    }

    /**
     * @name 发送在线消息
     * @param data 数据
     * @param callback 回调
     */
    userOnlineLogin(status: number, callback?: (response: restfulType<any>) => void): void {
        this.sendMessage({
            status: status,
			platform: clientServiceInstance.getClientPlatform()
        }, EventType.ONLINE_LOGIN, callback);
    }
}