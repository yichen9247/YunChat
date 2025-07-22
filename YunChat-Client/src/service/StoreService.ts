import { groupInfoType, userInfoType } from "../../types";

export const createDefaultUserInfo = (): userInfoType => ({
    status: 0,
    nick: "未登录",
    permission: 0,
    username: null,
    avatar: "0/default/default.png",
    uid: localStorage.getItem("yun_uid")
});

export const createDefaultGroupInfo = (): groupInfoType => ({
    gid: 0,
    status: 0,
    member: [],
    name: "正在初始化中",
    notice: "正在初始化中",
    time: "0000-00-00 00:00:00",
    avatar: "default_avatar.png",
});
