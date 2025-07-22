<script setup lang="ts">
    import utils from "@/scripts/utils"
    import socket from "@/socket/config"
    import { ApiType } from "@/enums/ApiType"
    import HandUtils from "@/scripts/HandUtils"
    import { restfulType } from "../../../types"
    import { ToastType } from "@/enums/ToastType"
    import dialogLayout from "@/scripts/dialogLayout"
    import { StorageType } from "@/enums/StorageType"
    import { DialogServiceImpl } from "@/service/impl/DialogServiceImpl"
    import { clientServiceInstance, messageServiceInstance } from "@/service/ServiceInstance"

    const userList: any = reactive([{}]);
    const dialogServicveImpl = new DialogServiceImpl();
    const applicationStore = utils.useApplicationStore();

    const sendChatMessage = async () => {
        await HandUtils.checkClientLoginStatus(async () => {
            messageServiceInstance.sendTextMessage(applicationStore.chantInput);
            applicationStore.setChantInput("");
        });
    }

    watch(applicationStore, () => {
        userList.length = 0;
        for (const item of applicationStore.userList) {
            const { nick, avatar } = item;
            userList.push({
                value: nick, avatar: avatar
            });
        }
    }, { deep: true });

    const handleUploadSuccess = async (res: restfulType<any>, type: string) => {
        if (res.code !== 200) return await utils.showToasts(ToastType.ERROR, res.message);
        const content = res.data.path;
        switch(type) {
            case StorageType.FILE:
                messageServiceInstance.sendFileMessage(content);
                break;
            case StorageType.IMAGE:
                messageServiceInstance.sendImageMessage(content);
                break;
            case StorageType.VIDEO:
                messageServiceInstance.sendVideoMessage(content);
                break;
        }
    };

    const uploadFileSuccess = async (res: restfulType<any>) => await handleUploadSuccess(res, StorageType.FILE);
    const uploadImageSuccess = async (res: restfulType<any>) => await handleUploadSuccess(res, StorageType.IMAGE);
    const uploadVideoSuccess = async (res: restfulType<any>) => await handleUploadSuccess(res, StorageType.VIDEO);
</script>

<template>
    <el-container>
        <el-footer class="content-footer" v-if="applicationStore.loginStatus">
            <div class="input-box">
                <el-mention @keydown.enter="sendChatMessage" placement="top" v-model="applicationStore.chantInput" :options="userList" maxlength="500" :placeholder="applicationStore.isDeviceMobile ? '请在此输入聊天内容' : '请在此输入聊天内容，按Enter发送...'">
                    <template #label="{ item }">
                        <div style="display: flex; align-items: center">
                            <el-avatar :size="24" :src="HandUtils.getStorageFileUrl({
                                path: item.avatar,
                                type: StorageType.AVATAR
                            })"/>
                            <span style="margin-left: 10px">
                                {{ item.value }}
                            </span>
                        </div>
                    </template>
                </el-mention>
                <el-button class="chat-button"  plain type="primary" @click="sendChatMessage">发送消息</el-button>
            </div>
            
            <el-dropdown placement="top" v-if="!applicationStore.isDeviceMobile">
                <el-button class="more-button" type="primary" plain>
                    <svg t="1708769523336" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="5067" width="20" height="20">
                        <path d="M925.696 384q19.456 0 37.376 7.68t30.72 20.48 20.48 30.72 7.68 37.376q0 20.48-7.68 37.888t-20.48 30.208-30.72 20.48-37.376 7.68l-287.744 0 0 287.744q0 20.48-7.68 37.888t-20.48 30.208-30.72 20.48-37.376 7.68q-20.48 0-37.888-7.68t-30.208-20.48-20.48-30.208-7.68-37.888l0-287.744-287.744 0q-20.48 0-37.888-7.68t-30.208-20.48-20.48-30.208-7.68-37.888q0-19.456 7.68-37.376t20.48-30.72 30.208-20.48 37.888-7.68l287.744 0 0-287.744q0-19.456 7.68-37.376t20.48-30.72 30.208-20.48 37.888-7.68q39.936 0 68.096 28.16t28.16 68.096l0 287.744 287.744 0z" p-id="5068" fill="#ffffff"></path>
                    </svg>
                </el-button>
                <template #dropdown>
                    <el-dropdown-menu>
                        <el-dropdown-item @click="dialogServicveImpl.openCustomSwalDialog(dialogLayout.EmojeCenter, {
                            title: '发送表情'
                        })">
                            发送表情
                        </el-dropdown-item>
                        <el-upload ref="uploadRef" class="upload-avatar"
                            :headers="clientServiceInstance.getClientHeader()"
                            @success="uploadFileSuccess" @error="utils.uploadFileError"
                            :action="socket.server.config.serverUrl + ApiType.UPLOAD_FILE"
                        >
                            <template #trigger>
                                <el-dropdown-item>发送文件</el-dropdown-item>
                            </template>
                        </el-upload>
                        <el-upload ref="uploadRef" accept="image/*"
                            :headers="clientServiceInstance.getClientHeader()"
                            @success="uploadImageSuccess" @error="utils.uploadFileError"
                            :action="socket.server.config.serverUrl + ApiType.UPLOAD_IMAGE"
                        >
                            <template #trigger>
                                <el-dropdown-item>发送图片</el-dropdown-item>
                            </template>
                        </el-upload>
                        <el-upload ref="uploadRef" accept="video/mp4"
                            :headers="clientServiceInstance.getClientHeader()"
                            @success="uploadVideoSuccess" @error="utils.uploadFileError"
                            :action="socket.server.config.serverUrl + ApiType.UPLOAD_VIDEO"
                        >
                            <template #trigger>
                                <el-dropdown-item>发送视频</el-dropdown-item>
                            </template>
                        </el-upload>
                    </el-dropdown-menu>
                </template>
            </el-dropdown>
        </el-footer>

        <el-footer class="content-footer-no" v-else>
            <p class="no-text">
                游客朋友你好，请 
                <span class="login" @click="dialogServicveImpl.openLoginDialog()">登录</span> 
                后参与聊天
            </p>
        </el-footer>
    </el-container>
</template>

<style lang="less" scoped>
    @import url("../../styles/model/chat/ChatFootBox.less");
</style>