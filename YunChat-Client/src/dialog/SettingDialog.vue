<script setup lang="ts">
    import utils from "@/scripts/utils"
    import { PieChart, Setting, ChatLineRound, Notification,  Document, Message } from '@element-plus/icons-vue'

    const applicationStore = utils.useApplicationStore();
    const props = defineProps({ index: { type: Number, required: true } });
    
    const settingIndex: Ref<number> = ref(props.index);
    const handleSelect = (key: string): number => settingIndex.value = Number(key);
</script>

<template>
    <div class="flex-line" v-if="!applicationStore.isDeviceMobile">
        <el-row class="tac">
            <el-col :span="12">
                <el-menu class="el-menu-vertical-setting" @select="handleSelect" :default-active="index.toString()">
                    <el-menu-item index="1">
                        <el-icon><PieChart /></el-icon>
                        <span>个人设置</span>
                    </el-menu-item>

                    <el-menu-item index="2">
                        <el-icon><Setting /></el-icon>
                        <span>主题设置</span>
                    </el-menu-item>

                    <el-menu-item index="3">
                        <el-icon><ChatLineRound /></el-icon>
                        <span>聊天设置</span>
                    </el-menu-item>

                    <el-menu-item index="4">
                        <el-icon><Document /></el-icon>
                        <span>隐私政策</span>
                    </el-menu-item>

                    <el-menu-item index="5">
                        <el-icon><Notification /></el-icon>
                        <span>关于项目</span>
                    </el-menu-item>

                    <el-menu-item index="6">
                        <el-icon><Message /></el-icon>
                        <span>更新日志</span>
                    </el-menu-item>
                </el-menu>
            </el-col>
        </el-row>

        <ul class="content">
            <li class="content-page" v-if="settingIndex === 1">
                <div class="page-header">
                    <span class="page-title">账号设置</span>
                </div>
                <div class="page-content">
                    <UserSetting/>
                </div>
            </li>

            <li class="content-page" v-if="settingIndex === 2">
                <div class="page-header">
                    <span class="page-title">主题设置</span>
                </div>
                <div class="page-content">
                    <ThemeSetting/>
                </div>
            </li>

            <li class="content-page" v-if="settingIndex === 3">
                <div class="page-header">
                    <span class="page-title">聊天设置</span>
                </div>
                <div class="page-content">
                    <ChatSetting/>
                </div>
            </li>

            <li class="content-page" v-if="settingIndex === 4">
                <div class="page-header">
                    <span class="page-title">隐私政策</span>
                </div>
                <div class="page-content">
                    <PrivacyPage/>
                </div>
            </li>

            <li class="content-page" v-if="settingIndex === 5">
                <div class="page-header">
                    <span class="page-title">关于项目</span>
                </div>
                <div class="page-content">
                    <AboutPage/>
                </div>
            </li>

            <li class="content-page" v-if="settingIndex === 6">
                <div class="page-header">
                    <span class="page-title">更新日志</span>
                </div>
                <div class="page-content">
                    <UpdateLog/>
                </div>
            </li>
        </ul>
    </div>
</template>

<style lang="less" scoped>
    @import url("../styles/dialog/SettingDialog.less");
</style>