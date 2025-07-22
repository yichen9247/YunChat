package com.server.yunchat.admin.mod;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@TableName("yun_user")
public class ServerUserModel {
    @TableId
    public Long uid;
    public String qqId;
    public String avatar;
    public String regTime;
    public Integer status;
    public Integer permission;

    @Size(min = 2, max = 8, message = "昵称不合规")
    public String nick;

    @Size(min = 5, max = 20, message = "账号不合规")
    public String username;

    @Size(min = 5, max = 20, message = "密码不合规")
    public String password;
}
