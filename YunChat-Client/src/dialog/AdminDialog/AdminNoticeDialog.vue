<script setup lang="ts">
    import Swal from 'sweetalert2';
    import { Reactive } from 'vue';
    import utils from '@/scripts/utils';
    import { ToastType } from '@/enums/ToastType';
    import { adminServiceInstance } from '@/service/ServiceInstance';
    import { adminNoticeFormType, restfulType } from '../../../types';

    const props = defineProps({
        create: {
            required: true,
            type: Boolean
        },
        formData: { 
            required: true,
            type: Object as PropType<adminNoticeFormType>
        },
        onSuccess: {
            required: true,
            type: Function as PropType<() => void>
        } // 成功回调（Hook）
    });

    const thisFormData: Reactive<adminNoticeFormType> = reactive({ 
        ...props.formData
    });

    const sendUpdateUserInfo = async (): Promise<void> => {
        if (thisFormData.title.trim() === "" || thisFormData.content.trim() === "") return utils.showToasts(ToastType.WARNING, "必填项为空");
        if (props.create) {
            adminServiceInstance.createNotice(thisFormData, (response: restfulType<any>) => {
                Swal.close();
                props.onSuccess();
                utils.showToasts(ToastType.SUCCESS, response.message);
            });
        } else adminServiceInstance.updateNoticeInfo(thisFormData, (response: restfulType<any>) => {
            Swal.close();
            props.onSuccess();
            utils.showToasts(ToastType.SUCCESS, response.message);
        });
    }
</script>

<template>
    <div class="admin-dialog admin-notice-dialog">
        <el-form :model="formData" label-width="auto">
            <el-form-item label="公告标题">
                <el-input size="large" v-model="thisFormData.title" placeholder="请输入公告标题" maxlength="10" show-word-limit/>
            </el-form-item>

            <el-form-item label="公告内容">
                <el-input type="textarea" v-model="thisFormData.content" :rows="5" placeholder="请输入公告内容" maxlength="200" show-word-limit/>
            </el-form-item>
        </el-form>
        <el-button class="confirm-button" type="primary" size="large" @click="sendUpdateUserInfo()">确认{{ create ? '创建' : '修改' }}</el-button>
    </div>
</template>

<style lang="less" scoped>
    @import url("../../styles/dialog/AdminDialog/AdminDialog.less");
</style>