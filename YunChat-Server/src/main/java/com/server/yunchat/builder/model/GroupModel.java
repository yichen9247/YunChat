package com.server.yunchat.builder.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.server.yunchat.client.mod.ClientUserModel;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
@TableName("yun_group")
public class GroupModel {
    @TableId(type = IdType.AUTO)
    public Integer gid;
    public String time;
    public String avatar;
    public Integer status;

    @Size(min = 2, max = 6, message = "名称不合规")
    public String name;

    @Size(min = 5, max = 100, message = "公告不合规")
    public String notice;

    public List<MessageModel> message;
    public List<ClientUserModel> member;
}
