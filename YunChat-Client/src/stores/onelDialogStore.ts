/**
 * Dialog Store Module
 * 
 * Manages dialog and modal states using Pinia store
 * Controls visibility of various UI components like sidebars, panels and modals
 */
import { defineStore } from "pinia"
import { OnelDialogStoreType } from "../../types/onelDialogStore"

export const useOnelDialogStore = defineStore('onelDialogStore', (): OnelDialogStoreType => {
    // UI component states
    const forumPostDetail: Ref<boolean> = ref(false);
    const userLoginCenter: Ref<boolean> = ref(false);
    const groupFrameStatus: Ref<boolean> = ref(false);
    const adminFrameSelect: Ref<string> = ref("dash");

    const setUserLoginCenter = (value: boolean) => userLoginCenter.value = value;
    const setForumPostDetail = (value: boolean) => forumPostDetail.value = value;
    const setAdminFrameSelect = (value: string): string => adminFrameSelect.value = value;

    // Toggle functions
    const toggleGroupFrameStatus = (): boolean => groupFrameStatus.value = !groupFrameStatus.value;

    return ({ userLoginCenter, adminFrameSelect, groupFrameStatus, forumPostDetail, setAdminFrameSelect, toggleGroupFrameStatus, setUserLoginCenter, setForumPostDetail });
});