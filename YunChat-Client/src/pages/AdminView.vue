<script setup>
    import utils from "@/scripts/utils"
    import { useRouter } from "vue-router"
    import { adminViews } from "@/scripts/adminLayout"
    import { UserAuthType } from "@/enums/UserAuthType"

    const router = useRouter();
    const oneDialogStore = utils.useOnelDialogStore();
</script>

<template>
    <Authority :permission="UserAuthType.ADMIN_AUTHENTICATION" placehoder>
        <section class="page-admin yun-shadow">
            <div class="yun-head-box">
                <el-page-header @back="router.back()">
                    <template #content>
                        <span class="text-large font-600 mr-3">管理后台</span>
                    </template>
                </el-page-header>
            </div>
            <div class="frame-content">
                <AdminMenu/>
                <div class="container">
                    <div class="admin-item-content item-content">
                        <component :is="adminViews.find(item => item.key === oneDialogStore.adminFrameSelect).component"/>
                    </div>
                </div>
            </div>
        </section>
    </Authority>
</template>

<style lang="less">
    @import url("../styles/pages/AdminView.less");
</style>