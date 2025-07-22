<script setup lang="ts">
    import utils from '@/scripts/utils'
    import socket from '@/socket/config'
    import { ApiType } from '@/enums/ApiType'
    import HandUtils from '@/scripts/HandUtils'
    import { ToastType } from '@/enums/ToastType'
    import { StorageType } from '@/enums/StorageType'
    import { restfulType, userInfoType } from '../../../types'
    import { DialogServiceImpl } from '@/service/impl/DialogServiceImpl'
    import { clientServiceInstance, userServiceInstance } from '@/service/ServiceInstance'

    const editNickStatus = ref(false);
    const dialogServiceImpl = new DialogServiceImpl();
    const applicationStore = utils.useApplicationStore();

    const isCurrentUser = computed(() => applicationStore.userInfo.uid === props.user.uid);
    const props = defineProps({ user: { type: Object as PropType<userInfoType>, required: true } });

    const uploadFileSuccess = async (data: any): Promise<void> => {
        if (data.code === 200) {
            userServiceInstance.updateUserAvatar(data.data.path, (response: restfulType<any>): void => {
               utils.showToasts(ToastType.SUCCESS, response.message);
               applicationStore.userInfo.avatar = data.data.path
            });
        } else await utils.showToasts(ToastType.ERROR, data.message)
    }

    const avatarUrl = computed(() => HandUtils.getStorageFileUrl({
        path: props.user.avatar,
        type: StorageType.AVATAR
    }));
</script>

<template>
    <div class="yun-user-center">
        <div class="userinfo-box">
            <div class="avatar-box">
                <el-upload ref="uploadRef" class="upload-avatar" 
                    @success="uploadFileSuccess" @error="utils.uploadFileError"
                    :action="socket.server.config.serverUrl + ApiType.UPLOAD_AVATAR"
                    :headers="clientServiceInstance.getClientHeader()" :disabled="!isCurrentUser"
                >
                    <template #trigger>
                        <img class="avatar" v-lazy="avatarUrl" :alt="user.nick" draggable="false">
                    </template>
                </el-upload>
            </div>

            <div class="nick-box" v-if="!editNickStatus">
                <span class="nick">{{ user.nick }}</span>
            </div>
        </div>

        <div class="button-sheet" v-if="isCurrentUser">
            <el-button type="primary" size="large" @click="dialogServiceImpl.openSettingDialog()" tabindex="-1">编辑资料</el-button>
            <el-button type="danger" size="large" @click="dialogServiceImpl.openUserLogoutDialog()" tabindex="-1">退出账号</el-button>
        </div>
        
        <div class="button-sheet" v-else>
            <el-button type="primary" size="large" @click="utils.showToasts(ToastType.WARNING, '暂不支持此功能')" tabindex="-1">加为好友</el-button>
        </div>
    </div>
</template>

<style lang="less">
    @import url("../../styles/dialog/CenterDialog/UserCenter.less");
</style>