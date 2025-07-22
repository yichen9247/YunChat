<script setup lang="ts">
    import utils from '@/scripts/utils'
    import { restfulType } from '../../../types'
    import { ToastType } from '@/enums/ToastType'
    import { userServiceInstance } from '@/service/ServiceInstance'

    const editForm = reactive({
        nick: "", password: ""
    });

    const emitToUpdateNick = (): Promise<void> => {
        if (editForm.nick.trim() === "") return utils.showToasts(ToastType.WARNING, '格式不合规');
        userServiceInstance.updateUserNick(editForm.nick, (response: restfulType<any>): void => {
           utils.showToasts(ToastType.SUCCESS, response.message); 
        });
    };

    const emitToUpdatePassword = (): Promise<void> => {
        if (editForm.password.trim() === "") return utils.showToasts(ToastType.WARNING, '格式不合规');
        userServiceInstance.updateUserPassword(editForm.password, (response: restfulType<any>): void => {
           utils.showToasts(ToastType.SUCCESS, response.message);
        });
    };
</script>

<template>
    <div class="user-setting">
        <p class="set-title">昵称设置</p>
        <div class="flex-line">
            <el-input v-model="editForm.nick" placeholder="请输入要设置的昵称" clearable maxlength="10" show-word-limit tabindex="-1"/>
            <el-button type="primary" @click="emitToUpdateNick" tabindex="-1">设置昵称</el-button>
        </div>
        
        <p class="set-title">密码设置</p>
        <div class="flex-line">
            <el-input v-model="editForm.password" placeholder="请输入要设置的密码" clearable maxlength="50" show-word-limit tabindex="-1"/>
            <el-button type="primary" @click="emitToUpdatePassword" tabindex="-1">设置密码</el-button>
        </div>
    </div>
</template>

<style lang="less" scoped>
    @import url("../../styles/dialog/SettingDialog/UserSetting.less");
</style>