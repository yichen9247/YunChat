package com.server.yunchat.service.impl

import com.server.yunchat.builder.dao.GroupDao
import com.server.yunchat.builder.model.GroupModel
import com.server.yunchat.builder.model.MemberModel
import com.server.yunchat.builder.types.RedisType.GROUP_INFO_KEY
import com.server.yunchat.plugin.SpringPlugin
import com.server.yunchat.service.GroupService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

/**
 * @name 群聊服务实现类
 * @author yichen9247
 */
@Service
class GroupServiceImpl @Autowired constructor(
    private val groupDao: GroupDao,
    private val springPlugin: SpringPlugin,
    @Qualifier("groupInfoRedisTemplate")
    private val groupInfoRedisTemplate: RedisTemplate<String, GroupModel>
): GroupService {
    /**
     * @name 获取群聊信息
     * @param gid 群聊id
     */
    override fun getGroupInfo(gid: Int): GroupModel? {
        val redisCache = groupInfoRedisTemplate.opsForHash<String, GroupModel>()
            .get(GROUP_INFO_KEY, gid.toString())
        return redisCache ?: queryGroupInfo(gid)?.also { currentGroup ->
            groupInfoRedisTemplate.opsForHash<String, GroupModel>()
                .put(GROUP_INFO_KEY, gid.toString(), currentGroup)
        }
    }

    /**
     * @name 添加群聊成员
     * @param gid 群聊编号
     * @param uid 用户编号
     * @param memberModel 数据模型
     */
    override fun addGroupMember(
        gid: Int,
        uid: Long,
        memberModel: MemberModel
    ): MemberModel {
        memberModel.gid = gid
        memberModel.uid = uid
        return memberModel
    }

    /**
     * @name 修改群聊状态
     * @param gid 群聊编号
     * @param status 群聊状态
     * @param groupModel 数据模型
     */
    override fun setGroupStatus(
        gid: Int,
        status: Int,
        groupModel: GroupModel
    ): GroupModel {
        groupModel.gid = gid
        groupModel.status = status
        return groupModel
    }

    /**
     * @name 添加群聊信息
     * @param name 群聊名称
     * @param avatar 群聊头像
     * @param notice 群聊公告
     * @param groupModel 数据模型
     */
    override fun addGroupInfo(name: String, avatar: String, notice: String, groupModel: GroupModel): GroupModel {
        groupModel.name = name
        groupModel.avatar = avatar
        groupModel.notice = notice
        springPlugin.validField(groupModel)
        return groupModel
    }

    /**
     * @name 修改群聊信息
     * @param gid 群聊编号
     * @param name 群聊名称
     * @param avatar 群聊头像
     * @param notice 群聊公告
     * @param groupModel 数据模型
     */
    override fun setGroupInfo(
        gid: Int,
        name: String,
        avatar: String,
        notice: String,
        groupModel: GroupModel
    ): GroupModel {
        groupModel.gid = gid
        groupModel.name = name
        groupModel.avatar = avatar
        groupModel.notice = notice
        springPlugin.validField(groupModel)
        return groupModel
    }

    /**
     * @name 查询群聊信息
     * @param gid 群聊id
     */
    private fun queryGroupInfo(gid: Int): GroupModel? {
        return try {
            groupDao.getGroupInfo(gid)
        } catch (e: Exception) {
            null
        }
    }
}