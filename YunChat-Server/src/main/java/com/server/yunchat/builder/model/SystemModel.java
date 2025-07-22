package com.server.yunchat.builder.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("yun_system")
public class SystemModel {
    @TableId
    public Integer id;
    public String name;
    public String value;
}
