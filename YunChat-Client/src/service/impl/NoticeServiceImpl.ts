import HandUtils from "@/scripts/HandUtils";
import { NoticeService } from "../NoticeService";
import { StorageType } from "@/enums/StorageType";
import { messageType, restfulType } from "types";
import { messageServiceInstance } from "../ServiceInstance";

/**
 * @name 通知服务实现类
 * @author yichen9247
 */
export class NoticeServiceImpl implements NoticeService {
    private showNotification({ nick, body, icon, image }): void {
        if (localStorage.getItem('notice-switch') !== 'true') return;
        if (Notification.permission === "granted") {
            this.createNotification({ nick, body, icon, image });
        } else if (Notification.permission !== "denied") {
            Notification.requestPermission().then(permission => {
                if (permission !== "granted") return;
                this.createNotification({ nick, body, icon, image });
            });
        }
    }

    private createNotification({ nick, body, icon, image }): void {
        const options = {
            tag: "yunchat-tag", body, image,
            icon: HandUtils.getStorageFileUrl({
                path: icon,
                type: StorageType.AVATAR
            }),
        };
        new Notification(nick, options);
    }

    showMessageNotice(response: restfulType<messageType>): void {
        let image = "";
        if (document.visibilityState === 'visible') return;
        const { uid, content, type } = response.data;
        const user = HandUtils.getUserInfoByUid(uid);
        if (type === "image") image = HandUtils.getStorageFileUrl({
            path: content,
            type: StorageType.IMAGE
        })
        this.showNotification({
            nick: user.nick,
            icon: user.avatar, image,
            body: messageServiceInstance.getNewMessageType(50, type, content)
        });
    }
}