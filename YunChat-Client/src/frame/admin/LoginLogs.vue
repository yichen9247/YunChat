<script setup lang="ts">
    import { Reactive } from 'vue'
    import { adminServiceInstance } from '@/service/ServiceInstance'
    import { arrayDataType, loginLogType, restfulType } from '../../../types'

    const pages: Ref<number> = ref(1);
    const total: Ref<number> = ref(0);
    const limit: Ref<number> = ref(15);
    const loading: Ref<boolean> = ref(false);
    const tableData: Reactive<loginLogType[]> = reactive([]);

    onMounted((): void => getLoginList());

    const getLoginList = (): void => {
        loading.value = true;
        adminServiceInstance.getLoginList(pages.value, limit.value, async (response: restfulType<arrayDataType<loginLogType>>) => {
            total.value = response.data.total;
            tableData.splice(0, tableData.length, ...response.data.items);
            if (tableData.length === 0 && response.data.total > 0) {
                pages.value = pages.value - 1;
                getLoginList();
            }
        });
        loading.value = false;
    }

    const currentChange = (page: number): void => {
        pages.value = page;
        getLoginList();
    }
</script>

<template>
    <div class="content-box message-manner table-container" v-loading="loading">
        <el-table :data="tableData" border height="calc(90% - 25px)" style="width: 100%">
            <el-table-column prop="uid" label="登录用户" width="150" show-overflow-tooltip/>
            <el-table-column prop="username" label="用户账号" width="150" show-overflow-tooltip/>
            <el-table-column prop="address" label="物理地址" width="200" show-overflow-tooltip/>
            <el-table-column prop="platform" label="登录平台" width="150" show-overflow-tooltip/>
            <el-table-column prop="location" label="归属地" width="200" show-overflow-tooltip/>
            <el-table-column prop="time" label="登录时间" show-overflow-tooltip/>
        </el-table>
        
        <div class="pagination-box">
            <el-pagination class="pagination" layout="total, prev, pager, next, jumper" :total="total" :default-page-size="limit" @currentChange="currentChange"/>
        </div>
    </div>
</template>