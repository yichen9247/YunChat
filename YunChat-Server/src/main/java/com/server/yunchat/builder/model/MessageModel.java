package com.server.yunchat.builder.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@TableName("yun_message")
public class MessageModel {
    public Long tar;
    public Long uid;
    public Integer obj;
    @TableId
    public String sid;
    public int deleted;
    public String type;
    public String time;

    @Size(min = 1, max = 500, message = "消息长度不合规")
    public String content;
}
