import { Reactive } from "vue"
import { defineStore } from "pinia"
import { deviceServiceInstance } from "@/service/ServiceInstance"
import { ApplicationStoreType } from '../../types/applicationStore';
import { createDefaultGroupInfo, createDefaultUserInfo } from "@/service/StoreService"
import { groupInfoType, userInfoType, messageType, onlineUserType, unReadMesageType } from "../../types"

export const useApplicationStore = defineStore('applicationStore', (): ApplicationStoreType => {
    const userInfo: Reactive<userInfoType> = reactive(
        createDefaultUserInfo()
    );
    const groupInfo: Reactive<groupInfoType> = reactive(
        createDefaultGroupInfo()
    );

    const chantInput: Ref<string> = ref("");
    const chatList: Reactive<messageType[]> = reactive([]);
    const userList: Reactive<userInfoType[]> = reactive([]);
    const messageList: Reactive<messageType[]> = reactive([]);
    const aiMessageList: Reactive<messageType[]> = reactive([]);
    const chatGroupList: Reactive<groupInfoType[]> = reactive([]);
    const onlineUserList: Reactive<onlineUserType[]> = reactive([]);
    const unReadMessage: Reactive<unReadMesageType[]> = reactive([]);
    
    const connection: Ref<boolean> = ref(false);
    const loginStatus: Ref<boolean> = ref(false);
    const isDeviceMobile: Ref<any> = ref(deviceServiceInstance.getDeviceIsMobile());

    const setChantInput = (value: string): string => chantInput.value = value;
    const setConnection = (state: boolean): boolean => connection.value = state;
    const setLoginStatus = (value: boolean): boolean => loginStatus.value = value;
    return ({ chatList, unReadMessage, userInfo, isDeviceMobile, loginStatus, groupInfo, userList, messageList, aiMessageList, onlineUserList, connection, chantInput, chatGroupList, setLoginStatus, setConnection, setChantInput });
});