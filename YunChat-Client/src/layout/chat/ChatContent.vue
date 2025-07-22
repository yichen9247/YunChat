<script setup lang="ts">
    import utils from '@/scripts/utils';
    const applicationStore = utils.useApplicationStore();
</script>

<template>
    <div class="chat-content" tabindex="-1">
        <div class="chat-list" v-if="applicationStore.messageList.length > 0">
            <ChatMessage 
                v-for="(message, index) in applicationStore.messageList" :key="index" :message="message"
            />
        </div>
        <el-empty style="margin: auto;" v-else description="暂无更多消息记录"/>
    </div>
</template>

<style lang="less" scoped>
    @import url("../../styles/layout/chat/ChatContent.less");
</style>