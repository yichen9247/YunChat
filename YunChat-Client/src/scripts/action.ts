import utils from './utils'
import socket from '@/socket/config'
import { ToastType } from '@/enums/ToastType'
import { useOnelDialogStore } from '@/stores/onelDialogStore'
import { useApplicationStore } from '@/stores/applicationStore'

export const setUserLoginForm = async (status: boolean): Promise<void> => {
    const applicationStore = useApplicationStore();
    if (applicationStore.connection) {
        const onelDialogStore = useOnelDialogStore();
        onelDialogStore.setUserLoginCenter(status)
    } else utils.showToasts(ToastType.ERROR, '连接服务器失败');
}

export const openGithubSite = (): Window => window.open(socket.application.github);