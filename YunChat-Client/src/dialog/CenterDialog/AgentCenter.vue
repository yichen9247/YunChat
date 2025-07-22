<script setup lang="ts">
    import utils from '@/scripts/utils';
    import { messageType, restfulType } from '../../../types';
    import { messageServiceInstance } from '@/service/ServiceInstance';

    const inputValue = ref("");
    const applicationStore = utils.useApplicationStore();

    const sendAgentMessage = () => {
        messageServiceInstance.sendAgentMessage(inputValue.value, (response: restfulType<messageType>) => {
            applicationStore.aiMessageList.push(response.data);
        });
        inputValue.value = "";
    }
</script>

<template>
    <div class="agent-dialog">
        <div class="agent-chat-content">
            <div class="chat-list" v-if="applicationStore.aiMessageList.length > 0">
                <ChatMessage 
                    :show-user-info="false" :allow-type="['text']"
                    v-for="(message, index) in applicationStore.aiMessageList" :key="index" :message="message"
                />
            </div>
            <el-empty style="margin: auto;" v-else description="暂无更多消息记录"/>
        </div>
        <div class="agent-input-box">
            <el-mention v-model="inputValue" @keydown.enter="sendAgentMessage" placement="top" maxlength="500" placeholder="请在此输入聊天内容，按Enter发送..."/>
            <el-button class="chat-button" plain type="primary" @click="sendAgentMessage">发送消息</el-button>
        </div>
    </div>
</template>

<style lang="less" >
    @import url("../../styles/dialog/CenterDialog/AgentCenter.less");
</style>