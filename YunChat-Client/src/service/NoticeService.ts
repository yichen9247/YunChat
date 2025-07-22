import { messageType, restfulType } from "types";

export interface NoticeService {
    showMessageNotice(response: restfulType<messageType>): void
}
