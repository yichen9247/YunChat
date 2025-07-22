<script setup lang="ts">
    import Swal from 'sweetalert2';
    import { Reactive } from 'vue';
    import utils from '@/scripts/utils';
    import socket from '@/socket/config';
    import { ApiType } from '@/enums/ApiType';
    import { ToastType } from '@/enums/ToastType';
    import { UploadFilled } from '@element-plus/icons-vue';
    import { adminGroupFormType, restfulType } from '../../../types';
    import { adminServiceInstance, clientServiceInstance } from '@/service/ServiceInstance';

    const props = defineProps({
        create: {
            required: true,
            type: Boolean
        },
        formData: { 
            required: true,
            type: Object as PropType<adminGroupFormType>
        },
        onSuccess: {
            required: true,
            type: Function as PropType<() => void>
        } // 成功回调（Hook）
    });

    const thisFormData: Reactive<adminGroupFormType> = reactive({ 
        ...props.formData
    });

    const handleUploadSuccess = (res: restfulType<any>) => {
        if (res.code === 200) {
            thisFormData.avatar = res.data.path;
        } else utils.showToasts(ToastType.ERROR, res.message);
    }

    const sendUpdateUserInfo = async (): Promise<void> => {
        if (thisFormData.name.trim() === "" || thisFormData.avatar.trim() === "" || thisFormData.notice.trim() === "") return utils.showToasts(ToastType.WARNING, "必填项为空");
        if (props.create) {
            adminServiceInstance.createGroup(thisFormData, (response: restfulType<any>) => {
                Swal.close();
                props.onSuccess();
                utils.showToasts(ToastType.SUCCESS, response.message);
            });
        } else adminServiceInstance.updateGroupInfo(thisFormData, (response: restfulType<any>) => {
            Swal.close();
            props.onSuccess();
            utils.showToasts(ToastType.SUCCESS, response.message);
        });
    }
</script>

<template>
    <div class="admin-dialog admin-group-dialog">
        <el-form :model="formData" label-width="auto">
            <el-form-item label="群聊名称">
                <el-input size="large" v-model="thisFormData.name" placeholder="请输入群聊名称" maxlength="6" show-word-limit/>
            </el-form-item>
            
            <el-form-item label="群聊头像">
                <el-input size="large" v-model="thisFormData.avatar" placeholder="请输入群聊头像"/>
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

            <el-form-item label="群聊公告">
                <el-input type="textarea" v-model="thisFormData.notice" :rows="3" placeholder="请输入频道公告" maxlength="100" show-word-limit/>
            </el-form-item>
        </el-form>
        <el-button class="confirm-button" type="primary" size="large" @click="sendUpdateUserInfo()">确认{{ create ? '创建' : '修改' }}</el-button>
    </div>
</template>

<style lang="less" scoped>
    @import url("../../styles/dialog/AdminDialog/AdminDialog.less");
</style>