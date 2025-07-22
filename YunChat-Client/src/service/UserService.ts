import { loginFormType, restfulType } from "../../types";

export interface UserService {
    userScanLogin(callback: (response: restfulType<any>) => void): void;
    updateUserNick(nick: string, callback: (response: restfulType<any>) => void): void;
    updateUserAvatar(path: string, callback: (response: restfulType<any>) => void): void;
    userLogin(formData: loginFormType, callback: (response: restfulType<any>) => void): void;
    updateUserPassword(password: string, callback: (response: restfulType<any>) => void): void;
    userRegister(formData: loginFormType, callback: (response: restfulType<any>) => void): void;
    userOnlineLogin(status: number, callback?: (response: restfulType<any>) => void): void;
}