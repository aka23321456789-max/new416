-- ============================================
-- 新闻系统数据库修复脚本
-- 数据库名: newsdb
-- ============================================

USE newsdb;

-- ============================================
-- 1. 检查并修复 news 表
-- ============================================

-- 添加 view_count 字段（如果不存在）
SET @exist := (SELECT COUNT(*) FROM information_schema.columns 
               WHERE table_name = 'news' AND column_name = 'view_count' AND table_schema = 'newsdb');
SET @sql := IF(@exist = 0, 
               'ALTER TABLE news ADD COLUMN view_count INT DEFAULT 0 COMMENT "阅读次数" AFTER category', 
               'SELECT "view_count 字段已存在"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 create_time 字段（如果不存在）
SET @exist := (SELECT COUNT(*) FROM information_schema.columns 
               WHERE table_name = 'news' AND column_name = 'create_time' AND table_schema = 'newsdb');
SET @sql := IF(@exist = 0, 
               'ALTER TABLE news ADD COLUMN create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT "创建时间" AFTER view_count', 
               'SELECT "create_time 字段已存在"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 update_time 字段（如果不存在）
SET @exist := (SELECT COUNT(*) FROM information_schema.columns 
               WHERE table_name = 'news' AND column_name = 'update_time' AND table_schema = 'newsdb');
SET @sql := IF(@exist = 0, 
               'ALTER TABLE news ADD COLUMN update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT "更新时间" AFTER create_time', 
               'SELECT "update_time 字段已存在"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 初始化现有新闻的阅读量为0
UPDATE news SET view_count = 0 WHERE view_count IS NULL;

-- ============================================
-- 2. 删除旧的 like 表，创建新的 user_like 表
-- ============================================

-- 删除旧的 like 表（如果存在）
DROP TABLE IF EXISTS `like`;

-- 创建新的 user_like 表
CREATE TABLE IF NOT EXISTS `user_like` (
  `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键 ID',
  `user_id` INT NOT NULL COMMENT '用户ID',
  `news_id` INT NOT NULL COMMENT '新闻ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY `uk_user_news` (`user_id`, `news_id`) COMMENT '用户只能点赞一次'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞表';

-- ============================================
-- 3. 检查并修复 favorite 表
-- ============================================

-- 删除旧的 favorite 表（如果结构不对）
-- 先检查是否有 content 字段（旧结构有，新结构没有）
SET @exist := (SELECT COUNT(*) FROM information_schema.columns 
               WHERE table_name = 'favorite' AND column_name = 'content' AND table_schema = 'newsdb');

-- 如果有 content 字段，说明是旧表，删除重建
SET @sql := IF(@exist > 0, 
               'DROP TABLE favorite', 
               'SELECT "favorite 表结构正常"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 创建 favorite 表（如果不存在）
CREATE TABLE IF NOT EXISTS `favorite` (
  `id` INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键 ID',
  `user_id` INT NOT NULL COMMENT '用户ID',
  `news_id` INT NOT NULL COMMENT '新闻ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY `uk_user_news` (`user_id`, `news_id`) COMMENT '用户只能收藏一次'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- ============================================
-- 4. 检查并修复 comment 表
-- ============================================

-- 添加 parent_id 字段（如果不存在）
SET @exist := (SELECT COUNT(*) FROM information_schema.columns 
               WHERE table_name = 'comment' AND column_name = 'parent_id' AND table_schema = 'newsdb');
SET @sql := IF(@exist = 0, 
               'ALTER TABLE comment ADD COLUMN parent_id INT DEFAULT NULL COMMENT "父评论ID（回复功能）" AFTER content', 
               'SELECT "parent_id 字段已存在"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加 create_time 字段（如果不存在）
SET @exist := (SELECT COUNT(*) FROM information_schema.columns 
               WHERE table_name = 'comment' AND column_name = 'create_time' AND table_schema = 'newsdb');
SET @sql := IF(@exist = 0, 
               'ALTER TABLE comment ADD COLUMN create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT "创建时间" AFTER parent_id', 
               'SELECT "create_time 字段已存在"');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加索引（如果不存在）
CREATE INDEX IF NOT EXISTS idx_news_id ON comment(news_id);

-- ============================================
-- 完成！
-- ============================================
SELECT '数据库修复完成！' AS message;
