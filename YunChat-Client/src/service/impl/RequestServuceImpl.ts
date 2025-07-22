import utils from '@/scripts/utils';
import { EventType } from '@/enums/EventType';
import { ToastType } from '@/enums/ToastType';
import { RequestService } from '../RequestService';
import { socketIOClientInstance } from '../ServiceInstance';
import { arrayDataType, noticeBoardType, reportFormType, restfulType } from '../../../types';

/**
 * 请求服务实现类
 * @author yichen9247
 */
export class RequestServiceImpl implements RequestService {
    // 统一消息内容发送
    private sendMessage(
        data: object,
        event: string,
        callback: (response: any) => void,
    ): void {
        socketIOClientInstance.sendSocketEmit({
            event, data,
            callback: this.createResponseHandler(callback)
        });
    }

    // 统一消息响应处理
    private createResponseHandler(callback: (response: restfulType<any>) => void) {
        return async (response: restfulType<any>): Promise<void> => {
            if (response.code !== 200) {
                utils.showToasts(ToastType.ERROR, response.message);
                return;
            }
            callback(response.data);
        };
    }

    /**
     * @name 获取公告列表
     * @param page 页码
     * @param limit 码数
     * @param callback 回调
     */
    getNoticeList(page: number, limit: number, callback: (response: arrayDataType<noticeBoardType>) => void): void {
        this.sendMessage({ page, limit }, EventType.SEARCH_NOTICE, callback);
    }

    /**
     * @name 发送用户举报
     * @param formData 表单
     * @param callback 回调
     */
    addUserReport(formData: reportFormType, callback: (response: any) => void): void {
        this.sendMessage(formData, EventType.REPORT_USER, callback);
    }
}