<script setup lang="ts">
    import themeUtils from '@/scripts/themeUtils'

    // Get saved theme from localStorage or use default
    const themeSelect = ref(localStorage.getItem('theme') || 'default');

    /**
     * Handle theme change event
     * @param {string} selectedTheme - The newly selected theme value
     */
    const themeSelectChange = async (selectedTheme: string): Promise<void> => {
        localStorage.setItem('theme', selectedTheme);
        await themeUtils.setDeviceTheme();
    }
</script>

<template>
    <div class="theme-select">
        <p class="set-title">主题颜色</p>
        <el-radio-group 
            @change="themeSelectChange"
            v-model="themeSelect" size="large" class="theme-group"
        >
            <el-radio-button label="默认" value="default" />
            <el-radio-button label="雅灰" value="pureshs" />
            <el-radio-button label="雅蓝" value="yalansh" />
            <el-radio-button label="柔粉" value="roufenh" />
        </el-radio-group>
    </div>
</template>

<style lang="less">
    div.theme-select {
        width: 100%;

        p.set-title {
            font-size: 16px;
        }

        div.theme-group {
            width: 100%;
            margin: 5px 0;
            margin-top: 15px;
        
            label {
                flex: 1;
                width: 25%;

                span {
                    width: 100%;
                }
            }
        }
    }
</style>