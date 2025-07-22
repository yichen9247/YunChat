<script setup lang="ts">
    import config from "@/socket/config"
</script>

<template>
    <div class="about-page">
        <p class="description">{{ config.application.description }}</p>
        <p style="display: flex; align-items: center; flex-wrap: wrap;">
            安卓下载：
            <el-link type="primary" target="_blank" :href="config.application.appDownload" >点击下载</el-link>
        </p>
        <p style="display: flex; align-items: center; flex-wrap: wrap;">
            项目作者：
            <el-link type="primary" target="_blank" :href="config.application.author" >{{ config.application.authorName }}</el-link>
        </p>
        <p style="display: flex; align-items: center; flex-wrap: wrap;">
            项目文档：
            <el-link type="primary" target="_blank" :href="config.application.appDoc" >{{ config.application.appDoc }}</el-link>
        </p>
        <p style="display: flex; align-items: center; flex-wrap: wrap;">
            项目地址：
            <el-link type="primary" target="_blank" :href="config.application.github" >{{ config.application.github }}</el-link>
        </p>
    </div>
</template>

<style lang="less">
    @import url("../../styles/dialog/SettingDialog/AboutPage.less");
</style>