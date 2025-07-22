<script setup lang="ts">
    import { Reactive } from 'vue'
    import utils from '@/scripts/utils'
    import { EventType } from '@/enums/EventType'
    import { ToastType } from '@/enums/ToastType'
    import { adminServiceInstance } from '@/service/ServiceInstance'
    import { DialogServiceImpl } from '@/service/impl/DialogServiceImpl'
    import { restfulType, adminSystemMannerType, systemInfoType } from '../../../types'

    const systemConfig: Reactive<adminSystemMannerType> = reactive({
        taboo: false,
        upload: false,
        register: false,
        welcome: false,
        version: "",
        download: "",
        ai_url: "",
        ai_role: "",
        ai_model: "",
        ai_token: ""
    });

    const activeIndex = ref('1');
    const loading: Ref<boolean> = ref(false);
    const dialogServiceImpl = new DialogServiceImpl();

    const toBool = (value: string): boolean => {
        return value?.toLowerCase() === 'open'
    }

    const handleSelect = (key: string, keyPath: string[]) => {
        activeIndex.value = key;
    }

    onMounted((): void => {
        loading.value = true;
        adminServiceInstance.getSystemConfigList((response: restfulType<systemInfoType[]>) => {
            systemConfig.ai_url = response.data.find((item: { name: string }) => item.name === 'ai_url').value;
            systemConfig.ai_role = response.data.find((item: { name: string }) => item.name === 'ai_role').value;
            systemConfig.ai_model = response.data.find((item: { name: string }) => item.name === 'ai_model').value;
            systemConfig.ai_token = response.data.find((item: { name: string }) => item.name === 'ai_token').value;
            systemConfig.version = response.data.find((item: { name: string }) => item.name === 'version').value;
            systemConfig.download = response.data.find((item: { name: string }) => item.name === 'download').value;
            systemConfig.taboo = toBool(response.data.find((item: { name: string }) => item.name === 'taboo').value);
            systemConfig.upload =  toBool(response.data.find((item: { name: string }) => item.name === 'upload').value);
            systemConfig.register = toBool(response.data.find((item: { name: string }) => item.name === 'register').value);
            systemConfig.welcome = toBool(response.data.find((item: { name: string }) => item.name === 'welcome').value);
        });
        loading.value = false;
    });

    const setSystemTaboo = (): void => {
        adminServiceInstance.updateSystemConfig("taboo", systemConfig.taboo? 'close' : 'open', (response: restfulType<any>) => {
            utils.showToasts(ToastType.SUCCESS, response.message);
            systemConfig.taboo = response.data.status === 'open';
        });
    }

    const setSystemUpload = (): void => {
        adminServiceInstance.updateSystemConfig("upload", systemConfig.upload? 'close' : 'open', (response: restfulType<any>) => {
            utils.showToasts(ToastType.SUCCESS, response.message);
            systemConfig.upload = response.data.status === 'open';
        });
    }

    const setSystemRegister = (): void => {
        adminServiceInstance.updateSystemConfig("register", systemConfig.register? 'close' : 'open', (response: restfulType<any>) => {
            utils.showToasts(ToastType.SUCCESS, response.message);
            systemConfig.register = response.data.status === 'open';
        });
    }

    const setSystemWelcome = (): void => {
        adminServiceInstance.updateSystemConfig("welcome", systemConfig.welcome? 'close' : 'open', (response: restfulType<any>) => {
            utils.showToasts(ToastType.SUCCESS, response.message);
            systemConfig.welcome = response.data.status === 'open';
        });
    }

    const adminClearHistory = (): void => {
        dialogServiceImpl.openSwalToastDialog({
            text: "是否确认清空所有聊天记录",
            confirmCall() {
                adminServiceInstance.systemConnectSend(EventType.RECE_REHISTORY_CLEAR, (response: restfulType<any>) => {
                    utils.showToasts(ToastType.SUCCESS, response.message);
                });
            }
        });
    }

    const setSystemConfigValue = ({ name, value }): void => {
        adminServiceInstance.updateSystemConfig(name, value, (response: restfulType<any>) => {
            utils.showToasts(ToastType.SUCCESS, response.message);
        });
    }
</script>

<template>
    <div class="content-box system-manner" v-loading="loading">
        <el-menu class="el-menu-demo" :default-active="activeIndex" mode="horizontal" @select="handleSelect" style="height: 50px;">
            <el-menu-item index="1">智能聊天</el-menu-item>
            <el-menu-item index="2">软件设置</el-menu-item>
            <el-menu-item index="3">系统设置</el-menu-item>
        </el-menu>

        <div class="content-box">
            <div class="box-item" v-if="activeIndex === '1'">
                <div class="flex-line">
                    <el-input v-model="systemConfig.ai_url" placeholder="请输入AI接口" clearable/>
                    <el-button type="primary" @click="setSystemConfigValue({
                        name: 'ai_url',
                        value: systemConfig.ai_url
                    })">设置AI接口</el-button>
                
                    <el-input v-model="systemConfig.ai_model" placeholder="请输入AI接口" clearable/>
                    <el-button type="primary" @click="setSystemConfigValue({
                        name: 'ai_model',
                        value: systemConfig.ai_model
                    })">设置AI模型</el-button>
                </div>

                <div class="flex-line">
                    <el-input v-model="systemConfig.ai_token" placeholder="请输入AI密钥" clearable/>
                    <el-button type="primary" @click="setSystemConfigValue({
                        name: 'ai_token',
                        value: systemConfig.ai_token
                    })">设置AI密钥</el-button>
                
                    <el-input v-model="systemConfig.ai_role" placeholder="请输入AI接口" clearable/>
                    <el-button type="primary" @click="setSystemConfigValue({
                        name: 'ai_role',
                        value: systemConfig.ai_role
                    })">设置AI角色</el-button>
                </div>
                <el-link type="primary" href="https://ppio.cn/user/register?from=ppinfra&invited_by=UUC4HY" target="_blank">推荐：新用户通过邀请码注册可获得约500万DeepSeek-V3 Tokens</el-link>
            </div>

            <div class="box-item" v-if="activeIndex === '2'">
                <div class="flex-line">
                    <el-input v-model="systemConfig.version" placeholder="请输入版本名称" clearable/>
                    <el-button type="primary" @click="setSystemConfigValue({
                        name: 'version',
                        value: systemConfig.version
                    })">设置最新版本</el-button>
                
                    <el-input v-model="systemConfig.download" placeholder="请输入下载链接" clearable/>
                    <el-button type="primary" @click="setSystemConfigValue({
                        name: 'download',
                        value: systemConfig.download
                    })">设置下载链接</el-button>
                </div>
            </div>

            <div class="box-item" v-if="activeIndex === '3'">
                <div class="flex-line">
                    <el-button type="primary" size="large" @click="adminClearHistory">清理聊天记录</el-button>
                    <el-button type="primary" size="large" @click="setSystemTaboo">{{ systemConfig.taboo ? '关闭全频禁言' : '开启全频禁言' }}</el-button>
                    <el-button type="primary" size="large" @click="setSystemUpload">{{ systemConfig.upload ? '关闭文件上传' : '开启文件上传' }}</el-button>
                    <el-button type="primary" size="large" @click="setSystemRegister">{{ systemConfig.register ? '关闭用户注册' : '开启用户注册' }}</el-button>
                    <el-button type="primary" size="large" @click="setSystemWelcome">{{ systemConfig.welcome ? '关闭新人欢迎' : '开启新人欢迎' }}</el-button>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped lang="less">
    @import url("../../styles/frame/admin/SystemManner.less");

    .el-menu--horizontal .el-menu-item:not(.is-disabled):focus, .el-menu--horizontal .el-menu-item:not(.is-disabled):hover {
        background-color: unset;
    }
</style>