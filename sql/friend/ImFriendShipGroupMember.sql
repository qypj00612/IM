CREATE TABLE `im_friendship_group_member` (
    `group_id` bigint NOT NULL COMMENT '分组ID',
    `to_id` varchar(50) NOT NULL COMMENT '群成员/好友ID',
    -- 主键设计：设置为联合主键
    PRIMARY KEY (`group_id`, `to_id`)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='好友分组成员表';