CREATE TABLE `im_message_body` (
   `app_id` INT NOT NULL COMMENT '应用ID',
   `message_key` BIGINT NOT NULL COMMENT '消息唯一ID(messageBodyId)',
   `message_body` TEXT DEFAULT NULL COMMENT '消息内容',
   `security_key` VARCHAR(255) DEFAULT NULL COMMENT '安全密钥',
   `message_time` BIGINT DEFAULT NULL COMMENT '消息发送时间戳',
   `create_time` BIGINT DEFAULT NULL COMMENT '创建时间戳',
   `extra` VARCHAR(1024) DEFAULT NULL COMMENT '扩展字段',
   `del_flag` TINYINT DEFAULT 0 COMMENT '删除标识 0正常 1删除',

   PRIMARY KEY (`message_key`) USING BTREE,
   KEY `idx_app_id` (`app_id`) USING BTREE

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息内容表';