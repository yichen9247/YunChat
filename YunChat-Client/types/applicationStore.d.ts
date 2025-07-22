import type { Reactive, Ref } from "vue";
import type { groupInfoType,userInfoType, messageType, onlineUserType, lastMessageType, unReadMesageType } from ".";

export interface ApplicationStoreType {
    chatList: Reactive<messageType[]>;
    unReadMessage: Reactive<unReadMesageType[]>;
    userInfo: Reactive<userInfoType>;
    isDeviceMobile: Ref<boolean>;
    loginStatus: Ref<boolean>;
    groupInfo: Reactive<groupInfoType>;
    userList: Reactive<userInfoType[]>;
    messageList: Reactive<messageType[]>;
    aiMessageList: Reactive<messageType[]>;
    onlineUserList: Reactive<onlineUserType[]>;
    connection: Ref<boolean>;
    chantInput: Ref<string>;
    chatGroupList: Reactive<groupInfoType[]>;
    
    setChantInput: (value: string) => string;
    setConnection: (state: boolean) => boolean;
    setLoginStatus: (value: boolean) => boolean;
}