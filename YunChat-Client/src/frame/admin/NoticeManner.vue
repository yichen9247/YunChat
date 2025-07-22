<script setup lang="ts">
    import { Reactive } from 'vue'
    import adminDialog from '@/scripts/adminDialog'
    import { adminServiceInstance } from '@/service/ServiceInstance'
    import { DialogServiceImpl } from '@/service/impl/DialogServiceImpl'
    import { adminNoticeFormType, arrayDataType, noticeBoardType, restfulType } from '../../../types'

    const pages: Ref<number> = ref(1);
    const total: Ref<number> = ref(0);
    const limit: Ref<number> = ref(15);
    const loading: Ref<boolean> = ref(false);
    const tableData: Reactive<any[]> = reactive([]);
    const dialogServiceImpl = new DialogServiceImpl();

    const formData: Reactive<adminNoticeFormType> = reactive({
        id: null, title: "", content: ""
    });

    const getNoticeList = (): void => {
        loading.value = true;
        adminServiceInstance.getNoticeList(pages.value, limit.value, async (response: restfulType<arrayDataType<noticeBoardType>>) => {
            total.value = response.data.total;
            tableData.splice(0, tableData.length, ...response.data.items);
            if (tableData.length === 0 && response.data.total > 0) {
                pages.value = pages.value - 1;
                getNoticeList();
            }
        });
        loading.value = false;
    }

    const currentChange = (page: number): void => {
        pages.value = page;
        getNoticeList();
    }

    const handleDeleteNotice = (id: number): void => {
        dialogServiceImpl.openSwalToastDialog({
            showDenyButton: true,
            showConfirmButton: false,
            denyButtonText: "确认删除",
            text: "是否确认删除该公告，此操作将无法回退？",
            denyCall() {
                adminServiceInstance.deleteNotice(id, (): void => getNoticeList());
            }
        });
    };
    onMounted((): void => getNoticeList());
</script>

<template>
    <div class="content-box message-manner table-container" v-loading="loading">
        <el-table :data="tableData" border height="calc(90% - 25px)" style="width: 100%">
            <el-table-column prop="id" label="公告编号" width="150" show-overflow-tooltip/>
            <el-table-column prop="title" label="公告标题" width="260" show-overflow-tooltip/>
            <el-table-column prop="time" label="创建时间" width="200" show-overflow-tooltip/>
            <el-table-column prop="content" label="公告内容"/>

            <el-table-column label="更多操作" width="260">
                <template #header>
                    <span>更多操作</span> &nbsp;<span>-></span>&nbsp; <span style="cursor: pointer;" @click="dialogServiceImpl.openCustomSwalDialog(adminDialog.AdminNoticeDialog, {
                        title: '创建公告',
                        props: {
                            create: true,
                            formData: formData,
                            onSuccess: getNoticeList
                        },
                        width: 335
                    })">创建公告</span>
                </template>
                <template #default="scope">
                    <el-button size="small" type="success" @click="dialogServiceImpl.openTextContentDialog(scope.row.content)">查看内容</el-button>
                    <el-button size="small" type="primary" @click="dialogServiceImpl.openCustomSwalDialog(adminDialog.AdminNoticeDialog, {
                        title: '编辑公告',
                        props: {
                            create: false,
                            formData: scope.row,
                            onSuccess: getNoticeList
                        },
                        width: 335
                    })">编辑公告</el-button>
                    <el-button size="small" type="danger" @click="handleDeleteNotice(scope.row.id)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
        
        <div class="pagination-box">
            <el-pagination class="pagination" layout="total, prev, pager, next, jumper" :total="total" :default-page-size="limit" @currentChange="currentChange"/>
        </div>
    </div>
</template>