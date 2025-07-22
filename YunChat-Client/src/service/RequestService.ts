import { addPostCommentType, arrayDataType, forumCommentType, forumPostType, noticeBoardType, reportFormType } from "../../types";

export interface RequestService {
    addUserReport(formData: reportFormType, callback: (response: any) => void): void;
    getNoticeList(page: number, limit: number, callback: (response: arrayDataType<noticeBoardType>) => void): void;
}