package com.server.yunchat.builder.open.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SendRobotMessageApiModel {
    @NotNull(message = "缺少必要的参数")
    public Integer obj;
    @NotNull(message = "缺少必要的参数")
    public Long tar;
    @NotBlank(message = "缺少必要的参数")
    public String type;
    @NotBlank(message = "缺少必要的参数")
    public String content;
}
