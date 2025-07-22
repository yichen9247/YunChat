package com.server.yunchat.client.mod;

import jakarta.validation.constraints.NotBlank;

public class ScanLoginModel {
    @NotBlank(message = "缺少必要的参数")
    public String qid;
    @NotBlank(message = "缺少必要的参数")
    public Integer status;
}
