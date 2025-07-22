package com.server.yunchat.builder.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("yun_login")
public class LoginModel {
    @TableId
    public Integer id;
    public Long uid;
    public String time;
    public String address;
    public String platform;
    public String location;
    public String username;
}
