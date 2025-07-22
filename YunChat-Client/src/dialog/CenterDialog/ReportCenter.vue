<script setup lang="ts">
    import { Reactive } from 'vue'
    import Swal from 'sweetalert2'
    import utils from '@/scripts/utils'
    import HandUtils from '@/scripts/HandUtils'
    import { ToastType } from '@/enums/ToastType'
    import { restfulType, reportFormType } from '../../../types'
    import { DialogServiceImpl } from '@/service/impl/DialogServiceImpl'
    import { messageServiceInstance, requestServiceInstance } from '@/service/ServiceInstance'

    const dialogServiceImpl = new DialogServiceImpl();
    const formData: Reactive<reportFormType> = reactive({
        sid: "",
        reason: "",
        reportedId: null
    });

    const onReportChange = (sid: string): void => {
        const messageInfo = HandUtils.getMessageInfoBySid(sid);
        formData.reportedId = messageInfo.uid;
    }

    const applicationStore = utils.useApplicationStore();
    const reversedMessages = computed((): any => applicationStore.messageList.slice().reverse());

    const onReportSubmit = async (): Promise<void> => {
        if (!formData.reportedId || formData.sid === "" || formData.reason.trim() === "") return utils.showToasts(ToastType.WARNING, '请填写完整信息');
        Swal.close()
        requestServiceInstance.addUserReport(formData, (response: restfulType<any>): void => {
            dialogServiceImpl.openSwalDialog({
                icon: "success",
                title: "举报成功",
                allowClose: true,
                text: "感谢你的举报，我们将认真审核"
            });
        });
    }
</script>

<template>
    <div class="report-user">
        <el-form class="report-form" label-width="auto">
            <div class="flex-form">
                <el-form-item label="举报用户">
                    <el-input v-model="formData.reportedId" placeholder="将根据聊天记录自动选择" disabled/>
                </el-form-item>

                <el-form-item label="举报理由">
                    <el-input type="textarea" :rows="3" v-model="formData.reason" placeholder="请输入举报理由" maxlength="50" show-word-limit/>
                </el-form-item>
                <el-button type="primary" size="large" style="width: 100%; margin-top: auto;" @click="onReportSubmit">确认举报</el-button>
            </div>
            <div class="flex-form">
                <el-radio-group v-model="formData.sid" class="radio-group" v-if="reversedMessages.length > 0">
                    <el-radio size="large" v-for="(item, index) in reversedMessages" :key="index" :value="item.sid" @change="onReportChange">{{ `${HandUtils.getUserInfoByUid(item.uid).nick}：${ item.type === 'text' ? item.content : messageServiceInstance.getMessageType(item.type) }` }}</el-radio>
                </el-radio-group>
                <div v-else style="height: 100%;">
                    <el-empty description="暂无更多消息记录" style="height: 100%;" />
                </div>
            </div>
        </el-form>
    </div>
</template>

<style lang="less">
    @import url("../../styles/dialog/CenterDialog/ReportUser.less");
</style>