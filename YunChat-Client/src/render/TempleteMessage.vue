<script setup lang="ts">
    import utils from '@/scripts/utils'
    import socket from '@/socket/config'
    import { messageType } from '../../types'
    import HandUtils from '@/scripts/HandUtils'
    import { ToastType } from '@/enums/ToastType'
    import { StorageType } from '@/enums/StorageType'
    import { clientServiceInstance } from '@/service/ServiceInstance'
    import { DialogServiceImpl } from '@/service/impl/DialogServiceImpl'
    import { PictureRounded, VideoPlay, Files } from '@element-plus/icons-vue'

    const chatMessage = defineProps({
        message: { type: Object as PropType<messageType>, required: true }
    });

    const loadError = ref(false);
    const videoFileBlob = ref("");
    const videoShowStatus = ref(true);
    const dialogServiceImpl = new DialogServiceImpl();
    const applicationStore = utils.useApplicationStore();
    const showStatus: Ref<boolean> = ref(socket.server.config.autoShowImage && chatMessage.message.type === 'image');

    const messageType = {
        file: {
            name: "文件"
        },
        image: {
            name: "图片",
        },
        video: {
            name: "视频"
        }
    }

    const openImagePreview = async (): Promise<void> => {
        await HandUtils.checkClientLoginStatus(() => {
            if (loadError.value) {
                utils.showToasts(ToastType.ERROR, "图片加载失败");
            } else showStatus.value = true;
        });
    }

    const openFileDownload = async (): Promise<void> => {;
        await HandUtils.checkClientLoginStatus((): Promise<void> => clientServiceInstance.downloadFile(HandUtils.getStorageFileUrl({ 
            type: StorageType.FILE,
            path: chatMessage.message.content,
        })));
    }

    const openVideoPreview = async (): Promise<void> => {
        const fileUrl = getStorageFileUrl('video') + '?v=1';
        await HandUtils.checkClientLoginStatus((): void => {
            showStatus.value = true;
            fetch(fileUrl).then(response => {
                if (response.ok) {
                    response.blob().then(blob => {
                        const blobUrl = URL.createObjectURL(blob);
                        videoFileBlob.value = blobUrl;
                        videoShowStatus.value = false;
                    });
                } else throw new Error('视频加载失败');
            }).catch(error => {
                setTimeout(()=> {
                    showStatus.value = false;
                }, 250);
                utils.showToasts(ToastType.ERROR, error.message);
            });
        });
    }

    const onTempleteClick = (): void => {
        HandUtils.checkClientLoginStatus(() => {
            switch(chatMessage.message.type) {
                case "file":
                    openFileDownload();
                    break;
                case "image":
                    openImagePreview();
                    break;
                case "video":
                    openVideoPreview();
                    break;
            }
        });
    }

    const getStorageFileUrl = (type: string) => computed(() => 
        HandUtils.getStorageFileUrl({
            path: chatMessage.message.content, type
        })
    ).value;
</script>

<template>
    <div class="message-content templete-message">
        <invite-message :data-type="message.type" @click="onTempleteClick" v-if="!showStatus && ['invite'].includes(message.type)" :group="JSON.parse(message.content)"/>
        <div class="message-box" :data-type="message.type" @click="onTempleteClick" v-if="!showStatus && ['text', 'file', 'image', 'video'].includes(message.type)">
            <div class="message-content-box">
                <el-icon :size="25">
                    <Files v-if="message.type === 'file'"/>
                    <VideoPlay v-if="message.type === 'video'"/>
                    <PictureRounded v-if="message.type === 'image'"/>
                </el-icon>
                <span class="box-name">发送了{{ messageType[message.type].name }}</span>
            </div>
        </div>

        <transition name="el-fade-in-linear" v-if="message.type == 'image'">
            <el-image v-if="applicationStore.loginStatus" v-show="showStatus" :src="getStorageFileUrl(StorageType.IMAGE)" @click="dialogServiceImpl.openImageDialog(getStorageFileUrl(StorageType.IMAGE))" fit="cover" @error="loadError = true"/>
        </transition>
        <transition name="el-fade-in-linear" v-if="message.type == 'video' && showStatus">
            <div v-show="showStatus">
                <el-skeleton :loading="videoShowStatus" animated :throttle="{ leading: 250, trailing: 250, initVal: true }">
                    <template #template>
                        <div class="skeleton-box">
                            <el-skeleton-item variant="caption" style="width: 100%; height: 100%"/>
                        </div>
                    </template>
                    <video controls preload="metadata">
                        <source :src="videoFileBlob + '\#t=0.01'" type="video/mp4">
                    </video>
                </el-skeleton>
            </div>
        </transition>
    </div>
</template>

<style lang="less">
    @import url("../styles/render/TempleteMessage.less");
</style>