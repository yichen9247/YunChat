<script setup lang="ts">
    import Swal from 'sweetalert2';
    import { Reactive } from 'vue';
    import utils from '@/scripts/utils';
    import socket from '@/socket/config';
    import { ApiType } from '@/enums/ApiType';
    import { ToastType } from '@/enums/ToastType';
    import { UserAuthType } from '@/enums/UserAuthType';
    import { UploadFilled } from '@element-plus/icons-vue';
    import { adminUserFormType, restfulType } from '../../../types';
    import { adminServiceInstance, clientServiceInstance } from '@/service/ServiceInstance';

    const props = defineProps({
        formData: { 
            required: true,
            type: Object as PropType<adminUserFormType>
        },
        onSuccess: {
            required: true,
            type: Function as PropType<() => void>
        } // 成功回调（Hook）
    });
    const thisFormData: Reactive<adminUserFormType> = reactive({ 
        ...props.formData, 
        robot: props.formData.permission === UserAuthType.ROBOT_AUTHENTICATION
    });

    const handleUploadSuccess = (res: restfulType<any>) => {
        if (res.code === 200) {
            thisFormData.avatar = res.data.path;
        } else utils.showToasts(ToastType.ERROR, res.message);
    }

    const sendUpdateUserInfo = async (): Promise<void> => {
        if (thisFormData.avatar.trim() === "" || thisFormData.nick.trim() === "") return utils.showToasts(ToastType.WARNING, "必填项为空");
        adminServiceInstance.updateUserInfo(thisFormData, (response: restfulType<any>) => {
            Swal.close();
            props.onSuccess();
            utils.showToasts(ToastType.SUCCESS, response.message);
        });
    }
</script>

<template>
    <div class="admin-dialog admin-user-dialog">
        <el-form :model="formData" label-width="auto">
            <el-form-item label="用户昵称">
                <el-input size="large" v-model="thisFormData.nick" placeholder="请输入用户昵称" maxlength="10" show-word-limit/>
            </el-form-item>
            
            <el-form-item label="用户头像">
                <el-input size="large" v-model="thisFormData.avatar" placeholder="请输入用户头像"/>
                <el-upload ref="uploadRef" accept="image/*"
                    :headers="clientServiceInstance.getClientHeader()"
                    @success="handleUploadSuccess" @error="utils.uploadFileError"
                    :action="socket.server.config.serverUrl + ApiType.UPLOAD_AVATAR">
                    <template #trigger>
                        <el-icon class="inline-btn" color="#FFFFFF" :size="18">
                            <UploadFilled/>
                        </el-icon>
                    </template>
                </el-upload>
            </el-form-item>
            <el-checkbox v-model="thisFormData.robot">设置为机器人</el-checkbox>
        </el-form>
        <el-button class="confirm-button" type="primary" size="large" @click="sendUpdateUserInfo()">确认修改</el-button>
    </div>
</template>

<style lang="less">
    @import url("../../styles/dialog/AdminDialog/AdminDialog.less");
</style>