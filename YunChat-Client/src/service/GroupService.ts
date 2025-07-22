import { restfulType } from "../../types";

export interface GroupService {
    searchGroup(name: string, callback: (response: restfulType<any>) => void): void;
    userJoinGroup(gid: number, callback: (response: restfulType<any>) => void): void;
    userExitGroup(gid: number, callback: (response: restfulType<any>) => void): void;
}