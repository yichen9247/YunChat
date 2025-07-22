import socket from '@/socket/config'
import { deviceServiceInstance } from '@/service/ServiceInstance'

export default class themeUtils {
    static setDeviceTheme = async (): Promise<void> => {
        let theme = localStorage.getItem('theme')
        let color: string
        let imageImport: Promise<{ default: string }>

        if (theme === 'default') {
            color = socket.dominColor.defaultDominColor
        } else if (theme === 'pureshs') {
            color = socket.dominColor.pureshsDominColor
        } else if (theme === 'yalansh') {
            color = socket.dominColor.yalanshDominColor
        } else if (theme === 'roufenh') {
            color = socket.dominColor.roufenhDominColor
        } else {
            localStorage.setItem('theme', 'default')
            color = socket.dominColor.defaultDominColor
        }

        try {
            if (!deviceServiceInstance.getDeviceIsMobile()) {
                const image = await imageImport
                await this.preloadImage(image.default)
            }
            document.body.style.setProperty("--dominColor", color);
            if (!deviceServiceInstance.getDeviceIsMobile()) {
                const image = await imageImport
                document.body.style.setProperty("--background", `url(${image.default})`)
            }
        } catch (e) {
            document.body.style.setProperty("--dominColor", color)
        } finally {
            document.body.style.setProperty("--dominColor", color)
        }
    }

    private static preloadImage = (url: string): Promise<void> => {
        return new Promise((resolve, reject) => {
            const img = new Image()
            img.src = url
            img.onload = () => resolve()
            img.onerror = (e) => reject(e)
        });
    }
}