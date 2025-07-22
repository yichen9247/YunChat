<script setup lang="ts">
    import Swal from 'sweetalert2';
    import { Reactive } from 'vue';
    import utils from '@/scripts/utils';
    import HandUtils from '@/scripts/HandUtils';
    import { ToastType } from '@/enums/ToastType';
    import { StorageType } from '@/enums/StorageType';
    import { SocketIOClient } from '@/socket/SocketIOClient';
    import { groupInfoType, restfulType } from '../../../types';
    import { groupServiceInstance, socketIOClientInstance } from '@/service/ServiceInstance';

    let history: string = null;
    const isLoading = ref(false);
    const searchInput = ref("");
    const groupList: Reactive<groupInfoType[]> = reactive([]);

    const joinedGroupMap = computed(() => {
        return new Set(SocketIOClient.applicationStore.chatGroupList.map(group => group.gid));
    });

    const searchOfGroup = async (): Promise<void> => {
        if (isLoading.value || history === searchInput.value) return;
        if (!searchInput.value.trim()) return utils.showToasts(ToastType.WARNING, "请输入搜索内容");

        isLoading.value = true;
        history = searchInput.value;
        
        try {
            groupServiceInstance.searchGroup(searchInput.value, (response: restfulType<groupInfoType[]>): void => {
                groupList.splice(0, groupList.length);
                if (response.data.length === 0) {
                    utils.showToasts(ToastType.WARNING, '没有搜索到相关群聊');
                }
                groupList.splice(0, groupList.length, ...response.data);
            });
        } catch(e: any) {
            utils.showToasts(ToastType.ERROR, e);
        } finally {
            isLoading.value = false;
        }
    }

    const userJoinGroup = (gid: number): void => {
        groupServiceInstance.userJoinGroup(gid, (response: restfulType<groupInfoType[]>): void => {
            Swal.close();
            utils.showToasts(ToastType.SUCCESS, response.message);
            socketIOClientInstance.initClientGroupList();
        });
    }
</script>

<template>
    <div class="search-group">
        <div class="input-box">
            <el-input v-model="searchInput" placeholder="请输入要搜索的群聊（支持模糊搜索）" clearable @keyup.enter="searchOfGroup"/>
            <el-button type="primary" @click="searchOfGroup" :loading="isLoading">搜索</el-button>
        </div>
        <ul class="search-result" :data-result="groupList.length === 0">
            <li class="group-item" v-for="(item, index) in groupList" :key="index">
                <div class="group-left">
                    <img class="group-avatar" v-lazy="HandUtils.getStorageFileUrl({
                        path: item.avatar,
                        type: StorageType.AVATAR
                    })" :alt="item.name" draggable="false">
                    <div class="group-basic">
                        <p class="group-name">{{ item.name }}</p>
                        <p class="group-desc">群号：{{ item.gid }}</p>
                    </div>
                </div>
                <el-button @click="userJoinGroup(item.gid)" :disabled="joinedGroupMap.has(item.gid)">{{ joinedGroupMap.has(item.gid) ? '已加入' : '加入' }}</el-button>
            </li>
        </ul>
    </div>
</template>

<style lang="less">
    @import url("../../styles/dialog/CenterDialog/SearchCenter.less");
</style>