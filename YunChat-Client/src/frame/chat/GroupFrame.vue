<script setup lang="ts">
    import utils from '@/scripts/utils'
    import HandUtils from '@/scripts/HandUtils'
    import { restfulType } from '../../../types'
    import { ToastType } from '@/enums/ToastType'
    import { Bell } from '@element-plus/icons-vue'
    import adminDialog from '@/scripts/adminDialog'
    import { StorageType } from '@/enums/StorageType'
    import dialogLayout from '@/scripts/dialogLayout'
    import { UserAuthType } from '@/enums/UserAuthType'
    import { DialogServiceImpl } from '@/service/impl/DialogServiceImpl'
    import { adminServiceInstance, groupServiceInstance, socketIOClientInstance } from '@/service/ServiceInstance'

    const dialogServiceImpl = new DialogServiceImpl();
    const applicationStore = utils.useApplicationStore();

    const group = computed(() => applicationStore.groupInfo);
    const handleUpdateGroupTabooStatus = (data: boolean): void => {
        group.value.status = data ? UserAuthType.TABOO_STATUS : 0;
        adminServiceInstance.updateGroupTabooStatus(group.value.gid, data ? UserAuthType.TABOO_STATUS : 0);
    }
    const taboo = computed(() => group.value.status === UserAuthType.TABOO_STATUS);

    const onGroupInfoCardClick = (): void => {
        if (HandUtils.getUserPermission() !== UserAuthType.ADMIN_AUTHENTICATION) return;
        dialogServiceImpl.openCustomSwalDialog(adminDialog.AdminGroupDialog, {
            title: "编辑群聊",
            props: {
                create: false,
                formData: group.value,
                onSuccess: () => {
                    socketIOClientInstance.initClientGroupList();
                }
            },
            width: 335
        });
    }

    const userExitGroup = (): void => {
        dialogServiceImpl.openSwalToastDialog({
            showDenyButton: true,
            showConfirmButton: false,
            denyButtonText: "确认删除",
            text: "是否确认退出该群聊，此操作将无法回退？",
            denyCall() {
                groupServiceInstance.userExitGroup(group.value.gid, (response: restfulType<any>) => {
                    utils.showToasts(ToastType.SUCCESS, response.message);
                    socketIOClientInstance.initClientGroupList();
                });
            }
        });
    }
</script>

<template>
    <section class="group-frame content-main" tabindex="-1">
        <div class="group-card info-card" @click="onGroupInfoCardClick()">
            <img class="group-avatar" :alt="group.name" v-lazy="HandUtils.getStorageFileUrl({
                path: group.avatar,
                type: StorageType.AVATAR
            })" draggable="false">
            <div class="group-content">
                <p class="group-name">{{ group.name }}</p>
                <p class="group-desc">群号：{{ group.gid }}</p>
            </div>
        </div>

        <div class="group-notice" v-if="applicationStore.connection">
            <el-icon :size="16" color="rgba(60, 60, 67, 0.65)"><Bell/></el-icon>
            <div class="content-box">{{ group.notice }}</div>
        </div>

        <div class="info-card group-info">
            <div class="card-content">
                <div class="info-line">
                    <span class="info-name">群聊人数</span>
                    <span class="info-desc">{{ group.member.length }}人</span>
                </div>
                <div class="info-line">
                    <span class="info-name">全体禁言</span>
                    <span class="info-desc">{{ taboo ? '开启' : '关闭' }}</span>
                </div>
                <div class="info-line">
                    <span class="info-name">发言限频</span>
                    <span class="info-desc">3S/条</span>
                </div>
            </div>
        </div>

        <div class="info-card group-info" v-if="HandUtils.getUserPermission() === UserAuthType.ADMIN_AUTHENTICATION">
            <div class="card-content">
                <div class="info-line">
                    <span class="info-name">全体禁言</span>
                    <span class="info-desc">
                        <el-switch size="small" tabindex="-1" v-model="taboo" @change="handleUpdateGroupTabooStatus"/>
                    </span>
                </div>
            </div>
        </div>

        <div class="button-group">
            <button class="button" style="color: #007aff" tabindex="-1" @click="dialogServiceImpl.openCustomSwalDialog(dialogLayout.InvitedCenter, {
                title: '分享群聊',
                props: { group },
                width: 380
            })">分享群聊</button>
            <button class="button" style="color: #dd524d" tabindex="-1" @click="userExitGroup()">退出群聊</button>
        </div>
    </section>
</template>

<style lang="less" scoped>
    @import url("../../styles/frame/chat/GroupFrame.less");
</style>