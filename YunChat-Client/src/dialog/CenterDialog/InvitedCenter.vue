<script setup lang="ts">
    import Swal from 'sweetalert2';
    import { groupInfoType } from 'types';
    import HandUtils from '@/scripts/HandUtils';
    import { StorageType } from '@/enums/StorageType';
    import { SocketIOClient } from '@/socket/SocketIOClient';
    import { messageServiceInstance, socketIOClientInstance } from '@/service/ServiceInstance';

    const props = defineProps({
        group: {
            required: true,
            type: Object as PropType<groupInfoType>
        }
    });

    const sendInvitedMessage = (gid: number): void => {
        Swal.close();
        socketIOClientInstance.joinRoom(gid, (): void => {
            messageServiceInstance.sendGroupInvitedMessage(props.group);
        });
    }
</script>

<template>
    <div class="group-invited">
        <ul class="invited-list">
            <li class="group-item" v-for="(item, index) in SocketIOClient.applicationStore.chatGroupList" :key="index">
                <div class="group-left">
                    <img class="group-avatar" v-lazy="HandUtils.getStorageFileUrl({
                        path: item.avatar,
                        type: StorageType.AVATAR
                    })" :alt="item.name" draggable="false">
                    <div class="group-basic">
                        <p class="group-name">{{ item.name }}</p>
                        <p class="group-desc">群号：{{ item.gid }}</p>
                    </div>
                </div>
                <el-button @click="sendInvitedMessage(item.gid)">分享至</el-button>
            </li>
        </ul>
    </div>
</template>

<style lang="less" scoped>
    @import url("../../styles/dialog/CenterDialog/InvitedCenter.less");
</style>