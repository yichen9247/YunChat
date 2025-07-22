<script setup lang="ts">
    import { updateLogs } from '@/socket/updateLogs';


</script>

<template>
    <el-timeline class="update-log">
        <el-timeline-item v-for="(item, index) in updateLogs" :key="index" :timestamp="item.name" placement="top">
            <el-descriptions size="large" :column="2">
                <el-descriptions-item v-for="(logItem, logIndex) in item.data" :key="logIndex" :label="logItem.type">{{ logItem.content }}</el-descriptions-item>
            </el-descriptions>
        </el-timeline-item>
    </el-timeline>
</template>

<style lang="less">
    @import url("../../styles/dialog/SettingDialog/UpdateLog.less");
</style>