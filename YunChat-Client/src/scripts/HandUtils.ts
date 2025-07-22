import Swal from "sweetalert2"
import utils from "@/scripts/utils"
import socket from "../socket/config"
import AudioUtils from "./audioUtils"
import StorageUtils from "./storageUtils"
import { ApiType } from "@/enums/ApiType"
import { ToastType } from "@/enums/ToastType"
import { StorageType } from "@/enums/StorageType"
import { createDefaultUserInfo } from "@/service/StoreService"
import { useApplicationStore } from "@/stores/applicationStore"
import { DialogServiceImpl } from "@/service/impl/DialogServiceImpl"
import { messageType, restfulType, userAuthStatus, userInfoType } from "../../types"
import { socketIOClientInstance, userServiceInstance } from "@/service/ServiceInstance"

export default class HandUtils {
	static getUserPermission(): number {
		const applicationStore = useApplicationStore();
		return applicationStore.userInfo.permission;
	}

	static getStorageFileUrl({ path, type }): string {
		if (!/^https?:\/\//.test(path.trim())) {
			const serverUrl = socket.server.config.serverUrl;
			switch(type) {
				case StorageType.FILE:
					return serverUrl + ApiType.DOWNLOAD_FILE + path;
				case StorageType.IMAGE:
					return serverUrl + ApiType.DOWNLOAD_IMAGE + path;
				case StorageType.VIDEO:
					return serverUrl + ApiType.DOWNLOAD_VIDEO + path;
				case StorageType.AVATAR:
					return serverUrl + ApiType.DOWNLOAD_AVATAR + path;
				default:
					return path;
			}
		} else return path;
	}

	static getFileSizeByUnit = ({ fileSize, mode }): number => {
		const fileSizeKB = fileSize / 1024.0;
		if (mode === 'KB') {
			return parseFloat(fileSizeKB.toFixed(2));
		} else if (mode === 'MB') {
			return parseFloat((fileSizeKB / 1024.0).toFixed(2));
		} else return null;
	}

	static getUserInfoByUid = (uid: string): userInfoType => {
		const applicationStore = useApplicationStore();
		return applicationStore.userList.find(item => item.uid === uid);
	}

	static getMessageInfoBySid = (sid: string): messageType => {
		const applicationStore = useApplicationStore();
		return applicationStore.messageList.find(item => item.sid === sid);
	}

	static checkClientLoginStatus = async (call: any) => {
		const applicationStore = utils.useApplicationStore();
		if (!applicationStore.connection)
			return utils.showToasts(ToastType.ERROR, '连接服务器失败');
		if (!applicationStore.loginStatus) {
			const dialogServiceImpl = new DialogServiceImpl();
			return dialogServiceImpl.openLoginDialog();
		}
		await call();
	}

	static playNotificationSound = async (messageObject: messageType): Promise<void> => {
		const applicationStore = utils.useApplicationStore();
		if (applicationStore.userInfo.uid !== messageObject.uid) {
			if (messageObject.type === 'text' || messageObject.type === 'clap') {
				if (localStorage.getItem('audio-switch') === 'true') await AudioUtils.playNoticeVoice();
				if (localStorage.getItem('audio-switch') !== 'true' && localStorage.getItem('audio-switch') !== 'false') {
					await AudioUtils.playNoticeVoice();
					localStorage.setItem('audio-switch', 'true');
				}
			}
		}
	}

	static removeClientLoginStatus = async (): Promise<void> => {
		const onelDialogStore = utils.useOnelDialogStore();
		await HandUtils.removeUserLoginStatus();
		socketIOClientInstance.startSocketIo();
		onelDialogStore.setUserLoginCenter(true);	
		utils.showToasts(ToastType.ERROR, '请重新登录');
	}

	static removeUserLoginStatus = async (): Promise<void> => {
		Swal.close();
		userServiceInstance.userOnlineLogin(0);
		const applicationStore = utils.useApplicationStore();
		applicationStore.setLoginStatus(false);
		applicationStore.userInfo = createDefaultUserInfo();
		StorageUtils.removeLocalStorage(['yun_uid', 'yun_token']);
	}

	static handleUserLogin = async (response: restfulType<userAuthStatus>, password: string) => {
		Swal.close();
		const { userinfo, token } = response.data;
		const applicationStore = utils.useApplicationStore();
		applicationStore.userInfo = createDefaultUserInfo();
        await StorageUtils.saveLocalStorage([
            "yun_uid", "yun_token", "yun_username", "yun_password"
        ], [userinfo.uid, token, userinfo.username, password]).then(async () => {
			applicationStore.userInfo = userinfo;
            setTimeout((): void => {
				socketIOClientInstance.startSocketIo();
            }, 300);
        });
    }
}