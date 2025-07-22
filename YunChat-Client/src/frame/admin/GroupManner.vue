<script setup lang="ts">
    import { Reactive } from 'vue'
    import HandUtils from '@/scripts/HandUtils'
    import adminDialog from '@/scripts/adminDialog'
    import { StorageType } from '@/enums/StorageType'
    import { UserAuthType } from '@/enums/UserAuthType'
    import { adminServiceInstance } from '@/service/ServiceInstance'
    import { DialogServiceImpl } from '@/service/impl/DialogServiceImpl'
    import { adminGroupFormType, arrayDataType, groupInfoType, restfulType } from '../../../types'
    
    const pages: Ref<number> = ref(1);
    const total: Ref<number> = ref(0);
    const limit: Ref<number> = ref(15);
    const loading: Ref<boolean> = ref(false);
    const tableData: Reactive<any[]> = reactive([]);
    const dialogServiceImpl = new DialogServiceImpl();

    const formData: Reactive<adminGroupFormType> = reactive({
        gid: null, name: "", avatar: "", notice: ""
    });

    const getGroupList = (): void => {
        loading.value = true;
        adminServiceInstance.getGroupList(pages.value, limit.value, async (response: restfulType<arrayDataType<groupInfoType>>) => {
            total.value = response.data.total;
            tableData.splice(0, tableData.length, ...response.data.items);
            if (tableData.length === 0 && response.data.total > 0) {
                pages.value = pages.value - 1;
                getGroupList();
            }
        });
        loading.value = false;
    }
    
    const currentChange = async (page: number): Promise<void> => {
        pages.value = page;
        getGroupList();
    }
    
    const handleDeleteChan = (gid: number): void => {
        dialogServiceImpl.openSwalToastDialog({
            showDenyButton: true,
            showConfirmButton: false,
            denyButtonText: "确认删除",
            text: "是否确认删除该群聊，此操作将无法回退？",
            denyCall() {
                adminServiceInstance.deleteGroup(gid, (): void => getGroupList());
            }
        });
    };

    const handleUpdateGroupTabooStatus = (gid: number, status: number): void => {
        adminServiceInstance.updateGroupTabooStatus(gid, status === 1 ? 0 : 1, (): void => getGroupList());
    }

    const handleCreateGroup = (): void => {
        formData.gid = null;
        formData.name = "";
        formData.notice = "当前群聊暂无公告";
        formData.avatar = "0/default/avatar.png";

        dialogServiceImpl.openCustomSwalDialog(adminDialog.AdminGroupDialog, {
            title: "创建群聊",
            props: {
                create: true,
                formData: formData,
                onSuccess: getGroupList
            },
            width: 335
        });
    }
    onMounted((): void => getGroupList());
</script>

<template>
    <div class="content-box message-manner table-container" v-loading="loading">
        <el-table :data="tableData" border height="calc(90% - 25px)" style="width: 100%">
            <el-table-column prop="gid" label="群聊编号" width="145" show-overflow-tooltip/>
            <el-table-column prop="name" label="频道名称" width="165" show-overflow-tooltip/>
            <el-table-column prop="avatar" label="频道头像" width="165">
                <template #default="scope">
                    <el-popover  placement="right">
                        <template #reference>
                            <span>{{ scope.row.avatar }}</span>
                        </template>
                        <template #default>
                            <img v-lazy="HandUtils.getStorageFileUrl({
                                path: scope.row.avatar,
                                type: StorageType.AVATAR
                            })" :alt="scope.row.nick" draggable="false" class="popover-image">
                        </template>
                    </el-popover>
                </template>
            </el-table-column>

            <el-table-column prop="notice" label="频道公告" width="165" show-overflow-tooltip />
            <el-table-column prop="time" label="创建时间" width="165" show-overflow-tooltip />

            <el-table-column prop="time" label="成员" show-overflow-tooltip>
                <template #default="scope">
                    <span>{{ scope.row.member.length }}</span>
                </template>
            </el-table-column>

            <el-table-column label="更多操作" width="260">
                <template #header>
                    <span>更多操作</span> &nbsp;<span>-></span>&nbsp; <span style="cursor: pointer;" @click="handleCreateGroup()">创建群聊</span>
                </template>

                <template #default="scope">
                    <el-button size="small" type="success" @click="dialogServiceImpl.openCustomSwalDialog(adminDialog.AdminMemberDialog, {
                        title: '成员管理',
                        props: {
                            gid: scope.row.gid,
                            list: scope.row.member,
                            onSuccess: getGroupList
                        },
                        width: 335
                    })">管理</el-button>
                    <el-button size="small" type="primary" @click="dialogServiceImpl.openCustomSwalDialog(adminDialog.AdminGroupDialog, {
                        title: '编辑群聊',
                        props: {
                            create: false,
                            formData: scope.row,
                            onSuccess: getGroupList
                        },
                        width: 335
                    })">编辑</el-button>
                    <el-button size="small" type="warning" @click="handleUpdateGroupTabooStatus(scope.row.gid, scope.row.status)">{{ scope.row.status === UserAuthType.TABOO_STATUS ? '解禁' : '禁言' }}</el-button>
                    <el-button size="small" type="danger" @click="handleDeleteChan(scope.row.gid)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>

        <div class="pagination-box">
            <el-pagination class="pagination" layout="total, prev, pager, next, jumper" :total="total" :default-page-size="limit" @currentChange="currentChange"/>
        </div>
    </div>
</template>