package com.server.yunchat.client.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.server.yunchat.client.mod.ClientUserModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ClientUserDao extends BaseMapper<ClientUserModel> {
}
