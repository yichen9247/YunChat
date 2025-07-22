package com.server.yunchat.client.mod;

import jakarta.validation.constraints.NotBlank;

public class UserUpdateModel {
    @NotBlank(message = "缺少必要的参数")
    public String data;
}
