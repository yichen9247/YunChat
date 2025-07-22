<script setup lang="ts">
    import utils from "@/scripts/utils"
    import { socketIOClientInstance } from '@/service/ServiceInstance'

    onMounted((): void => {
        socketIOClientInstance.startSocketIo();
    });

    const applicationStore = utils.useApplicationStore();
    const mobileStatus = computed(() => applicationStore.isDeviceMobile);
</script>

<template>
    <div :class="['main-container', mobileStatus ? 'container-mobile' : 'container-pc']">
        <ChatSideBar v-if="!mobileStatus"/>
        <RouterView/>
    </div>
</template>

<style lang="less">
    @import url("./assets/index.less");
    @import url("./styles/YunChat.less");
    @import url("./assets/paces/pace.less");
    @import url("./assets/various/media.less");
    @import url("./assets/various/variour.less");
</style>