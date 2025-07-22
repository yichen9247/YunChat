package com.server.yunchat.builder.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.server.yunchat.builder.model.GroupModel;
import io.lettuce.core.dynamic.annotation.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GroupDao extends BaseMapper<GroupModel> {
    GroupModel getGroupInfo(@Param("gid") Integer gid);
    List<GroupModel> getGroupList(@Param("uid") Long uid);
    Boolean getIsUserInGroup(@Param("gid") Integer gid, @Param("uid") Long uid);
    List<GroupModel> getAllGroupList(@Param("page") Integer page, @Param("limit") Integer limit);
    List<GroupModel> searchGroupList(@Param("name") String name);
}
