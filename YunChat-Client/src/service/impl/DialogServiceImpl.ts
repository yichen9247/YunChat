import utils from '@/scripts/utils';
import socket from '@/socket/config';
import HandUtils from '@/scripts/HandUtils';
import { ToastType } from '@/enums/ToastType';
import { userInfoType } from '../../../types';
import { DialogService } from '../DialogService';
import dialogLayout from '@/scripts/dialogLayout';
import Swal, { SweetAlertResult } from 'sweetalert2';
import { loadVueLazyLoad } from '@/scripts/appScript';
import { SocketIOClient } from './../../socket/SocketIOClient';
import { deviceServiceInstance, socketIOClientInstance } from '../ServiceInstance';

/**
 * @name 弹窗服务实现类
 * @author yichen9247
 * @description 不能单例调用，部分组件会循环调用
 */
export class DialogServiceImpl implements DialogService {
    /**
     * @name 打开图片预览
     * @param src 图片路径
     */
    openImageDialog(src: string): void {
        Swal.fire({
			imageUrl: src,
			imageWidth: "90%",
			showConfirmButton: false
		});
    }

    /**
     * @name 打开用户面板
     * @param user 目标用户
     */
    openUserCenterDialog(user: userInfoType): void {
        this.openCustomSwalDialog(dialogLayout.UserCenter, {
            width: 350,
            props: { user },
            className: 'user-center-dialog'
        });
    }

    /**
     * @name 打开设置面板
     * @param index 目标选项
     */
    openSettingDialog(index: number = 1): void {
        if (deviceServiceInstance.getDeviceIsMobile()) {
            utils.showToasts(ToastType.ERROR, '移动端暂不支持');
            return;
        }
        this.openCustomSwalDialog(dialogLayout.SettingDialog, {
            width: 620,
            props: { index }
        });
    }

    /**
     * @name 打开Swal弹窗
     * @param param0 参数列表
     */
    openSwalDialog({ icon, title, text, allowClose = false }): void {
        Swal.fire({
			showConfirmButton: false,
			icon: icon, text: text, title: title,
			allowEscapeKey: allowClose, allowOutsideClick: allowClose
		});
    }

    /**
     * @name 打开登录面板
     */
    openLoginDialog(): void {
        if (SocketIOClient.applicationStore.connection) {
            this.openCustomSwalDialog(dialogLayout.LoginCenter, {
                width: 420,
                loginDialog: true,
                className: deviceServiceInstance.getDeviceIsMobile() && "yun-login-mobile"
            });
        } else utils.showToasts(ToastType.ERROR, '连接服务器失败');
    }

    /**
     * @name 打开退出弹窗
     */
    openUserLogoutDialog(): void {
        this.openSwalToastDialog({
            confirmButtonText: "确认退出",
            text: "确定要退出登录吗，部分功能将无法使用？",
            confirmCall() {
                HandUtils.removeUserLoginStatus();
                socketIOClientInstance.startSocketIo();
                utils.showToasts(ToastType.SUCCESS, '退出登录成功');
            }
        });
    }

    /**
     * @name 打开关于弹窗
     */
    openAboutDialog(): void {
        Swal.fire({
            title: '关于项目',
            showConfirmButton: false,
            html: `
                <p style="font-size: 16px; line-height: 28px; word-break: break-all">
                    ${socket.application.description}
                </p>
            `
        });
    }

    /**
     * @name 打开文本弹窗
     */
    openTextContentDialog(content: string): void {
        Swal.fire({
            width: 435,
            title: "查看内容",
            showConfirmButton: false,
            html: `
                <div style="width: 100%; display: flex; justify-content: center">
                    <div style="text-align: justify;">
                        ${content}
                    </div>
                </div>
            `
        });
    }

    /**
     * @name 打开询问弹窗
     * @param param0 参数列表
     */
    openSwalToastDialog({
        text, 
        denyCall = null,
        confirmCall = null,
        showDenyButton =  false,
        denyButtonText = "确认",
        showCancelButton = true,
        cancelButtonText = "取消",
        confirmButtonText = "确认",
        showConfirmButton =  true
    }: {
        text: string,
        denyButtonText?: string,
        showDenyButton?: boolean,
        cancelButtonText?: string,
        confirmButtonText?: string,
        showCancelButton?: boolean,
        showConfirmButton?: boolean,
        denyCall?: (() => void) | null; 
        confirmCall?: (() => void) | null;
    }): void {
        Swal.fire({
            toast: true, text,
            customClass: {
                container: "yun-swal-toast"
            },
            showDenyButton, showCancelButton, showConfirmButton,
            cancelButtonText, confirmButtonText, denyButtonText
        }).then((result: SweetAlertResult<any>): void => {
            if (result.isDenied && denyCall) denyCall(); // 危险回调
            if (result.isConfirmed && confirmCall) confirmCall(); // 确认回调
        });
    } 
    
    /**
     * @name 打开自定义Swal弹窗
     * @param content 弹窗内容
     * @param param1 参数列表
     */
    openCustomSwalDialog(content: any, {
        title = "", 
        props = {}, 
        width = 525, 
        autoWidth = false, 
        loginDialog = false, 
        className = "yun-swal-dialog"
    } = {}): void {
        const createVueWrapper = () => {
            const vueWrapper = document.createElement('div');
            const application = createApp({
                setup: () => () => h(content, props)
            });
            loadVueLazyLoad(application);
            application.mount(vueWrapper);
            return { vueWrapper, application };
        };
        const swalConfig = {
            html: null as HTMLElement | null,
            showConfirmButton: false, title,
            width: autoWidth ? "auto" : width,
            customClass: { container: className },
            willClose: null as (() => void) | null
        };
        const { vueWrapper, application } = createVueWrapper();
        swalConfig.html = vueWrapper;
        swalConfig.willClose = () => setTimeout(application.unmount, 800);
        const action = loginDialog 
            ? () => Swal.fire(swalConfig) 
            : () => HandUtils.checkClientLoginStatus((): Promise<SweetAlertResult<any>> => Swal.fire(swalConfig));
        action();
    }
}