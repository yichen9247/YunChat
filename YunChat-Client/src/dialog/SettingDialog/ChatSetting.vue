<script setup lang="ts">
    import AudioUtils from "@/scripts/audioUtils"
    import { Check, Close } from '@element-plus/icons-vue'

    const audioSelect = ref(localStorage.getItem('audio'));
    const audioSwitch = ref(localStorage.getItem('audio-switch') === 'true');
    const noticeSwitch = ref(localStorage.getItem('notice-switch') === 'true');

    /**
     * Handle notification sound type change
     * @param {string} selectedSound - The newly selected sound value
     */
    const audioSelectChange = async (selectedSound: string): Promise<void> => {
        localStorage.setItem('audio', selectedSound);
        await AudioUtils.playNoticeVoice();
    }

    const audioSwitchChange = async (enabled: string): Promise<void> => {
        localStorage.setItem('audio-switch', enabled);
    }

    const noticeSwitchChange = async (enabled: string): Promise<void> => {
        localStorage.setItem('notice-switch', enabled);
    }
</script>

<template>
    <div class="chat-setting">
        <p class="set-title">声音提醒</p>
        <div class="switch-item">
            <el-switch size="large" inline-prompt :active-icon="Check" v-model="audioSwitch" :inactive-icon="Close" @change="audioSwitchChange" tabindex="-1"/>
            <span class="label">{{ audioSwitch ? '开' : '关' }}</span>
        </div>
        <p class="set-title">后台通知</p>
        <div class="switch-item">
            <el-switch size="large" inline-prompt :active-icon="Check" v-model="noticeSwitch" :inactive-icon="Close" @change="noticeSwitchChange" tabindex="-1"/>
            <span class="label">{{ noticeSwitch ? '开' : '关' }}</span>
        </div>

        <p class="set-title">提示声音</p>
        <el-radio-group
            size="large"
            class="audio-group"
            v-model="audioSelect"
            @change="audioSelectChange"
        >
            <el-radio-button label="默认" value="default" />
            <el-radio-button label="苹果" value="apple" />
            <el-radio-button label="陌陌" value="momo" />
            <el-radio-button label="滑稽" value="huaji" />
        </el-radio-group>
    </div>
</template>

<style lang="less">
    div.chat-setting {
        width: 100%;

        p.set-title {
            font-size: 16px;
        }

        div.audio-group {
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

        div.switch-item {
            display: flex;
            margin-top: 5px;
            margin-bottom: 10px;
            align-items: center;

            span.label {
                margin-top: 2.2px;
                margin-left: 10px;
            }
        }
    }
</style>