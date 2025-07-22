package com.server.yunchat.client.mod;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("yun_user")
public class ClientUserModel {
    @TableId
    public Long uid;
    public String nick;
    public String avatar;
    public Integer aiAuth;
    public String regTime;
    public Integer status;
    public String username;
    public Integer permission;
}
