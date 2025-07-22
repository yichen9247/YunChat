<script setup lang="ts">
    import { Reactive } from 'vue'
    import config from '@/socket/config'
    import { useTransition } from '@vueuse/core'
    import { updateLogs } from '@/socket/updateLogs'
    import { adminServiceInstance } from '@/service/ServiceInstance'
    import { restfulType, adminSystemInfoType, adminDashInfoType, loginLogType, arrayDataType } from '../../../types'
    
    const userTotal: Ref<number> = ref(0);
    const groupTotal: Ref<number> = ref(0);
    const todayRegUser: Ref<number> = ref(0);
    const todayChatTotal: Ref<number> = ref(0);
    const loginUserLoading: Ref<boolean> = ref(true);
    const loginUserList: Reactive<loginLogType[]> = reactive([]);


    const systemOsInfo: Reactive<adminSystemInfoType> = reactive({
        osInfo: null,
        osArch: null,
        locale: null,
        hostName: null,
        appVersion: null,
        timeZoneId: null,
        hostAddress: null,
        systemUptime: null,
        logicalCount: null,
        memoryUsageInfo: null
    });
    
    const useCommonTransition = (source: any, duration: number): any => {
        return useTransition(source, { duration });
    }
    const userTotalValue: any = useCommonTransition(userTotal, 1000);
    const groupTotalValue: any = useCommonTransition(groupTotal, 1000);
    const todayRegUserValue: any = useCommonTransition(todayRegUser, 1000);
    const todayChatTotalValue: any = useCommonTransition(todayChatTotal, 1000);
    
    onMounted((): void => {
        adminServiceInstance.getDashData((response: restfulType<adminDashInfoType>): void => {
            updateDashboardData(response.data);
        });

        adminServiceInstance.getLoginList(1, 10, async (response: restfulType<arrayDataType<loginLogType>>) => {
            loginUserLoading.value = false;
            loginUserList.splice(0, loginUserList.length, ...response.data.items);
        });
    });
    
    const updateDashboardData = (data: adminDashInfoType): void => {
        userTotal.value = data.userTotal;
        groupTotal.value = data.groupTotal;
        todayRegUser.value = data.todayRegUser;
        todayChatTotal.value = data.todayChatTotal;
        Object.assign(systemOsInfo, data.systemOsInfo);
    }
</script>

<template>
    <div class="content-box admin-dash">
        <div class="top-container">
            <div class="stat-card">
                <span class="card-name">注册用户</span>
                <el-statistic :value="userTotalValue" class="statistic-nuumber" />
                <div class="icon-box">
                    <svg fill="none" viewBox="0 0 24 24" width="1em" height="1em" class="t-icon t-icon-usergroup"><path fill="currentColor" d="M7 5a3 3 0 000 6v2a5 5 0 00-5 5v4H0v-4a7 7 0 013.75-6.2A4.99 4.99 0 017 3h1v2a5 5 0 018 0V3h1a5 5 0 013.25 8.8A7 7 0 0124 18v4h-2v-4a5 5 0 00-5-5v-2a3 3 0 100-6h-1a5 5 0 11-8 0H7zm5 0a3 3 0 100 6 3 3 0 000-6zM4 19a5 5 0 015-5h6a5 5 0 015 5v3H4v-3zm5-3a3 3 0 00-3 3v1h12v-1a3 3 0 00-3-3H9z"></path></svg>
                </div>
            </div>
            <div class="stat-card">
                <span class="card-name">今日注册</span>
                <el-statistic :value="todayRegUserValue" class="statistic-nuumber" />
                <div class="icon-box">
                    <svg t="1730373086666" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="12741"><path d="M512 499.2c254.4896 0 460.8 206.3104 460.8 460.8 0 8.5888-0.2304 17.1392-0.704 25.6H869.504c0.5888-8.448 0.896-16.9856 0.896-25.6 0-197.9392-160.4608-358.4-358.4-358.4S153.6 762.0608 153.6 960c0 8.6144 0.3072 17.152 0.896 25.6H51.904A468.1856 468.1856 0 0 1 51.2 960c0-254.4896 206.3104-460.8 460.8-460.8z" p-id="12742"></path><path d="M281.6 883.2h460.8v102.4H281.6zM512 38.4c141.3888 0 256 114.6112 256 256s-114.6112 256-256 256-256-114.6112-256-256S370.6112 38.4 512 38.4z m0 102.4c-84.8256 0-153.6 68.7744-153.6 153.6s68.7744 153.6 153.6 153.6 153.6-68.7744 153.6-153.6-68.7744-153.6-153.6-153.6z" p-id="12743"></path></svg>
                </div>
            </div>
            <div class="stat-card">
                <span class="card-name">群聊数量</span>
                <el-statistic :value="groupTotalValue" class="statistic-nuumber" />
                <div class="icon-box">
                    <svg t="1730373205063" class="icon" viewBox="0 0 1025 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="16351"><path d="M382.208 493.312A236.288 236.288 0 1 0 145.92 256a236.544 236.544 0 0 0 236.288 237.312z m0-369.92A133.888 133.888 0 1 1 248.32 256a133.632 133.632 0 0 1 133.888-132.608zM1024 790.528l-19.712-137.984a144.384 144.384 0 0 0-142.336-123.392h-25.6a51.2 51.2 0 1 0 0 102.4h25.6a41.472 41.472 0 0 1 40.96 35.584l18.688 137.728a41.216 41.216 0 0 1-40.96 47.104h-40.96a51.2 51.2 0 1 0 0 102.4h40.96a143.616 143.616 0 0 0 108.544-49.408A145.152 145.152 0 0 0 1024 790.528z" p-id="16352"></path><path d="M668.928 125.952a133.888 133.888 0 0 1 8.704 260.096A51.2 51.2 0 0 0 691.2 486.4a41.728 41.728 0 0 0 13.568-2.048A236.032 236.032 0 0 0 689.152 25.6a51.2 51.2 0 1 0-20.224 100.352zM488.192 631.552a110.336 110.336 0 0 1 0 220.416H212.736a110.336 110.336 0 1 1 0-220.416h275.456m0-102.4H212.736A213.504 213.504 0 0 0 0 742.4a213.248 213.248 0 0 0 212.736 212.48h275.456a212.992 212.992 0 0 0 212.48-212.48 213.248 213.248 0 0 0-212.48-212.736z" p-id="16353"></path></svg>
                </div>
            </div>
            <div class="stat-card">
                <span class="card-name">今日消息</span>
                <el-statistic :value="todayChatTotalValue" class="statistic-nuumber" />
                <div class="icon-box">
                    <svg t="1730372900836" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="4460"><path d="M682.666667 853.333333V170.666667H170.666667v640a42.666667 42.666667 0 0 0 42.666666 42.666666h469.333334z m128 85.333334H213.333333a128 128 0 0 1-128-128V128a42.666667 42.666667 0 0 1 42.666667-42.666667h597.333333a42.666667 42.666667 0 0 1 42.666667 42.666667v298.666667h170.666667v384a128 128 0 0 1-128 128z m-42.666667-426.666667v298.666667a42.666667 42.666667 0 1 0 85.333333 0v-298.666667h-85.333333zM256 256h256v256H256V256z m85.333333 85.333333v85.333334h85.333334V341.333333H341.333333z m-85.333333 213.333334h341.333333v85.333333H256v-85.333333z m0 128h341.333333v85.333333H256v-85.333333z" p-id="4461"></path></svg>
                </div>
            </div>
        </div>

        <div class="info-container">
            <div class="info-card" style="height: 212px;">
                <el-descriptions size="large" :column="2" title="程序信息">
                    <el-descriptions-item label="程序名称">{{ config.application.appName }}</el-descriptions-item>
                    <el-descriptions-item label="程序版本">{{ config.application.appVersion }}</el-descriptions-item>
                    <el-descriptions-item label="程序作者">{{ config.application.authorName }}</el-descriptions-item>
                    <el-descriptions-item label="程序框架">Springboot + Vite</el-descriptions-item>
                    <el-descriptions-item label="程序简介">{{ config.application.description }}</el-descriptions-item>
                </el-descriptions>
            </div>
            <div class="info-card" style="height: 212px;" v-loading="loginUserLoading">
                <el-descriptions size="large" :column="2" title="最近登录">
                    <el-descriptions-item v-for="(item, index) in loginUserList" :key="index" :label="item.location">{{ item.uid }}（{{ item.platform }}）</el-descriptions-item>
                </el-descriptions>
                <div class="empty-box" v-if="loginUserList.length == 0">暂无数据</div>
            </div>
        </div>

        <div class="info-container">
            <div class="info-card">
                <div class="card-overview">
                    <el-descriptions size="large" :column="2" title="系统信息">
                        <el-descriptions-item label="主机名称">{{ systemOsInfo.hostName }}</el-descriptions-item>
                        <el-descriptions-item label="系统语言">{{ systemOsInfo.locale }}</el-descriptions-item>
                        <el-descriptions-item label="系统架构">{{ systemOsInfo.osArch }}</el-descriptions-item>
                        <el-descriptions-item label="主机地址">{{ systemOsInfo.hostAddress }}</el-descriptions-item>
                        <el-descriptions-item label="操作系统">{{ systemOsInfo.osInfo }}</el-descriptions-item>
                        <el-descriptions-item label="系统时区">{{ systemOsInfo.timeZoneId }}</el-descriptions-item>
                        <el-descriptions-item label="系统内存">{{ systemOsInfo.memoryUsageInfo }}</el-descriptions-item>
                        <el-descriptions-item label="核心数量">{{ systemOsInfo.logicalCount }}核心</el-descriptions-item>
                        <el-descriptions-item label="运行时长">{{ systemOsInfo.systemUptime }}</el-descriptions-item>
                        <el-descriptions-item label="程序版本">{{ systemOsInfo.appVersion }}</el-descriptions-item>
                    </el-descriptions>
                </div>
            </div>

            <div class="info-card">
                <div class="card-update">
                    <el-descriptions size="large" :column="2" title="更新日志">
                        <el-descriptions-item v-for="(item, index) in updateLogs[0].data" :key="index" :label="item.type">{{ item.content }}</el-descriptions-item>
                    </el-descriptions>
                </div>
            </div>
        </div>

        <div class="dash-footer">
            <div class="left-box box-content">
                <span>Copyright © 2023-2025 YunChat. All Rights Reserved</span>
            </div>

            <div class="right-box box-content">
                <el-link type="primary" :href="config.application.demoUrl" target="_blank">演示站点</el-link>
                &nbsp;&nbsp;
                <el-link type="primary" :href="config.application.appDoc" target="_blank">项目文档</el-link>
                &nbsp;&nbsp;
                <el-link type="primary" :href="config.application.github" target="_blank">项目地址</el-link>
            </div>
        </div>
    </div>
</template>

<style lang="less">
    @import url("../../styles/frame/admin/DashManner.less");
</style>