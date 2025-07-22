export default {
    devtool: {
        url: '/',
        clearLog: true,
        timeOutUrl: '/',
        disableCut: false,
        disableMenu: true,
        disableCopy: false,
        disablePaste: false,
        disableIframeParents: true,
    },

    server: {
        config: {
            baseUrl: import.meta.env.BASE_URL || "",
            serverIP: import.meta.env.VITE_SERVER_IP || "",
            serverUrl: import.meta.env.VITE_SERVER_URL || "",
            secretKey: import.meta.env.VITE_APP_SECRET_KEY || "",
            scanLogin: import.meta.env.VITE_APP_SCAN_LOGIN || "",
            autoShowImage: import.meta.env.VITE_APP_AUTO_SHOWIMAGE === 'true'
        }
    },

    dominColor: {
        defaultDominColor: '#448EF6',
        pureshsDominColor: 'rgba(150, 180, 190, 1)',
        yalanshDominColor: 'rgba(140, 200, 250, 1)',
        roufenhDominColor: 'rgba(255, 119, 138, 1)',
    },

    application: {
        appName: 'YunChat',
        appVersion: '2.3.2-B21',
        authorName: 'yichen9247',
        demoUrl: 'https://im.yunair.cn',
        appDoc: 'https://doc.im.xiaokolomi.cn',
        author: 'https://github.com/yichen9247',
        github: 'https://github.com/yichen9247/YunChat',
        appDownload: 'https://doc.im.xiaokolomi.cn/apk/app-release.apk',
        description: "YunChat是一款有趣且开源的聊天应用，基于 Springboot、Vite、TypeScript、Redis、Socket.IO、Kotlin等技术开发",
    }
}