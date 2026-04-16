-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS springboot_demo DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE springboot_demo;

-- 创建 user 表
CREATE TABLE IF NOT EXISTS `user` (
  `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键 ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码',
  `role` INT DEFAULT 0 COMMENT '角色 0:普通用户 1:管理员',
  `phone` VARCHAR(20) COMMENT '手机号',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除 0:未删除 1:已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
