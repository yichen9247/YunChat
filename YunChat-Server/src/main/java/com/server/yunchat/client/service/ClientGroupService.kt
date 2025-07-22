package com.server.yunchat.client.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.server.yunchat.builder.dao.GroupDao
import com.server.yunchat.builder.dao.MemberDao
import com.server.yunchat.builder.data.ResultModel
import com.server.yunchat.builder.model.MemberModel
import com.server.yunchat.builder.utils.HandUtils
import com.server.yunchat.plugin.TaskPlugin
import com.server.yunchat.service.impl.GroupServiceImpl
import com.server.yunchat.service.impl.UserServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DuplicateKeyException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class ClientGroupService @Autowired constructor(
    private val groupDao: GroupDao,
    private val memberDao: MemberDao,
    private val taskPlugin: TaskPlugin,
    private val userServiceImpl: UserServiceImpl,
    private val groupServiceImpl: GroupServiceImpl
) {
    fun getGroupList(uid: Long): ResultModel {
        return try {
            val groupList = groupDao.getGroupList(uid)
            if (groupList.isEmpty() && uid == 0.toLong()) {
                groupList.add(groupServiceImpl.getGroupInfo(1)) // 返回默认群聊
            }
            HandUtils.handleResultByCode(HttpStatus.OK, groupList, "获取成功")
        } catch (e: Exception) {
            HandUtils.handleResultByCode(HttpStatus.BAD_REQUEST, null, e.message.toString())
        }
    }

    fun addGroupMember(gid: Int, uid: Long): ResultModel {
        return try {
            val memberModel = MemberModel()
            val result = groupServiceImpl.addGroupMember(gid, uid, memberModel)
            if (memberDao.insert(result) > 0) {
                HandUtils.handleResultByCode(HttpStatus.OK, result, "加入群聊成功")
            } else HandUtils.handleResultByCode(HttpStatus.BAD_REQUEST, null, "加入群聊失败")
        } catch (e: DuplicateKeyException) {
            HandUtils.handleResultByCode(HttpStatus.BAD_REQUEST, null, "你已在此群聊中")
        } catch (e: Exception) {
            HandUtils.handleResultByCode(HttpStatus.BAD_REQUEST, null, e.message.toString())
        }
    }

    fun deleteGroupMember(gid: Int, uid: Long): ResultModel {
        if (gid == 1)
            return HandUtils.handleResultByCode(
                HttpStatus.NOT_ACCEPTABLE, null, "默认群聊不可退出"
            )
        if (userServiceImpl.getUserIsAdmin(uid))
            return HandUtils.handleResultByCode(
                HttpStatus.NOT_ACCEPTABLE, null, "管理账号不可退出"
            )
        return try {
            val memberModel = MemberModel()
            memberModel.gid = gid
            memberModel.uid = uid
            return memberDao.selectOne(
                QueryWrapper<MemberModel>().eq("uid", uid).eq("gid", gid)
            ).let {
                if (memberDao.deleteById(it) > 0) {
                    taskPlugin.doDeleteRedisCache() // 清除缓存
                    HandUtils.handleResultByCode(HttpStatus.OK, null, "退出群聊成功")
                } else HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, "退出群聊失败")
            }
        } catch (e: Exception) {
            HandUtils.handleResultByCode(HttpStatus.INTERNAL_SERVER_ERROR, null, e.message.toString())
        }
    }

    fun searchGroupList(name: String): ResultModel {
        return try {
            val groupList = groupDao.searchGroupList(name)
            HandUtils.handleResultByCode(HttpStatus.OK, groupList, "搜索成功")
        } catch (e: Exception) {
            HandUtils.handleResultByCode(HttpStatus.BAD_REQUEST, null, e.message.toString())
        }
    }

    fun getIsUserInGroup(gid: Int, uid: Long): Boolean {
        return groupDao.getIsUserInGroup(gid, uid)
    }
}