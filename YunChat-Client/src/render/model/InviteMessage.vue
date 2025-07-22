<script setup lang="ts">
    import utils from '@/scripts/utils';
    import HandUtils from '@/scripts/HandUtils';
    import { ToastType } from '@/enums/ToastType';
    import { StorageType } from '@/enums/StorageType';
    import { groupInfoType, restfulType } from 'types';
    import { groupServiceInstance, socketIOClientInstance } from '@/service/ServiceInstance';
    
    const props = defineProps({
        group: {
            required: true,
            type: Object as PropType<groupInfoType>
        }
    });

    const userJoinGroup = (): void => {
        HandUtils.checkClientLoginStatus((): void => {
            groupServiceInstance.userJoinGroup(props.group.gid, (response: restfulType<any>) => {
                utils.showToasts(ToastType.SUCCESS, response.message);
                socketIOClientInstance.initClientGroupList();
            });
        });
    }
</script>

<template>
    <div class="message-box" @click="userJoinGroup">
        <p class="box-title">邀请你加入群聊</p>
        <div class="invite-content">
            <div class="text-box">
                <p class="text">邀请你加入群聊：{{ group.name }}，进入可查看详情</p>
            </div>
            <img class="image" v-lazy="HandUtils.getStorageFileUrl({
                path: group.avatar,
                type: StorageType.AVATAR
            })" :alt="group.name" draggable="false">
        </div>
    </div>
</template>