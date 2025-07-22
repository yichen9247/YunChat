import { userInfoType } from "../../types";

export interface DialogService {
    openAboutDialog(): void;
    openLoginDialog(): void;
    openUserLogoutDialog(): void;
    openUserLogoutDialog(): void;
    openImageDialog(src: string): void;
    openSettingDialog(index: number): void;
    openTextContentDialog(content: string): void
    openUserCenterDialog(user: userInfoType): void;
    openSwalDialog({ icon, title, text, allowClose }): void;
    openCustomSwalDialog(content: any, { title, props, width, autoWidth, className }): void;
}