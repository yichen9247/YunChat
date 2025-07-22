<script setup lang="ts">
    import { computed } from 'vue';
    import HandUtils from '@/scripts/HandUtils';
    import { StorageType } from '@/enums/StorageType';
    import { SocketIOClient } from '@/socket/SocketIOClient';
    import { messageServiceInstance, socketIOClientInstance } from '@/service/ServiceInstance';
    import { ApplicationStoreType } from '../../types/applicationStore';
    import { groupInfoType, messageType, unReadMesageType } from '../../types';

    const chatList = computed((): messageType[] => appStore.chatList);
    const activeGroupId = computed((): number => appStore.groupInfo.gid);
    const appStore: ApplicationStoreType = SocketIOClient.applicationStore;
    const chatGroupList = computed((): groupInfoType[] => appStore.chatGroupList);
    const unReadMessage = computed((): unReadMesageType[] => appStore.unReadMessage);
    const chatMap = computed((): Map<number, messageType> => new Map(chatList.value.map(item => [item.tar, item])));

    const getLastMessageInfo = (gid: number) => {
        const messageList: messageType[] = appStore.chatGroupList.find(item => item.gid === gid).message;
        if (messageList.length < 1) return { nick: '', content: '暂无更多未读消息' };
        const message = messageList[0]
        const userInfo = HandUtils.getUserInfoByUid(message.uid);
        return {
            content: messageServiceInstance.getGroupMessageType(message.type, message.content),
            nick: `${userInfo?.uid === appStore.userInfo.uid ? "我" : userInfo?.nick || '未知用户'}：`
        };
    };
    
    const mergedGroupList = computed(() => {
        const unreadMap = new Map(
            unReadMessage.value.map(
                item => [item.gid, item.num]
            )
        );
        const currentActiveId = activeGroupId.value;
        return chatGroupList.value.map(group => {
            const { gid } = group;
            return {...group,
                chatItem: chatMap.value.get(gid),
                isActive: currentActiveId === gid,
                lastMessage: getLastMessageInfo(gid),
                unreadCount: unreadMap.get(gid) || 0
            };
        });
    });

    const handleGroupClick = (gid: number) => {
        if (appStore.groupInfo.gid === gid) return;
        HandUtils.checkClientLoginStatus((): void => {
            socketIOClientInstance.toggleChatObject(gid);
        });
    };
</script>

<template>
    <div class="right-sidebar">
        <ul class="group-list">
            <el-skeleton animated :loading="mergedGroupList.length === 0" :throttle="500">
                <!-- 正在获取群聊列表，渲染骨架 -->
                <template #template>
                    <li class="group-list-item" v-for="i in 6" :key="i">
                        <div class="item-content">
                            <el-skeleton-item variant="image" class="avatar" />
                            <div class="info-content">
                                <el-skeleton-item class="item-name" />
                                <el-skeleton-item class="item-latest" />
                            </div>
                        </div>
                    </li>
                </template>

                <!-- 获取群聊列表完成，渲染列表 -->
                <template #default>
                    <li :class="['group-list-item', { 'item-active': group.isActive }]" @click="handleGroupClick(group.gid)" v-for="group in mergedGroupList" :key="group.gid">
                        <div class="item-content">
                            <img class="avatar" :alt="group.name" v-lazy="HandUtils.getStorageFileUrl({
                                path: group.avatar,
                                type: StorageType.AVATAR
                            })" />
                            <div class="info-content">
                                <span class="item-name">{{ group.name }}</span>
                                <span class="item-latest">{{ group.lastMessage.nick }}{{ group.lastMessage.content }}</span>
                                <div class="message-number">
                                    <span v-if="group.unreadCount > 0 && !group.isActive" class="number swal2-show">
                                        {{ Math.min(group.unreadCount, 99) }}{{ group.unreadCount > 99 ? '+' : '' }}
                                    </span>
                                </div>
                            </div>
                        </div>
                    </li>
                </template>
            </el-skeleton>
        </ul>
    </div>
</template>