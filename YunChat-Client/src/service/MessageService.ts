import { groupInfoType, messageType, restfulType } from "../../types";

export interface MessageService {
    getMessageType(type: string): string;
    getGroupMessageType(type: string, content: string)
    getNewMessageType( length: number, type: string, content: string): string
    sendTextMessage(content: string, callback?: (response: restfulType<messageType>) => void): void;
    sendFileMessage(content: string, callback?: (response: restfulType<messageType>) => void): void;
    sendImageMessage(content: string, callback?: (response: restfulType<messageType>) => void): void;
    sendVideoMessage(content: string, callback?: (response: restfulType<messageType>) => void): void;
    sendAgentMessage(content: string, callback?: (response: restfulType<messageType>) => void): void;
    sendGroupInvitedMessage(group: groupInfoType, callback?: (response: restfulType<messageType>) => void): void;
}