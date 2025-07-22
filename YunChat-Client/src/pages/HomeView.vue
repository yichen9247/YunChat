<script setup lang="ts">
    import utils from '@/scripts/utils'
    const applicationStore = utils.useApplicationStore();
</script>

<template>
    <section class="page-home" tabindex="-1">
        <div class="home-content yun-shadow">
            <ChatHeadBox/>
            <div class="content-body">
                <ChatContent/>
            </div>
            <ChatFootBox/>
        </div>
        <ChatAside v-if="!applicationStore.isDeviceMobile"/>
        <audio id="audioRef" style="display: none!important;"></audio>
    </section>
</template>

<style lang="less" scoped>
    @import url("../styles/pages/HomeView.less");
</style>