package com.server.yunchat.builder.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.server.yunchat.builder.model.UploadModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UploadDao extends BaseMapper<UploadModel> {}
