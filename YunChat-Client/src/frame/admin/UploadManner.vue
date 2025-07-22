<script setup lang="ts">
    import { Reactive } from 'vue'
    import utils from '@/scripts/utils'
    import HandUtils from '@/scripts/HandUtils'
    import { ToastType } from '@/enums/ToastType'
    import { StorageType } from '@/enums/StorageType'
    import { DialogServiceImpl } from '@/service/impl/DialogServiceImpl'
    import { arrayDataType, restfulType, uploadInfoType } from '../../../types'
    import { adminServiceInstance, clientServiceInstance } from '@/service/ServiceInstance'

    const pages: Ref<number> = ref(1);
    const total: Ref<number> = ref(0);
    const limit: Ref<number> = ref(15);
    const loading: Ref<boolean> = ref(false);
    const tableData: Reactive<any[]> = reactive([]);
    const dialogServiceImpl = new DialogServiceImpl();
    const fileType: any = { file: '文件', avatar: '头像', image: '图片', video: '视频'}; 
    
    const getUploadList = (): void => {
        loading.value = true;
        adminServiceInstance.getUploadList(pages.value, limit.value, async (response: restfulType<arrayDataType<uploadInfoType>>) => {
            total.value = response.data.total;
                tableData.splice(0, tableData.length, ...response.data.items);
                if (tableData.length === 0 && response.data.total > 0) {
                    pages.value = pages.value - 1;
                    getUploadList();
                }
        });
        loading.value = false;
    }

    const currentChange = (page: number): void => {
        pages.value = page;
        getUploadList();
    }

    const handleDeleteUpload = (uid: string, fid: string): void => {
        dialogServiceImpl.openSwalToastDialog({
            showDenyButton: true,
            showConfirmButton: false,
            denyButtonText: "确认删除",
            text: "是否确认删除该文件，此操作将无法回退？",
            denyCall() {
                adminServiceInstance.deleteUpload(fid, (): void => getUploadList());
            }
        });
    };

    const openDownload = (item: any): void => {
        if (item.type === 'image' || item.type === 'avatar') {
            dialogServiceImpl.openImageDialog(HandUtils.getStorageFileUrl({
                path: item.path,
                type: item.type === 'avatar' ? StorageType.AVATAR : StorageType.IMAGE,
            }));
        } else
        if (item.type === 'file') clientServiceInstance.downloadFile(HandUtils.getStorageFileUrl({
            path: item.path,
            type: StorageType.FILE,
        }));
        if (item.type === 'video') utils.showToasts(ToastType.WARNING, "暂不支持查看视频");
    }
    onMounted((): void => getUploadList());
</script>

<template>
    <div class="content-box message-manner table-container" v-loading="loading">
        <el-table :data="tableData" border height="calc(90% - 25px)" style="width: 100%">
            <el-table-column prop="uid" label="上传用户" width="150" show-overflow-tooltip/>
            <el-table-column prop="time" label="上传时间" width="200" show-overflow-tooltip/>
            <el-table-column prop="name" label="文件名称" width="150" show-overflow-tooltip/>
            <el-table-column prop="size" label="文件大小" width="150" show-overflow-tooltip>
                <template #default="scope">
                    <span>{{ HandUtils.getFileSizeByUnit({
                        mode: 'MB',
                        fileSize: scope.row.size
                    })}}MB</span>
                </template>
            </el-table-column>

            <el-table-column prop="type" label="类型" show-overflow-tooltip>
                <template #default="scope">
                    <span>{{ fileType[scope.row.type] }}</span>
                </template>
            </el-table-column>
            <el-table-column prop="path" label="文件路径" width="200" show-overflow-tooltip/>

            <el-table-column label="更多操作" width="165" show-overflow-tooltip>
                <template #default="scope">
                    <el-button size="small" type="primary" @click="openDownload(scope.row)">查看</el-button>
                    <el-button size="small" type="danger" @click="handleDeleteUpload(scope.row.uid, scope.row.fid)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
        
        <div class="pagination-box">
            <el-pagination class="pagination" layout="total, prev, pager, next, jumper" :total="total" :default-page-size="limit" @currentChange="currentChange"/>
        </div>
    </div>
</template>