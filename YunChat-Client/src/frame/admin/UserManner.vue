<script setup lang="ts">
    import { Reactive } from 'vue'
    import Swal from 'sweetalert2'
    import utils from '@/scripts/utils'
    import HandUtils from '@/scripts/HandUtils'
    import { ToastType } from '@/enums/ToastType'
    import adminDialog from '@/scripts/adminDialog'
    import { StorageType } from '@/enums/StorageType'
    import { adminServiceInstance } from '@/service/ServiceInstance'
    import { DialogServiceImpl } from '@/service/impl/DialogServiceImpl'
    import { restfulType, arrayDataType, userInfoType } from '../../../types'

    const pages: Ref<number> = ref(1);
    const total: Ref<number> = ref(0);
    const limit: Ref<number> = ref(15);
    const loading: Ref<boolean> = ref(false);
    const tableData: Reactive<any[]> = reactive([]);
    const dialogServiceImpl = new DialogServiceImpl();

    const getUserList = (): void => {
        loading.value = true;
        adminServiceInstance.getUserList(pages.value, limit.value, async (response: restfulType<arrayDataType<userInfoType>>) => {
            total.value = response.data.total;
            tableData.splice(0, tableData.length, ...response.data.items);
            if (tableData.length === 0 && response.data.total > 0) {
                pages.value = pages.value - 1;
                getUserList();
            }
        });
        loading.value = false;
    }

    const currentChange = async (page: number): Promise<void> => {
        pages.value = page;
        getUserList();
    }

    const handleDeleteUser = (uid: number): void => {
        dialogServiceImpl.openSwalToastDialog({
            showDenyButton: true,
            showConfirmButton: false,
            denyButtonText: "确认删除",
            text: "是否确认删除该用户，此操作将无法回退？",
            denyCall() {
                adminServiceInstance.deleteUser(uid, (): void => getUserList());
            }
        });
    };

    const validateInput = async (password: string): Promise<boolean> => {
        const textRegex = "^[a-zA-Z0-9]+$";
        if (!password.match(textRegex)) {
            await utils.showToasts(ToastType.ERROR, '密码格式不合规');
            return false;
        }
        if (password.length < 5 || password.length > 20) {
            await utils.showToasts(ToastType.ERROR, '密码长度不合规');
            return false;
        }
        return true;
    };

    const handleUpdateUserPassword = async (uid: number): Promise<void> => {
        const { value: password } = await Swal.fire({
            width: 500,
            input: "text",
            title: "修改用户密码",
            inputAttributes: {
                maxlength: "50",
            },
            confirmButtonText: "确认修改用户密码",
            inputPlaceholder: "请输入要修改用户的密码"
        });
        if (password === '' || password === null) return utils.showToasts(ToastType.WARNING, '输入格式不合规');
        if (!await validateInput(password)) return;
        adminServiceInstance.updateUserPassword(uid, password, (response: restfulType<any>) => {
            utils.showToasts(ToastType.SUCCESS, response.message);
        });
    };

    const handleUpdateUserTabooStatus = (uid: number, status: number): void => {
        adminServiceInstance.updateUserTabooStatus(uid, status === 1 ? 0 : 1, (): void => getUserList());
    }
    onMounted((): void => getUserList());
</script>

<template>
    <div class="content-box user-manner table-container" v-loading="loading">
        <el-table :data="tableData" border height="calc(90% - 25px)" style="width: 100%">
            <el-table-column prop="uid" label="用户编号" width="145" show-overflow-tooltip/>
            <el-table-column prop="nick" label="用户昵称" width="145" show-overflow-tooltip/>
            <el-table-column prop="avatar" label="用户头像" width="145">
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
            <el-table-column prop="username" label="用户账号" width="145" show-overflow-tooltip/>
            <el-table-column prop="regTime" label="注册时间" width="175" show-overflow-tooltip/>
            <el-table-column prop="qqId" label="QQ">
                <template #default="scope">
                    <span>{{ scope.row.qqId ? "已绑定" : "未绑定" }}</span>
                </template>
            </el-table-column>

            <el-table-column label="更多操作" width="265">
                <template #default="scope">
                    <el-button size="small" type="primary" @click="dialogServiceImpl.openCustomSwalDialog(adminDialog.AdminUserDialog, {
                        title: '编辑用户',
                        props: {
                            formData: scope.row,
                            onSuccess: getUserList
                        },
                        width: 335
                    })">编辑</el-button>
                    <el-button size="small" type="warning" @click="handleUpdateUserPassword(scope.row.uid)">改密</el-button>
                    <el-button size="small" type="info" @click="handleUpdateUserTabooStatus(scope.row.uid, scope.row.status)">{{ scope.row.status === 1 ? '解禁' : '禁言' }}</el-button>
                    <el-button size="small" type="danger" @click="handleDeleteUser(scope.row.uid)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>

        <div class="pagination-box">
            <el-pagination class="pagination" layout="total, prev, pager, next, jumper" :total="total" :default-page-size="limit" @currentChange="currentChange"/>
        </div>
    </div>
</template>