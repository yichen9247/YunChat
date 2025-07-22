import { restfulType, userInfoType } from "../../types";

export interface ClientService {
    getClientHeader(): object;
    getClientPlatform(): string;
    downloadFile(url: string): Promise<void>;
    clientPing(callback?: (response: restfulType<any>) => void): void;
    clientInit(callback: (response: restfulType<userInfoType>) => void): void;
}