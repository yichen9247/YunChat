package com.server.yunchat.scripts;

import com.server.yunchat.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

@Service
public class MysqlChecker {

    private final Long ADMIN_UID;
    private final Long ROBOT_UID;
    private final String ADMIN_AVATAR;
    private final String ROBOT_AVATAR;

    @Autowired
    public MysqlChecker(UserServiceImpl userServiceImpl) {
        ADMIN_UID = userServiceImpl.getRegisterUid();
        ROBOT_UID = userServiceImpl.getRegisterUid();
        ADMIN_AVATAR = userServiceImpl.getRandomAvatar();
        ROBOT_AVATAR = userServiceImpl.getRandomAvatar();
    }

    private final String USER_TABLE_NAME = "yun_user";
    private final String GROUP_TABLE_NAME = "yun_group";
    private final String LOGIN_TABLE_NAME = "yun_login";
    private final String NOTICE_TABLE_NAME = "yun_notice";
    private final String REPORT_TABLE_NAME = "yun_report";
    private final String SYSTEM_TABLE_NAME = "yun_system";
    private final String UPLOAD_TABLE_NAME = "yun_upload";
    private final String MEMBER_TABLE_NAME = "yun_member";
    private final String MESSAGE_TABLE_NAME = "yun_message";

    // 创建用户表
    private final String CREATE_USER_TABLE_SQL = """
                CREATE TABLE IF NOT EXISTS %s (
                    `uid` bigint(20) NOT NULL,
                    `nick` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '热心网友',
                    `qq_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL UNIQUE,
                    `status` int(1) NOT NULL DEFAULT '0',
                    `avatar` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                    `ai_auth` int(1) NOT NULL DEFAULT '0',
                    `reg_time` datetime DEFAULT CURRENT_TIMESTAMP,
                    `username` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL UNIQUE,
                    `password` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
                    `permission` int(1) NOT NULL DEFAULT '0',
                    PRIMARY KEY (uid)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
            """.formatted(USER_TABLE_NAME);

    // 创建群聊表
    private final String CREATE_GROUP_TABLE_SQL = """
                CREATE TABLE IF NOT EXISTS %s (
                    `gid` bigint(20) NOT NULL AUTO_INCREMENT,
                    `status` int(1) NOT NULL DEFAULT '0',
                    `time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    `name` varchar(6) COLLATE utf8mb4_unicode_ci NOT NULL,
                    `notice` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT '当前群聊暂无公告',
                    `avatar` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0/default/avatar.png',
                    PRIMARY KEY (gid)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
            """.formatted(GROUP_TABLE_NAME);

    // 创建成员表
    private final String CREATE_MEMBER_TABLE_SQL = """
                CREATE TABLE IF NOT EXISTS %s (
                    `id` int(11) NOT NULL AUTO_INCREMENT,
                    `gid` bigint(20) NOT NULL,
                    `uid` bigint(20) NOT NULL,
                    `time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    PRIMARY KEY (id),
                    INDEX `idx_uid` (`uid`),
                    INDEX `idx_gid` (`gid`),
                    UNIQUE INDEX `unique_member` (`gid`, `uid`),
                    FOREIGN KEY (uid) REFERENCES %s(uid) ON DELETE CASCADE ON UPDATE CASCADE,
                    FOREIGN KEY (gid) REFERENCES %s(gid) ON DELETE CASCADE ON UPDATE CASCADE
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
            """.formatted(MEMBER_TABLE_NAME, USER_TABLE_NAME, GROUP_TABLE_NAME);

    // 创建消息表
    private final String CREATE_MESSAGE_TABLE_SQL = """
                CREATE TABLE IF NOT EXISTS %s (
                    `obj` int(1) NOT NULL,
                    `tar` bigint(20) NOT NULL,
                    `uid` bigint(20) NOT NULL,
                    `sid` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
                    `type` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
                    `time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    `deleted` int(1) DEFAULT '0',
                    `content` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL,
                    PRIMARY KEY (sid),
                    INDEX `idx_uid` (`uid`),
                    FOREIGN KEY (uid) REFERENCES %s(uid) ON DELETE CASCADE ON UPDATE CASCADE
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
            """.formatted(MESSAGE_TABLE_NAME, USER_TABLE_NAME);

    // 创建配置表
    private final String CREATE_SYSTEM_TABLE_SQL = """
                CREATE TABLE IF NOT EXISTS %s (
                    `id` int(11) NOT NULL AUTO_INCREMENT,
                    `name` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL UNIQUE,
                    `value` text COLLATE utf8mb4_unicode_ci,
                    `time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    PRIMARY KEY (id)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
            """.formatted(SYSTEM_TABLE_NAME);

    // 创建上传表
    private final String CREATE_UPLOAD_TABLE_SQL = """
                CREATE TABLE IF NOT EXISTS %s (
                    `fid` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
                    `uid` bigint(20) NOT NULL,
                    `size` bigint(20) NOT NULL,
                    `time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    `name` text COLLATE utf8mb4_unicode_ci NOT NULL,
                    `path` text COLLATE utf8mb4_unicode_ci NOT NULL,
                    `type` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
                    PRIMARY KEY (fid)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
            """.formatted(UPLOAD_TABLE_NAME);

    // 创建举报表
    private final String CREATE_REPORT_TABLE_SQL = """
                CREATE TABLE IF NOT EXISTS %s (
                    `rid` int(11) NOT NULL AUTO_INCREMENT,
                    `sid` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
                    `time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    `reason` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
                    `deleted` int(1) DEFAULT '0',
                    `reporter_id` bigint(20) NOT NULL,
                    `reported_id` bigint(20) NOT NULL,
                    PRIMARY KEY (rid),
                    INDEX `idx_reporter` (`reporter_id`),
                    INDEX `idx_reported` (`reported_id`),
                    FOREIGN KEY (reporter_id) REFERENCES %s(uid) ON DELETE CASCADE ON UPDATE CASCADE,
                    FOREIGN KEY (reported_id) REFERENCES %s(uid) ON DELETE CASCADE ON UPDATE CASCADE
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
            """.formatted(REPORT_TABLE_NAME, USER_TABLE_NAME, USER_TABLE_NAME);

    // 创建登录表
    private final String CREATE_LOGIN_TABLE_SQL = """
                CREATE TABLE IF NOT EXISTS %s (
                    `id` int(11) NOT NULL AUTO_INCREMENT,
                    `uid` bigint(20) NOT NULL,
                    `time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    `address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                    `location` varchar(15) COLLATE utf8mb4_unicode_ci NOT NULL,
                    `platform` varchar(15) COLLATE utf8mb4_unicode_ci NOT NULL,
                    `username` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
                    PRIMARY KEY (id)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
            """.formatted(LOGIN_TABLE_NAME);

    // 创建公告表
    private final String CREATE_NOTICE_TABLE_SQL = """
                CREATE TABLE IF NOT EXISTS %s (
                    `id` int(11) NOT NULL AUTO_INCREMENT,
                    `time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    `title` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
                    `content` text CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
                    PRIMARY KEY (id)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
            """.formatted(NOTICE_TABLE_NAME);

    public void checkTable(String databaseDriver, String databaseUrl, String databaseUsername, String databasePassword) throws ClassNotFoundException, SQLException {
        Class.forName(databaseDriver);
        Connection connection = DriverManager.getConnection(databaseUrl, databaseUsername, databasePassword);
        DatabaseMetaData dbMetaData = connection.getMetaData();
        if (!dbMetaData.getTables(null, null, USER_TABLE_NAME, null).next()) createUserTable(connection);
        if (!dbMetaData.getTables(null, null, GROUP_TABLE_NAME, null).next()) createGroupTable(connection);
        if (!dbMetaData.getTables(null, null, MEMBER_TABLE_NAME, null).next()) createMemberTable(connection);
        if (!dbMetaData.getTables(null, null, LOGIN_TABLE_NAME, null).next()) createLoginTable(connection);
        if (!dbMetaData.getTables(null, null, NOTICE_TABLE_NAME, null).next()) createNoticeTable(connection);
        if (!dbMetaData.getTables(null, null, SYSTEM_TABLE_NAME, null).next()) createSystemTable(connection);
        if (!dbMetaData.getTables(null, null, UPLOAD_TABLE_NAME, null).next()) createUploadTable(connection);
        if (!dbMetaData.getTables(null, null, REPORT_TABLE_NAME, null).next()) createReportTable(connection);
        if (!dbMetaData.getTables(null, null, MESSAGE_TABLE_NAME, null).next()) createMessageTable(connection);
    }

    private void createUserTable(Connection connection) throws SQLException {
        connection.createStatement().execute(CREATE_USER_TABLE_SQL);
        connection.createStatement().execute("""
                INSERT INTO %s (`uid`, `nick`, `qq_id`, `status`, `avatar`, `ai_auth`, `reg_time`, `username`, `password`, `permission`) VALUES
                    (%d, '管理员账号', NULL, 0, '%s', 1, CURRENT_TIMESTAMP, 'yunchat', '378ca9bac7fef8e76bdc2a7020805a63', 1),
                    (%d, '机器人账号', NULL, 0, '%s', 0, CURRENT_TIMESTAMP, 'robot123', 'c51ce410c124a10e0db5e4b97fc2af39', 2);
            """.formatted(USER_TABLE_NAME, ADMIN_UID, ADMIN_AVATAR, ROBOT_UID, ROBOT_AVATAR));
    }

    private void createGroupTable(Connection connection) throws SQLException {
        connection.createStatement().execute(CREATE_GROUP_TABLE_SQL);
        connection.createStatement().execute("""
                INSERT INTO %s (`name`) VALUES ('项目全员群'), ('项目测试群');
            """.formatted(GROUP_TABLE_NAME));
    }

    private void createMemberTable(Connection connection) throws SQLException {
        connection.createStatement().execute(CREATE_MEMBER_TABLE_SQL);
        connection.createStatement().execute("""
                INSERT INTO %s (`gid`, `uid`) VALUES (1, %d), (1, %d), (2, %d);
            """.formatted(MEMBER_TABLE_NAME, ADMIN_UID, ROBOT_UID, ADMIN_UID));
    }

    private void createNoticeTable(Connection connection) throws SQLException {
        connection.createStatement().execute(CREATE_NOTICE_TABLE_SQL);
    }

    private void createLoginTable(Connection connection) throws SQLException {
        connection.createStatement().execute(CREATE_LOGIN_TABLE_SQL);
    }

    private void createSystemTable(Connection connection) throws SQLException {
        connection.createStatement().execute(CREATE_SYSTEM_TABLE_SQL);
        connection.createStatement().execute("""
                INSERT INTO %s (`name`, `value`) VALUES
                    ('taboo', 'close'),
                    ('upload', 'open'),
                    ('register', 'open'),
                    ('version', '2.1.2'),
                    ('welcome', 'open'),
                    ('download', '无'),
                    ('ai_url', 'https://api.ppinfra.com/v3/openai/chat/completions'),
                    ('ai_role', '作为YunChat实时对话AI，你需：理解用户核心需求'),
                    ('ai_model', 'deepseek/deepseek-r1/community'),
                    ('ai_token', '无');
            """.formatted(SYSTEM_TABLE_NAME));
    }

    private void createUploadTable(Connection connection) throws SQLException {
        connection.createStatement().execute(CREATE_UPLOAD_TABLE_SQL);
    }

    private void createReportTable(Connection connection) throws SQLException {
        connection.createStatement().execute(CREATE_REPORT_TABLE_SQL);
    }

    private void createMessageTable(Connection connection) throws SQLException {
        connection.createStatement().execute(CREATE_MESSAGE_TABLE_SQL);
    }
}