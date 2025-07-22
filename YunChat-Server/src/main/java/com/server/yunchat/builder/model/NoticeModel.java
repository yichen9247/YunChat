package com.server.yunchat.builder.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@TableName("yun_notice")
public class NoticeModel {
    @TableId
    public Integer id;
    public String time;

    @Size(min = 4, max = 10, message = "标题不合规")
    public String title;

    @Size(min = 5, max = 200, message = "内容不合规")
    public String content;
}
