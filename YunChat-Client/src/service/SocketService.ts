import { Socket } from "socket.io-client";
import { groupInfoType, messageType, restfulType, userInfoType } from "../../types";

export interface SocketService {
    startSocketIo(): Socket;
    joinGroupRoom(gid: number, callback: (response: restfulType<groupInfoType>) => void): void;
    leaveGroupRoom(gid: number, callback: (response: restfulType<groupInfoType>) => void): void;
    
    getChanInfo(gid: number, callback: (response: restfulType<groupInfoType>) => void): void;
    getUserList(callback: (response: restfulType<userInfoType[]>) => void): void;
    getGroupList(callback: (response: restfulType<groupInfoType[]>) => void): void;
    joinMessageRoom(list: number[], callback?: (response: restfulType<any>) => void): void
    getMessageList(obj: number, tar: number, callback: (response: restfulType<messageType[]>) => void): void;
    addSocketEventListener(eventHandlers: Record<string, (messageObject: restfulType<any>) => void | Promise<void>>): void;
}