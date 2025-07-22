<script setup lang="ts">
    import HandUtils from "@/scripts/HandUtils"
    import { userInfoType } from "../../../types"
    import { StorageType } from "@/enums/StorageType"
    import { useApplicationStore } from "@/stores/applicationStore"

    const props = defineProps({
        user: {
            required: true,
            type: Object as PropType<userInfoType>
        }
    });

    const applicationStore = useApplicationStore();
    const userInfo = computed(() => HandUtils.getUserInfoByUid(props.user.uid));

    const isOnline = computed(() => 
        applicationStore.onlineUserList.some(item => item.uid === props.user.uid)
    );
</script>

<template>
    <div class="online-user-item" v-if="applicationStore.userList.some(item => item.uid === props.user.uid)">
        <div class="item-content">
            <div class="user-avatar">
                <img class="avatar" v-lazy="HandUtils.getStorageFileUrl({
                    path: userInfo.avatar,
                    type: StorageType.AVATAR
                })" draggable="false" :alt="userInfo.nick">
            </div>
            <div class="content">
                <span class="user-nick">{{ userInfo.nick }}</span>
                <span class="user-radio" :data-status="isOnline"></span>
            </div>
        </div>
    </div>
</template>

<style lang="less" scoped>
    @import url("../../styles/model/chat/ChatOnlineUser.less");
</style>