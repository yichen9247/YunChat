/**
 * Main Application Entry Point
 * 
 * Initializes and configures the Vue application with required plugins and dependencies.
 * Sets up routing, state management, UI components and development tools.
 */
import 'element-plus/dist/index.css'

import { createApp } from 'vue'

import router from "@/router"
import { createPinia } from 'pinia'
import YunChat from './YunChat.vue'
import socket from './socket/config'
import ElementPlus from 'element-plus'
import DisableDevtool from 'disable-devtool'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import { loadVueLazyLoad } from './scripts/appScript'

const application = createApp(YunChat);
if (import.meta.env.MODE === 'production') DisableDevtool(socket.devtool);

Promise.all([
    application.use(router),
    loadVueLazyLoad(application),
    application.use(ElementPlus, {
        locale: zhCn,
    }),
    application.use(createPinia())
]).then(() => {
    application.mount('#application');
});