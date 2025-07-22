package com.server.yunchat.client.man;

import com.server.yunchat.builder.model.LoginModel;

public class ClientLoginManage {
    public void insertClientLoginLog(LoginModel loginModel, Long uid, String address, String location, String platform, String username) {
        loginModel.setUid(uid);
        loginModel.setAddress(address);
        loginModel.setPlatform(platform);
        loginModel.setLocation(location);
        loginModel.setUsername(username);
    }
}