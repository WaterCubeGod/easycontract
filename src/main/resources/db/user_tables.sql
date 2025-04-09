-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 角色表
CREATE TABLE IF NOT EXISTS `role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '角色名称',
  `code` varchar(50) NOT NULL COMMENT '角色编码',
  `description` varchar(200) DEFAULT NULL COMMENT '角色描述',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS `user_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 初始化角色数据
INSERT INTO `role` (`name`, `code`, `description`) VALUES
('管理员', 'ROLE_ADMIN', '系统管理员，拥有所有权限'),
('普通用户', 'ROLE_USER', '普通用户，可以访问基本功能');

-- 初始化管理员用户（密码为123456的BCrypt加密）
INSERT INTO `user` (`username`, `password`, `email`, `status`) VALUES
('admin', '$2a$10$rDxPr5lKLuEGBCBpKgVb9.ib4U/MuEa1JR5bjY5Z4BbWRVpVVIxNi', 'admin@example.com', 1);

-- 初始化普通用户（密码为123456的BCrypt加密）
INSERT INTO `user` (`username`, `password`, `email`, `status`) VALUES
('user', '$2a$10$rDxPr5lKLuEGBCBpKgVb9.ib4U/MuEa1JR5bjY5Z4BbWRVpVVIxNi', 'user@example.com', 1);

-- 关联用户和角色
INSERT INTO `user_role` (`user_id`, `role_id`) VALUES
(1, 1), -- admin用户关联管理员角色
(2, 2); -- user用户关联普通用户角色
