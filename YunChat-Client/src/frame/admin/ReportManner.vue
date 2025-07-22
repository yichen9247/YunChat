<script setup lang="ts">
    import { Reactive } from 'vue'
    import utils from '@/scripts/utils'
    import HandUtils from '@/scripts/HandUtils'
    import { ToastType } from '@/enums/ToastType'
    import { StorageType } from '@/enums/StorageType'
    import { adminServiceInstance } from '@/service/ServiceInstance'
    import { DialogServiceImpl } from '@/service/impl/DialogServiceImpl'
    import { arrayDataType, messageType, reportFormType, restfulType } from '../../../types'
    
    const pages: Ref<number> = ref(1);
    const total: Ref<number> = ref(0);
    const limit: Ref<number> = ref(15);
    const loading: Ref<boolean> = ref(false);
    const tableData: Reactive<any[]> = reactive([]);
    const dialogServiceImpl = new DialogServiceImpl();
    
    const getReportList = (): void => {
        loading.value = true;
        adminServiceInstance.getReportList(pages.value, limit.value, async (response: restfulType<arrayDataType<reportFormType>>) => {
            total.value = response.data.total;
            tableData.splice(0, tableData.length, ...response.data.items);
            if (tableData.length === 0 && response.data.total > 0) {
                pages.value = pages.value - 1;
                getReportList();
            }
        });
        loading.value = false;
    }
    
    const currentChange = (page: number): void => {
        pages.value = page;
        getReportList();
    }
    
    const handleDeleteRepo = (rid: number): void => {
        dialogServiceImpl.openSwalToastDialog({
            showDenyButton: true,
            showConfirmButton: false,
            denyButtonText: "确认删除",
            text: "是否确认删除该举报，此操作将无法回退？",
            denyCall() {
                adminServiceInstance.deleteReport(rid, (): void => getReportList());
            }
        });
    };

    const handleDeleteChat = (sid: string): void => {
        dialogServiceImpl.openSwalToastDialog({
            showDenyButton: true,
            showConfirmButton: false,
            denyButtonText: "确认删除",
            text: "是否确认删除该消息，此操作将无法回退？",
            denyCall() {
                adminServiceInstance.deleteChat(sid, (response: restfulType<any>): void => {
                    getReportList();
                    utils.showToasts(ToastType.SUCCESS, response.message);
                });
            }
        });
    }

    const getChatContent = (sid: string): void => {
        adminServiceInstance.getChatContent(sid, (response: restfulType<messageType>): void => {
            const { type, content } = response.data;
            if (type === 'files') {
                open(HandUtils.getStorageFileUrl({
                    path: content,
                    type: StorageType.FILE,
                }));
            } else if (type === 'image') {
                dialogServiceImpl.openImageDialog(HandUtils.getStorageFileUrl({
                    path: content,
                    type: StorageType.IMAGE,
                }));
            } else if (type === "text") {
                dialogServiceImpl.openTextContentDialog(content);
            } else utils.showToasts(ToastType.WARNING, '暂不支持查看此类型消息');
        });
    }
    onMounted((): void => getReportList());
</script>

<template>
    <div class="content-box message-manner table-container" v-loading="loading">
        <el-table :data="tableData" border height="calc(90% - 25px)" style="width: 100%">
            <el-table-column prop="reporterId" label="举报者" width="150" show-overflow-tooltip/>
            <el-table-column prop="reportedId" label="被举报者" width="150" show-overflow-tooltip/>
            <el-table-column prop="reason" label="举报理由" show-overflow-tooltip/>
            <el-table-column prop="time" label="举报时间" width="200" show-overflow-tooltip/>

            <el-table-column label="更多操作" width="260">
                <template #default="scope">
                    <el-button size="small" type="primary" @click="getChatContent(scope.row.sid)">查看</el-button>
                    <el-button size="small" type="warning" @click="handleDeleteChat(scope.row.sid)">删除内容</el-button>
                    <el-button size="small" type="danger" @click="handleDeleteRepo(scope.row.rid)">删除举报</el-button>
                </template>
            </el-table-column>
        </el-table>
        
        <div class="pagination-box">
            <el-pagination class="pagination" layout="total, prev, pager, next, jumper" :total="total" :default-page-size="limit" @currentChange="currentChange"/>
        </div>
    </div>
</template>