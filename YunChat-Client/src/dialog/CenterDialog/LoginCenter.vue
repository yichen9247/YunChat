<script setup lang="ts">
    import QRCode from 'qrcode'
    import { Reactive, ref } from 'vue'
    import utils from '@/scripts/utils'
    import socket from '@/socket/config'
    import HandUtils from '@/scripts/HandUtils'
    import { EventType } from '@/enums/EventType'
    import { ToastType } from '@/enums/ToastType'
    import { SocketIOClient } from '@/socket/SocketIOClient'
    import { userServiceInstance } from '@/service/ServiceInstance'
    import { restfulType, loginFormType, userAuthStatus } from '../../../types'

    let intervalTimer: any;
    const qrcode = ref(null);
    const exStatus = ref(false);
    const activeTab = ref('login');
    const qrcodeLoading = ref(false);

    const loginForm: Reactive<loginFormType> = reactive({ 
        username: localStorage.getItem("yun_username") ? localStorage.getItem("yun_username") : "", 
        password: localStorage.getItem("yun_password") ? localStorage.getItem("yun_password") : ""
    });
    const registerForm: Reactive<loginFormType> = reactive({ username: '', password: '' });

    /**
     * Handle login/register authentication
     * @param {number} mode - 1 for login, 2 for register
     * @param {Object} form - Form data containing username and password
     */
    const handleAuth = async (mode: number, form: loginFormType) => {
        if (!form.username.trim() || !form.password.trim()) {
            utils.showToasts(ToastType.WARNING, '账号或密码为空');
            return;
        }

        if (mode === 1) {
            userServiceInstance.userLogin(form, (response: restfulType<any>) => {
                utils.showToasts(ToastType.SUCCESS, response.message);
                HandUtils.handleUserLogin(response, loginForm.password);
            });
        } else userServiceInstance.userRegister(form, (response: restfulType<any>) => {
            utils.showToasts(ToastType.SUCCESS, response.message);
            HandUtils.handleUserLogin(response, loginForm.password);
        });
    }

    const formButtonClick = async (mode: number): Promise<void> => {
        await handleAuth(mode, mode === 1 ? loginForm : registerForm);
    }

    const tabChange = async (key: string): Promise<void> => {
        if (key === 'scan') {
            onScanQrcodeTabShow();
        } else {
            qrcode.value = null;
            clearInterval(intervalTimer);
        }
    }

    const onScanQrcodeTabShow = (): void => {
        exStatus.value = false;
        qrcodeLoading.value = true;
        userServiceInstance.userScanLogin((response: restfulType<any>) => {
            setTimeout(() => {
                qrcodeLoading.value = false;
                QRCode.toDataURL(JSON.stringify(response.data), {
                    errorCorrectionLevel: 'M'
                }).then(url => {
                    qrcode.value = url;
                }).catch(() => {
                    utils.showToasts(ToastType.ERROR, "生成二维码失败");
                });
            }, 500);
            intervalTimer = setInterval(async () => {
                SocketIOClient.socketIO.emit(EventType.USER_SCAN_LOGIN_STATUS, {
                    qid: response.data.qid,
                    tempToken: SocketIOClient.tempToken
                }, async (statusResponse: restfulType<userAuthStatus>) => {
                    if (statusResponse.code === 200) {
                        await HandUtils.handleUserLogin(statusResponse, loginForm.password)
                        clearInterval(intervalTimer);
                    } else 
                    if (statusResponse.code === 401) {
                        exStatus.value = true;
                        clearInterval(intervalTimer);
                        await utils.showToasts(ToastType.ERROR, statusResponse.message);
                    }
                });
		    }, 500);
        });
    }
    
    onUnmounted((): void => {
        qrcode.value = null;
        exStatus.value = false;
        clearInterval(intervalTimer);
    });
</script>

<template>
    <div class="login-dialog">
        <el-tabs v-model="activeTab" class="login-tabs" @tab-change="tabChange">
            <el-tab-pane label="登录" name="login">
                <div class="form-box">
                    <el-form :model="loginForm" @keydown.enter="formButtonClick(1)">
                        <el-form-item label="账号" prop="username">
                            <el-input v-model="loginForm.username" clearable tabindex="-1"/>
                        </el-form-item>

                        <el-form-item label="密码" prop="password">
                            <el-input v-model="loginForm.password" type="password" clearable show-password tabindex="-1"/>
                        </el-form-item>

                        <el-button class="login-btn" type="primary" @click="formButtonClick(1)">立即登录</el-button>
                    </el-form>
                </div>
            </el-tab-pane>

            <el-tab-pane label="注册" name="register">
                <div class="form-box">
                    <el-form :model="registerForm" @keydown.enter="formButtonClick(2)">
                        <el-form-item label="账号" prop="username">
                            <el-input v-model="registerForm.username" clearable tabindex="-1"/>
                        </el-form-item>
                        <el-form-item label="密码" prop="password">
                            <el-input v-model="registerForm.password" type="password" clearable show-password tabindex="-1"/>
                        </el-form-item>
                        <el-button class="login-btn" type="primary" @click="formButtonClick(2)">
                            立即注册
                        </el-button>
                    </el-form>
                </div>
            </el-tab-pane>

            <el-tab-pane label="扫码" name="scan" v-if="socket.server.config.scanLogin === 'true'">
                <div class="form-box">
                    <div class="app-box">
                        <div class="qrcode-box" v-loading="qrcodeLoading" element-loading-text="正在请求中" element-loading-background="rgba(255, 255, 255, 0.95)">
                            <img v-if="qrcode" class="qrcode" v-lazy="qrcode" alt="扫码登录" draggable="false">
                            <div class="expire-box" v-if="exStatus" @click="onScanQrcodeTabShow">
                                <svg t="1742615455835" class="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="3521" width="48" height="48"><path d="M938.336973 255.26894c-16.685369-6.020494-35.090879 2.752226-40.939358 19.437594l-24.770032 69.493701c-29.070385-65.537376-74.998152-123.162103-133.48295-166.337645-185.947253-137.611288-450.848984-100.112212-590.180413 83.942886C81.534688 350.908785 52.980346 460.653788 68.805644 570.742819c15.825298 110.605073 74.48211 208.481102 164.789518 275.394591 75.686209 55.904586 164.273476 83.082815 252.172686 83.082815 128.494541 0 255.26894-57.624727 338.007727-166.853687 36.639006-48.335965 61.581052-102.348396 74.48211-160.833193 3.78431-17.373425-7.224593-34.402822-24.426004-38.187133-17.201411-3.78431-34.402822 7.052579-38.187133 24.426004-10.836889 49.36805-31.994625 95.123803-62.957164 135.891147-118.173694 156.016798-342.996136 187.839409-500.90509 70.869814-76.546279-56.592642-126.086343-139.33143-139.503444-232.907106-13.417101-93.059634 10.664875-185.775239 67.77356-261.11742C318.05409 144.491853 542.704519 112.497228 700.785486 229.466823c57.280699 42.315471 100.112212 100.972283 123.334117 167.197715l-110.261045-43.003528c-16.513355-6.364522-35.090879 1.720141-41.627415 18.233496-6.536536 16.513355 1.720141 35.090879 18.233496 41.627415l162.38132 63.473207c3.78431 1.548127 7.740635 2.236183 11.69696 2.236183 0.516042 0 1.032085-0.172014 1.548127-0.172014 1.204099 0.172014 2.408198 0.688056 3.612296 0.688056 13.245087 0 25.630102-8.256677 30.274483-21.32975l57.796741-161.693264C963.623047 279.694944 955.022342 261.289434 938.336973 255.26894z" fill="var(--dominColor)" p-id="3522"></path></svg>
                                <span class="expire-text">二维码已过期</span>
                            </div>
                        </div>
                        <p class="qrcode-description">请在
                            <el-link type="primary" target="_blank">
                                30秒内
                            </el-link>完成登录
                        </p>
                    </div>
                </div>
            </el-tab-pane>
        </el-tabs>
    </div>
</template>

<style lang="less">
    @import url("../../styles/dialog/CenterDialog/LoginCenter.less");
</style>