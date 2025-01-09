-- 添加用户状态字段
ALTER TABLE users ADD COLUMN status BOOLEAN DEFAULT TRUE;

-- 更新现有用户的状态为启用
UPDATE users SET status = TRUE WHERE status IS NULL; 