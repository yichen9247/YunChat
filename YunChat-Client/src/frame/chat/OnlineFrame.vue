<script setup lang="ts">
    import utils from '@/scripts/utils'
    import { DialogServiceImpl } from '@/service/impl/DialogServiceImpl'

    const dialogServiceImpl = new DialogServiceImpl();
    const onlineUids = ref<Record<string, boolean>>({});
    const applicationStore = utils.useApplicationStore();

    /**
     * @name 在线用户列表监听器
     * @description 使用排序算法排序在线用户列表
     */
    watch(() => applicationStore.onlineUserList, (users) => {
        const newUids = users.reduce<Record<string, boolean>>((acc, user) => {
            acc[user.uid] = true;
            return acc;
        }, {});
        onlineUids.value = newUids;
    }, { immediate: true });

    /**
     * @name 群聊在线用户排序算法
     * @description 在线用户排在前面，离线用户排在后面
     */
    const sortedMembers = computed(() => {
        let onlineIndex = 0;
        const members = applicationStore.groupInfo.member;
        const result = new Array(members.length);
        
        let offlineIndex = members.length - 1;
        for (let i = 0; i < members.length; i++) {
            const member = members[i];
            if (onlineUids.value[member.uid]) {
                result[onlineIndex++] = member;
            } else result[offlineIndex--] = member;
        }

        const onlineCount = onlineIndex;
        const offlineCount = members.length - 1 - offlineIndex;

        if (offlineCount > 0) {
            const offlineStart = offlineIndex + 1;
            for (let i = 0; i < offlineCount; i++) {
                result[onlineCount + i] = result[offlineStart + i];
            }
            result.length = onlineCount + offlineCount;
        } else result.length = onlineCount;
        return result;
    });
</script>

<template>
    <section class="online-frame content-main" tabindex="-1">
        <div class="online-list">
            <ChatOnlineUser v-for="item in sortedMembers" :key="item.uid" :user="item" @click="dialogServiceImpl.openUserCenterDialog(item)"/>
        </div>
    </section>
</template>

<style lang="less" scoped>
    @import url("../../styles/frame/chat/OnlineFrame.less");
</style>