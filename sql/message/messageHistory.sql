CREATE TABLE `im_message_history` (
  `app_id` INT NOT NULL COMMENT '应用ID',
  `from_id` VARCHAR(64) NOT NULL COMMENT '发送方ID',
  `to_id` VARCHAR(64) NOT NULL COMMENT '接收方ID/群ID',
  `owner_id` VARCHAR(64) NOT NULL COMMENT '消息归属者ID（用户自己的历史记录）',
  `message_key` BIGINT NOT NULL COMMENT '消息体ID，关联im_message_body表',
  `sequence` BIGINT NOT NULL COMMENT '消息序列号（递增）',
  `message_random` VARCHAR(32) DEFAULT NULL COMMENT '随机码',
  `message_time` BIGINT NOT NULL COMMENT '消息发送时间戳',
  `create_time` BIGINT DEFAULT NULL COMMENT '创建时间戳',

  PRIMARY KEY (`app_id`,`owner_id`,`sequence`) USING BTREE,
  KEY `idx_message_key` (`message_key`) USING BTREE,
  KEY `idx_from_to` (`app_id`,`from_id`,`to_id`) USING BTREE

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息历史记录表';