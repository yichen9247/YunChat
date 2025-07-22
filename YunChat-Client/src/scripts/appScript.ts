import { App } from "vue";
import VueLazyLoad from 'vue3-lazyload';
import defaultImage from "@/assets/image/default.png";

export const loadVueLazyLoad = (application: App<Element>): void => {
    application.use(VueLazyLoad, {
        error: defaultImage,
        loading: defaultImage
    }); // 图片懒加载插件
}