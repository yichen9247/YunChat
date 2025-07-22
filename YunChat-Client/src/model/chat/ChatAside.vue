<script setup lang="ts">
    import utils from '@/scripts/utils'
    
    import dialogViews from '@/scripts/dialogLayout'
    import { DialogServiceImpl } from '@/service/impl/DialogServiceImpl'

    const oneDialogStore = utils.useOnelDialogStore();
    const dialogServicveImpl = new DialogServiceImpl();
    const applicationStore = utils.useApplicationStore();
    const onlineCount = computed((): number => applicationStore.onlineUserList.length);
</script>

<template>
    <aside class="chat-aside yun-shadow" :class="applicationStore.isDeviceMobile ? 'device-mobile' : 'device-pc'" tabindex="-1">
        <div class="yun-head-box">
            <span class="box-name">在线人数：{{ onlineCount }}</span>
        </div>
        <div class="aside-frame">
            <div class="frame-content" :style="{ transform: oneDialogStore.groupFrameStatus ? 'translateX(-100%)' : 'translateX(0)' }">
                <OnlineFrame/>
                <GroupFrame/>
            </div>

            <div class="button-box">
                <el-button class="report-button" type="primary" @click="dialogServicveImpl.openCustomSwalDialog(dialogViews.ReportCenter, {
                    title: '举报用户'
                })">举报聊天记录</el-button>
            </div>
        </div>
    </aside>
</template>

<style lang="less" scoped>
    @import url("../../styles/model/chat/ChatAside.less");
</style>