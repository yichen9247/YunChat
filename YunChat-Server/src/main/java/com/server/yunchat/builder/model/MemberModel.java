package com.server.yunchat.builder.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("yun_member")
public class MemberModel {
    public Long uid;
    @TableId
    public Integer id;
    public Integer gid;
    public String time;
}
