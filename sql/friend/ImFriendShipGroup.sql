CREATE TABLE `im_friendship_group` (
    `group_id` bigint NOT NULL COMMENT '分组ID',
    `from_id` varchar(50) NOT NULL COMMENT '创建人ID',
    `app_id` int NOT NULL COMMENT '应用ID',
    `group_name` varchar(255) DEFAULT NULL COMMENT '分组名称',
    `sequence` bigint DEFAULT NULL COMMENT '序列号/排序',
    `create_time` bigint NOT NULL COMMENT '创建时间戳',
    `update_time` bigint NOT NULL COMMENT '更新时间戳',
    `del_flag` int DEFAULT NULL COMMENT '删除标志 (0正常 1删除)',
    -- 主键设计：group_id 单主键
    PRIMARY KEY (`group_id`)

    UNIQUE KEY `uk_app_from_group` (`app_id`,`from_id`,`group_name`)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='好友分组表';


CREATE TABLE `im_friendship_group` (
    `group_id` bigint NOT NULL COMMENT '分组ID',
    `from_id` varchar(50) NOT NULL COMMENT '创建人ID',
    `app_id` int NOT NULL COMMENT '应用ID',
    `group_name` varchar(255) DEFAULT NULL COMMENT '分组名称',
    `sequence` bigint DEFAULT NULL COMMENT '序列号/排序',
    `create_time` bigint NOT NULL COMMENT '创建时间戳',
    `update_time` bigint NOT NULL COMMENT '更新时间戳',
    `del_flag` int DEFAULT NULL COMMENT '删除标志 (0正常 1删除)',

    PRIMARY KEY (`group_id`),

    UNIQUE KEY `uk_app_from_group` (`app_id`,`from_id`,`group_name`)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='好友分组表';