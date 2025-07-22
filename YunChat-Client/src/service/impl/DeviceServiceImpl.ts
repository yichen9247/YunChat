import { DeviceService } from "../DeviceService";
import { getCurrentWindow } from "@tauri-apps/api/window";

export class DeviceServiceImpl implements DeviceService {
    /**
     * @name 获取设备是否移动端
     * @return Boolean
     */
    getDeviceIsMobile(): Boolean {
        return navigator.userAgent.match(
            /(phone|pad|pod|iPhone|iPod|ios|iPad|Android|Mobile|BlackBerry|IEMobile|MQQBrowser|JUC|Fennec|wOSBrowser|BrowserNG|WebOS|Symbian|Windows Phone)/i
        ) && window.innerWidth < 720;
    }

    /**
     * @name 获取设备是否桌面端
     * @return Boolean
     */
    getDeviceIsDesktop(): Boolean {
        try {
            getCurrentWindow();
            return true;
        } catch (e) {
            return false;
        }
    }
}