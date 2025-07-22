<script setup lang="ts">
    import { Reactive } from 'vue'
    import utils from '@/scripts/utils'
    import HandUtils from '@/scripts/HandUtils'
    import { ToastType } from '@/enums/ToastType'
    import { StorageType } from '@/enums/StorageType'
    import { DialogServiceImpl } from '@/service/impl/DialogServiceImpl'
    import { arrayDataType, messageType, restfulType } from '../../../types'
    import { adminServiceInstance, clientServiceInstance, messageServiceInstance } from '@/service/ServiceInstance'

    const pages: Ref<number> = ref(1);
    const total: Ref<number> = ref(0);
    const limit: Ref<number> = ref(15);
    const loading: Ref<boolean> = ref(false);
    const tableData: Reactive<any[]> = reactive([]);
    const dialogServiceImpl = new DialogServiceImpl();

    const getChatList = (): void => {
        loading.value = true;
        adminServiceInstance.getChatList(pages.value, limit.value, (response: restfulType<arrayDataType<messageType>>): void => {
            total.value = response.data.total;
            tableData.splice(0, tableData.length, ...response.data.items);
            if (tableData.length === 0 && response.data.total > 0) {
                pages.value = pages.value - 1;
                getChatList();
            }
        });
        loading.value = false;
    }

    const currentChange = (page: number): void => {
        pages.value = page;
        getChatList();
    }

    const openContentAlert = async (item: messageType): Promise<void> => {
        if (item.type === 'file') {
            clientServiceInstance.downloadFile(HandUtils.getStorageFileUrl({ 
                path: item.content,
                type: StorageType.FILE,
            }));
        } else if (item.type === 'image') {
            dialogServiceImpl.openImageDialog(HandUtils.getStorageFileUrl({
                path: item.content,
                type: StorageType.IMAGE,
            }));
        } else if (item.type === "text") {
            dialogServiceImpl.openTextContentDialog(item.content);
        } else utils.showToasts(ToastType.WARNING, '暂不支持查看此类型消息');
    }

    const handleDeleteChat = (sid: string): void => {
        dialogServiceImpl.openSwalToastDialog({
            showDenyButton: true,
            showConfirmButton: false,
            denyButtonText: "确认删除",
            text: "是否确认删除该消息，此操作将无法回退？",
            denyCall() {
                adminServiceInstance.deleteChat(sid, (): void => getChatList());
            }
        });
    };
    onMounted((): void => getChatList());
</script>

<template>
    <div class="content-box message-manner table-container" v-loading="loading">
        <el-table :data="tableData" border height="calc(90% - 25px)" style="width: 100%">
            <el-table-column prop="uid" label="用户编号" width="150" show-overflow-tooltip/>
            <el-table-column prop="tar" label="发送对象" width="150" show-overflow-tooltip/>
            <el-table-column prop="obj" label="对象类型" width="150" show-overflow-tooltip>
                <template #default="scope">
                    <span>{{ scope.row.tar ? "群聊" : "私聊" }}</span>
                </template>
            </el-table-column>
            <el-table-column prop="type" label="消息类型" width="150" show-overflow-tooltip>
                <template #default="scope">
                    <span>{{ messageServiceInstance.getMessageType(scope.row.type) }}</span>
                </template>
            </el-table-column>
            <el-table-column prop="time" label="发送时间" width="200" show-overflow-tooltip/>

            <el-table-column prop="deleted" label="撤回" show-overflow-tooltip>
                <template #default="scope">
                    <span>{{ scope.row.deleted === 1 ? '是' : '否' }}</span>
                </template>
            </el-table-column>

            <el-table-column label="更多操作" width="200">
                <template #default="scope">
                    <el-button size="small" type="primary" @click="openContentAlert(scope.row)">查看内容</el-button>
                    <el-button size="small" type="danger" @click="handleDeleteChat(scope.row.sid)">删除消息</el-button>
                </template>
            </el-table-column>
        </el-table>

        <div class="pagination-box">
            <el-pagination class="pagination" layout="total, prev, pager, next, jumper" :total="total" :default-page-size="limit" @currentChange="currentChange"/>
        </div>
    </div>
</template>