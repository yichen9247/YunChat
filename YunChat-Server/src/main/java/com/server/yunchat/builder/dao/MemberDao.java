package com.server.yunchat.builder.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.server.yunchat.builder.model.MemberModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberDao extends BaseMapper<MemberModel> {}
