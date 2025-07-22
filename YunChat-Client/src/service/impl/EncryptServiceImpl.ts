import CryptoJS from 'crypto-js';
import config from '@/socket/config';
import { EncryptService } from './../EncryptService';
import { deviceServiceInstance } from '../ServiceInstance';

/**
 * @name 加密服务实现类
 * @author yichen9247
 */
export class EncryptServiceImpl implements EncryptService {
    private readonly SECRET_KEY: string = config.server.config.secretKey;

    /**
     * @name 加密文本
     * @param plainText 待加密的文本
     * @returns Base64格式的加密字符串
     */
    private encryptText(plainText: string): string {
        return CryptoJS.AES.encrypt(plainText, CryptoJS.enc.Utf8.parse(this.SECRET_KEY), {
            mode: CryptoJS.mode.CBC,
            padding: CryptoJS.pad.Pkcs7,
            iv: CryptoJS.enc.Utf8.parse(this.SECRET_KEY)
        }).toString();
    }

    /**
     * @name 加密授权信息
     * @returns Promise<string> Base64格式的加密字符串
     */
    async encryptAuthorization(): Promise<string> {
        const timestamp = Date.now();
        const uid = localStorage.getItem("yun_uid") || "";
        const token = localStorage.getItem("yun_token") || "";
        const isDesktop = deviceServiceInstance.getDeviceIsDesktop();
        const plainText = `${uid}||${token}||${isDesktop ? "Desktop" : "Web"}||${timestamp}`;
        return this.encryptText(plainText);
    }

    /**
     * @name 加密登录信息
     * @returns Promise<string> Base64格式的加密字符串
     */
    async encryptLogin(username: string, password: string): Promise<string> {
        const timestamp = Date.now();
        const isDesktop = deviceServiceInstance.getDeviceIsDesktop();
        const plainText = `${username}||${password}||${isDesktop ? "Desktop" : "Web"}||${timestamp}`;
        return this.encryptText(plainText);
    }
}