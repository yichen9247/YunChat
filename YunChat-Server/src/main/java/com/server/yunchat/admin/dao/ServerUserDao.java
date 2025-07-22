package com.server.yunchat.admin.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.server.yunchat.admin.mod.ServerUserModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ServerUserDao extends BaseMapper<ServerUserModel> {}
