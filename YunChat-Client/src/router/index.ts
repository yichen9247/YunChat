import Pace from 'pace-js'
import socket from '@/socket/config'
import ThemeUtils from '@/scripts/themeUtils'
import { deviceServiceInstance } from '@/service/ServiceInstance'
import { createRouter, createWebHistory, Router, RouteRecordRaw } from 'vue-router'

const siteLoadComplete = (): void => {
	Pace.on("done", (): void => {
		document.body.className = ""
	});
}

Pace.on("start", (): void => {
	ThemeUtils.setDeviceTheme();
	if (!localStorage.getItem("audio")) localStorage.setItem('audio', 'default');
	if (!localStorage.getItem("audio-switch")) localStorage.setItem("audio-switch", "true");
});

const routes: readonly RouteRecordRaw[] = [{ 
		path: '/', 
		name: 'home',
		beforeEnter: [siteLoadComplete],
		meta: { title: 'YunChat聊天室' },
		component: () => import("@/pages/HomeView.vue")
	}, { 
		path: "/:catchAll(.*)", 
		props: { result: 404 },
		beforeEnter: [siteLoadComplete],
		meta: { title: 'Error - YunChat' },
		component: () => import("@/pages/ResultView.vue")
	}
];

const router: Router = createRouter({
	routes,
	history: createWebHistory(socket.server.config.baseUrl),
	scrollBehavior(to, from, savedPosition): object {
		if (savedPosition) return savedPosition;
		if (to.meta.scrollToTop) return { top: 0 };
		if (to.hash) return { el: to.hash, behavior: 'smooth' };
	},
});

if (!deviceServiceInstance.getDeviceIsMobile()) {
	router.addRoute({
		name: 'admin',
		path: '/admin',
		beforeEnter: [siteLoadComplete],
		meta: { title: '后台 - YunChat' },
		component: () => import("@/pages/AdminView.vue")
	});
}

router.beforeEach((to: any, from: any, next: any): void => {
	if (to === from) return;
	if (to.meta.title) document.title = to.meta.title
	next();
});

export default router;