<script setup lang="ts">
    import utils from '@/scripts/utils';
    import HandUtils from '@/scripts/HandUtils';
    import { ToastType } from '@/enums/ToastType';
    import { StorageType } from '@/enums/StorageType';
    import { restfulType, userInfoType } from '../../../types';
    import { adminServiceInstance } from '@/service/ServiceInstance';
    import { DialogServiceImpl } from '@/service/impl/DialogServiceImpl';

    const dialogServiceImpl = new DialogServiceImpl();

    const props = defineProps({
        gid: {
            required: true,
            type: Number
        },
        list: {
            required: true,
            type: Array<userInfoType>
        },
        onSuccess: {
            required: true,
            type: Function as PropType<() => void>
        } // 成功回调（Hook）
    });

    const handleDeleteMember = (uid: string) => {
        dialogServiceImpl.openSwalToastDialog({
            showDenyButton: true,
            showConfirmButton: false,
            denyButtonText: "确认移出",
            text: "是否确认移出该成员，此操作将无法回退？",
            denyCall() {
                adminServiceInstance.deleteMember(Number(uid), props.gid, (response: restfulType<any>): void => {
                    props.onSuccess();
                    utils.showToasts(ToastType.SUCCESS, response.message);
                }); 
            }
        });
    }
</script>

<template>
    <div class="admin-dialog admin-member-dialog">
        <ul class="member-list">
            <li class="member-list-item" v-for="(item, index) in list" :key="index">
                <span class="user-box">
                    <img class="user-avatar" v-lazy="HandUtils.getStorageFileUrl({
                        path: item.avatar,
                        type: StorageType.AVATAR
                    })" :alt="item.nick" draggable="false">
                    <div class="user-info">
                        <p class="user-nick">{{ item.nick }}</p>
                        <p class="user-uid">{{ item.uid }}</p>
                    </div>
                </span>
                <div class="button-sheet">
                    <el-button class="item-button" type="danger" @click="handleDeleteMember(item.uid)">移出</el-button>
                </div>
            </li>
        </ul>
    </div>
</template>

<style lang="less">
    @import url("../../styles/dialog/AdminDialog/AdminDialog.less");
    @import url("../../styles/dialog/AdminDialog/AdminMemberDialog.less");
</style>