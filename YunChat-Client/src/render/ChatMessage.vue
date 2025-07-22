<script setup lang="ts">
    import { messageType } from "../../types"
    import HandUtils from "@/scripts/HandUtils"
    
    defineProps({
        showUserInfo: {
            type: Boolean, 
            default: true,
            required: false,
        },
        message: {
            required: true,
            type: Object as PropType<messageType>
        },
        allowType: {
            required: false,
            type: Array<string>,
            default: ['text', 'file', 'image', 'video', 'invite']
        }
    });
</script>

<template>
    <span class="chat-message" :data-type="message.type" v-if="allowType.includes(message.type)">
        <ChatMessageInfo :message="message" :showUserInfo="showUserInfo"
            v-if="message.uid === '1234567890' || HandUtils.getUserInfoByUid(message.uid) !== null"
        />
    </span>
</template>

<style lang="less">
    @import url("../styles/render/ChatMessage.less");
</style>