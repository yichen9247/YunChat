<script setup lang="ts">
    import utils from '@/scripts/utils'
    import { adminViews } from '@/scripts/adminLayout'
    
    const oneDialogStore = utils.useOnelDialogStore();
    onUnmounted((): string => oneDialogStore.setAdminFrameSelect("dash"));
    const handleSelect = (key: string) => oneDialogStore.setAdminFrameSelect(key);
</script>

<template>
    <div class="admin-menu">
        <el-menu default-active="dash" class="el-menu-vertical-admin" @select="handleSelect">
            <el-menu-item v-for="(item, index) in adminViews" :key="index" :index="item.key">
                <el-icon/>
                <span>{{ item.name }}</span>
            </el-menu-item>
        </el-menu>
    </div>
</template>

<style lang="less" scoped>
    @import url("../../styles/layout/admin/AdminMenu.less");
</style>