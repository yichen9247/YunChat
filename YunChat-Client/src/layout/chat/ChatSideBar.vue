<script setup lang="ts">
    import { useRouter } from "vue-router"

    const router = useRouter();
    const currentRoute = computed(() => router.currentRoute.value.name);

    const onHome = computed(() => currentRoute.value === "home");
</script>

<template>
    <div class="sidebar-container yun-shadow" :data-route="currentRoute">
        <LeftSideBar/>
        <RightSideBar v-if="onHome"/>
    </div>
</template>

<style lang="less">
    @import url("../../styles/layout/chat/ChatSidebar.less");
</style>