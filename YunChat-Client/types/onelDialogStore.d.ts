import type { Ref } from "vue";

export interface OnelDialogStoreType {
    // 组件状态
    forumPostDetail: Ref<boolean>;
    userLoginCenter: Ref<boolean>;
    groupFrameStatus: Ref<boolean>;
    adminFrameSelect: Ref<string>;
    
    // 设置方法
    setUserLoginCenter: (value: boolean) => void;
    setForumPostDetail: (value: boolean) => void;
    setAdminFrameSelect: (value: string) => void;
    
    // 切换方法
    toggleGroupFrameStatus: () => boolean;
}