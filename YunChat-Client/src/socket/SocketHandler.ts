import audioUtils from "@/scripts/audioUtils";
import { SocketIOClient } from "./SocketIOClient";
import { noticeServiceInstance, socketIOClientInstance } from "@/service/ServiceInstance";
import { aiEventHandleType, groupInfoType, messageType, onlineUserType, restfulType, userInfoType } from "../../types";
import HandUtils from "@/scripts/HandUtils";

/**
 * @name Socket.IO事件处理器
 * @author yichen9247
 */
export class SocketHandler {
    onReceiveClientToken(response: restfulType<any>): void {
        SocketIOClient.tempToken = response.data;
    }

    onReceiveOnlineUserList(response: restfulType<onlineUserType>): void {
        SocketIOClient.applicationStore.onlineUserList = response.data;
    }

    onReceviceClientMessage(response: restfulType<messageType>): void {
        SocketIOClient.applicationStore.messageList.push(response.data);
        if (response.data.uid !== SocketIOClient.applicationStore.userInfo.uid) {
            audioUtils.playNoticeVoice();
            noticeServiceInstance.showMessageNotice(response);
        }
        // 延迟原因：确保滚动到最新的消息
        setTimeout(() => document.querySelector(".chat-content").scrollTo({ top: document.querySelector(".chat-list").scrollHeight, behavior: 'smooth' }), 100);
    }

    onReceviceNewClientMessage(response: restfulType<messageType>): void {
        const { tar, uid } = response.data;
        const applicationStore = SocketIOClient.applicationStore;
        const { userInfo, groupInfo, unReadMessage } = SocketIOClient.applicationStore;
        const currentGroup: groupInfoType = applicationStore.chatGroupList.find(item => item.gid === tar);
        currentGroup.message[0] = response.data;
        if (uid === userInfo.uid || groupInfo.gid === tar) return;
        if (!unReadMessage.some(item => item.gid === tar)) {
            unReadMessage.push({
                num: 1,
                gid: tar
            });
        } else unReadMessage.find(item => item.gid === tar).num += 1;
    }

    onReceviceAiChatMessage(response: restfulType<aiEventHandleType>): void {
        const { event, result, content, eventId } = response.data;
        const { aiMessageList } = SocketIOClient.applicationStore;
        switch (event) {
            case "CREATE-MESSAGE":
                aiMessageList.push(result);
                break;
            case "PUSH-STREAM":
                const targetMsg = aiMessageList.find(item => item.sid === eventId);
                if (!targetMsg) return;
                if (targetMsg.content === "正在请求中") targetMsg.content = "";
                targetMsg.content += content;
                break;
        }
    }

    onClientVersionError(): void {
        SocketIOClient.dialogServiceImpl.openSwalDialog({
            icon: "error",
            title: "前后端版本不一致",
            text: "请将前端和后端更新到同一个版本"
        });
    }

    onReceiveUserNickUpdate(response: restfulType<userInfoType>): void {
        const uid = response.data.uid;
        const index = SocketIOClient.applicationStore.userList.findIndex(
            (item: userInfoType) => item.uid === uid
        );
        if (index !== -1) {
            SocketIOClient.applicationStore.userList[index].nick = response.data.nick;
        } else socketIOClientInstance.initClientUserList(); // 重新拉取用户列表
    }

    onReceiveUserAvatarUpdate(response: restfulType<userInfoType>): void {
        const uid = response.data.uid;
        const index = SocketIOClient.applicationStore.userList.findIndex(
            (item: userInfoType) => item.uid === uid
        );
        if (index !== -1) {
            SocketIOClient.applicationStore.userList[index].avatar = response.data.avatar;
        } else socketIOClientInstance.initClientUserList(); // 重新拉取用户列表
    }
}