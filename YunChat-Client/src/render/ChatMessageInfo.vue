<script setup lang="ts">
    import { computed } from 'vue'
    import utils from "@/scripts/utils"
    import HandUtils from "@/scripts/HandUtils"
    import { StorageType } from '@/enums/StorageType'
    import { messageType, userInfoType } from '../../types'
    import { DialogServiceImpl } from '@/service/impl/DialogServiceImpl'

    const dialogServiceImpl = new DialogServiceImpl();
    const applicationStore = utils.useApplicationStore();
    const props = defineProps({
        showUserInfo: {
            type: Boolean, required: true
        },
        message: {
            type: Object as PropType<messageType>, required: true
        }
    });
    const isCurrentUser = computed(() => props.message.uid === applicationStore.userInfo.uid && applicationStore.loginStatus);

    const messageInfo = computed(() => ({
        isSelf: isCurrentUser.value,
        user: HandUtils.getUserInfoByUid(props.message.uid),
        typeClass: isCurrentUser.value ? 'chatCode-T' : 'chatCode-O'
    }));

    const mouseState = reactive({
        timer: null,
        duration: 0
    });

    const handleAvatarInteraction = (username: string) => ({
        async mousedown() {
            await HandUtils.checkClientLoginStatus((): void => {
                mouseState.timer = setInterval(() => {
                    if (mouseState.duration++ >= 1) {
                        applicationStore.setChantInput(`${applicationStore.chantInput}@${username} `)
                        resetMouseState()
                    }
                }, 300);
            });
        },
        mouseup: resetMouseState,
        touchend: resetMouseState
    });

    const resetMouseState = (): void => {
        mouseState.duration = 0
        clearInterval(mouseState.timer)
    }

    const getStorageFileUrl = (type: string) => computed(() => 
        HandUtils.getStorageFileUrl({
            path: messageInfo.value.user.avatar, type
        })
    ).value;

    const getUserTypeByInfo = (userinfo: userInfoType): string => {
		switch (userinfo.permission) {
			case 1:
				return "admin";
			case 2:
				return "robot";
			default:
				return "normal";
		}
	}
</script>

<template>
    <div class="message-info" :style="{ justifyContent: isCurrentUser ? 'flex-end' : 'flex-start' }">
        <div class="avatar-box" 
            v-if="!isCurrentUser && showUserInfo"
            :type="getUserTypeByInfo(messageInfo.user)" 
            v-on="handleAvatarInteraction(messageInfo.user.nick)"
            @click.once="dialogServiceImpl.openUserCenterDialog(messageInfo.user)"
        >
            <img class="avatar" v-lazy="getStorageFileUrl(StorageType.AVATAR)" draggable="false" :alt="messageInfo.user.nick"/>
        </div>

        <div class="content-box" :style="{ alignItems: isCurrentUser ? 'flex-end' : 'flex-start' }" :data-self="isCurrentUser">
            <div class="user-info" v-if="['text', 'file', 'image', 'video', 'invite'].includes(message.type) && showUserInfo">
                <template v-if="!isCurrentUser">
                    <span class="user-name">{{ messageInfo.user.nick }}</span>
                    <span class="user-time">{{ message.time.split(' ')[1] }}</span>
                </template>
                <template v-else>
                    <span class="user-time">{{ message.time.split(' ')[1] }}</span>
                    <span class="user-name">{{ messageInfo.user.nick }}</span>
                </template>
            </div>
            <TempleteMessage v-if="['file', 'image', 'video', 'invite'].includes(message.type)" :message="message"/>
            <div class="message-content" v-html="message.content" v-else-if="message.type === 'text'"></div>
        </div>

        <div class="avatar-box" v-if="isCurrentUser && showUserInfo" 
            :type="getUserTypeByInfo(messageInfo.user)"
            @click="dialogServiceImpl.openUserCenterDialog(messageInfo.user)"
        >
            <img class="avatar" v-lazy="getStorageFileUrl(StorageType.AVATAR)" draggable="false" :alt="messageInfo.user.nick"/>
        </div>
    </div>
</template>