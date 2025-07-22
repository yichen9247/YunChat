<script setup>
    import utils from "@/scripts/utils"

    defineProps({
        permission: { type: Number, required: true },
        placehoder: { type: Boolean, required: false, default: false }
    });

    const applicationStore = utils.useApplicationStore();
</script>

<template>
    <slot v-if="applicationStore.userInfo.permission >= permission && applicationStore.loginStatus"/>
    <ResultView v-else-if="placehoder" :result="403"/>
</template>