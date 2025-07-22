<script setup lang="ts">
    import { Reactive } from 'vue'
    import { arrayDataType, noticeBoardType } from '../../types'
    import { RequestServiceImpl } from '@/service/impl/RequestServuceImpl'
    
    const pages: Ref<number> = ref(1);
    const total: Ref<number> = ref(0);
    const loading: Ref<boolean> = ref(true);
    const noticeList: Reactive<any[]> = reactive([]);
    const requestServiceImpl = new RequestServiceImpl();

    const getNoticeList = (): void => {
        requestServiceImpl.getNoticeList(pages.value, 10, (data: arrayDataType<noticeBoardType>) => {
            total.value = data.total;
            noticeList.splice(0, noticeList.length, ...data.items);
        });
        loading.value = false;
    }

    const currentChange = (page: number): void => {
        pages.value = page;
        getNoticeList();
    }
    onMounted((): void => getNoticeList());
</script>

<template>
    <div class="notice-content">
        <div class="post-list-container" v-loading="loading">
            <div class="post-list" v-if="noticeList.length > 0">
                <NoticeItem v-for="(item, index) in noticeList" :key="index" :post="item"/>
                <el-pagination class="pagination" background layout="prev, pager, next" :total="total" :hide-on-single-page="true" @currentChange="currentChange"/>
            </div>
            <div v-else style="height: 100%;">
                <el-empty description="暂无更多通知" style="height: 100%;" />
            </div>
        </div>
    </div>
</template>

<style lang="less">
    @import url("../styles/notice/NoticeContent.less");
</style>