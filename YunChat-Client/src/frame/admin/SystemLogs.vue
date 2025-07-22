<script setup lang="ts">
    import utils from '@/scripts/utils'
    import { throttle } from 'lodash-es'
    import { restfulType } from '../../../types'
    import { ToastType } from '@/enums/ToastType'
    import { Delete } from '@element-plus/icons-vue'
    import { ref, onMounted, shallowReactive } from 'vue'
    import { adminServiceInstance } from '@/service/ServiceInstance'
    import { DialogServiceImpl } from '@/service/impl/DialogServiceImpl'

    const logsLimit = 200;
    const loading = ref(true);
    const currentIndex = ref(0);
    const logList = shallowReactive<string[]>([]);
    const systemLogs = shallowReactive<string[]>([]);
    const dialogServiceImpl = new DialogServiceImpl();

    const getSystemLogsList = (): void => {
        adminServiceInstance.getLogsList((response: restfulType<string>): void => {
            const logsData = response.data.split('\n').filter(line => line.trim() !== '').reverse();
            logList.splice(0, logList.length, ...logsData);
            currentIndex.value = 0;
            systemLogs.splice(0, systemLogs.length);
            onLogsLoad();
            loading.value = false;
        });
    }

    const onLogsLoad = (): void => {
        const start = currentIndex.value;
        const end = start + logsLimit;
        const newLogs = logList.slice(start, end);
        if (newLogs.length === 0) return;
        systemLogs.push(...newLogs);
        currentIndex.value = end;
    }

    const handleScroll = throttle((event: Event) => {
        const target = event.target as HTMLElement;
        if (target) {
            const isBottom = target.scrollTop + target.clientHeight >= target.scrollHeight - 5;
            if (isBottom && currentIndex.value < logList.length) {
                onLogsLoad();
            }
        }
    }, 200);

    const deleteSystemLogs = (): void => {
        dialogServiceImpl.openSwalToastDialog({
            showDenyButton: true,
            showConfirmButton: false,
            denyButtonText: "确认删除",
            text: "是否确认删除所有日志，此操作将无法回退？",
            denyCall() {
                adminServiceInstance.deleteLogs((response: restfulType<any>): void => {
                    getSystemLogsList();
                    utils.showToasts(ToastType.SUCCESS, response.message);
                });
            }
        });
    }
    onMounted(() => setTimeout(() => getSystemLogsList(), 250));
</script>

<template>
    <div class="content-box system-logs" v-loading="loading" element-loading-background="rgba(51, 51, 51, 1)">
        <div class="system-logs-container">
            <el-empty description="暂无更多系统日志" v-if="systemLogs.length === 0" style="margin: auto;" />
            <div class="box-container" v-else>
                <div class="systemLogs-list" @scroll.passive="handleScroll">
                    <p class="systemLogs-list-item" v-for="(item, index) in systemLogs" :key="index">{{ item }}</p>
                </div>
                <div class="logs-btn">
                    <el-button v-if="!loading" size="large" class="btn" type="primary" :icon="Delete" circle @click="deleteSystemLogs()"/>
                </div>
            </div>
        </div>
    </div>
</template>

<style lang="less">
    @import url("../../styles/frame/admin/SystemLogs.less");
</style>