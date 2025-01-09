/*
 Navicat Premium Dump SQL

 Source Server         : local
 Source Server Type    : MySQL
 Source Server Version : 80012 (8.0.12)
 Source Host           : 127.0.0.1:3306
 Source Schema         : bread_shop

 Target Server Type    : MySQL
 Target Server Version : 80012 (8.0.12)
 File Encoding         : 65001

 Date: 09/01/2025 19:24:13
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for categories
-- ----------------------------
DROP TABLE IF EXISTS `categories`;
CREATE TABLE `categories`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `sort_order` int(11) NOT NULL DEFAULT 0,
  `status` tinyint(1) NOT NULL DEFAULT 1,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `icon` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name` ASC) USING BTREE,
  INDEX `idx_category_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of categories
-- ----------------------------
INSERT INTO `categories` VALUES (1, '面包', 1, 1, '2025-01-08 21:00:57', '2025-01-09 14:37:34', 'Food-icon');
INSERT INTO `categories` VALUES (2, '蛋糕', 2, 1, '2025-01-08 21:00:57', '2025-01-09 14:37:40', 'IceCream-icon');
INSERT INTO `categories` VALUES (3, '饮品', 3, 1, '2025-01-08 21:00:57', '2025-01-09 14:37:42', 'Coffee-icon');
INSERT INTO `categories` VALUES (4, '其他', 4, 1, '2025-01-09 14:27:33', '2025-01-09 14:37:43', 'More-icon');

-- ----------------------------
-- Table structure for delivery_addresses
-- ----------------------------
DROP TABLE IF EXISTS `delivery_addresses`;
CREATE TABLE `delivery_addresses`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '地址ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `receiver_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收货人电话',
  `province` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '省份',
  `city` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '城市',
  `district` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '区/县',
  `detail_address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '详细地址',
  `is_default` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否默认地址：0-否，1-是',
  `tag` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '地址标签：家、公司等',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '收货地址表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of delivery_addresses
-- ----------------------------
INSERT INTO `delivery_addresses` VALUES (1, 2, '张三', '13800138001', '广东省', '广州市', '天河区', '天河路100号', 1, '公司', '2025-01-09 13:00:00', '2025-01-09 13:00:00');
INSERT INTO `delivery_addresses` VALUES (2, 2, '张三', '13800138001', '广东省', '广州市', '番禺区', '市桥街道100号', 0, '家', '2025-01-09 13:00:00', '2025-01-09 13:00:00');
INSERT INTO `delivery_addresses` VALUES (3, 3, '李四', '13800138002', '广东省', '广州市', '海珠区', '江南大道50号', 1, '家', '2025-01-09 13:00:00', '2025-01-09 13:00:00');
INSERT INTO `delivery_addresses` VALUES (4, 4, '王五', '13800138003', '山西省', '晋城市', '阳城县', '中山路20号', 1, '公司', '2025-01-09 13:00:00', '2025-01-09 16:46:17');
INSERT INTO `delivery_addresses` VALUES (5, 4, '王五', '13800138003', '广东省', '广州市', '白云区', '机场路150号', 0, '家', '2025-01-09 13:00:00', '2025-01-09 16:46:04');

-- ----------------------------
-- Table structure for order_items
-- ----------------------------
DROP TABLE IF EXISTS `order_items`;
CREATE TABLE `order_items`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `order_id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  `product_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `product_image` varchar(200) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `quantity` int(11) NOT NULL,
  `price` decimal(10, 2) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_item_order`(`order_id` ASC) USING BTREE,
  INDEX `idx_order_item_product`(`product_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of order_items
-- ----------------------------
INSERT INTO `order_items` VALUES (1, 1, 1, '法式面包', NULL, 2, 15.00, '2025-01-09 13:46:46', '2025-01-09 13:46:46');
INSERT INTO `order_items` VALUES (2, 1, 2, '全麦面包', NULL, 1, 12.00, '2025-01-09 13:46:46', '2025-01-09 13:46:46');
INSERT INTO `order_items` VALUES (3, 2, 3, '草莓蛋糕', NULL, 1, 68.00, '2025-01-09 14:00:00', '2025-01-09 14:00:00');
INSERT INTO `order_items` VALUES (4, 3, 4, '美式咖啡', NULL, 2, 18.00, '2025-01-09 14:30:00', '2025-01-09 14:30:00');
INSERT INTO `order_items` VALUES (5, 4, 1, '法式面包', NULL, 3, 15.00, '2025-01-09 15:00:00', '2025-01-09 15:00:00');
INSERT INTO `order_items` VALUES (6, 4, 3, '草莓蛋糕', NULL, 1, 68.00, '2025-01-09 15:00:00', '2025-01-09 15:00:00');
INSERT INTO `order_items` VALUES (7, 8, 1, '法式面包', NULL, 2, 15.00, '2025-01-09 15:41:19', '2025-01-09 15:41:19');
INSERT INTO `order_items` VALUES (8, 9, 2, '全麦面包', NULL, 1, 12.00, '2025-01-09 16:59:46', '2025-01-09 16:59:46');
INSERT INTO `order_items` VALUES (9, 10, 1, '法式面包', NULL, 2, 15.00, '2025-01-09 18:41:08', '2025-01-09 18:41:08');
INSERT INTO `order_items` VALUES (10, 11, 2, '全麦面包', NULL, 1, 12.00, '2025-01-09 18:41:41', '2025-01-09 18:41:41');
INSERT INTO `order_items` VALUES (11, 12, 2, '全麦面包', NULL, 1, 12.00, '2025-01-09 18:42:06', '2025-01-09 18:42:06');
INSERT INTO `order_items` VALUES (12, 13, 2, '全麦面包', NULL, 1, 12.00, '2025-01-09 18:43:03', '2025-01-09 18:43:03');

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `total_amount` decimal(10, 2) NOT NULL,
  `status` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL DEFAULT 'pending',
  `remark` text CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL,
  `address` text CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `phone` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_order_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (1, 2, 42.00, 'completed', '请尽快送达', '广州市天河区天河路100号', '13800138001', '2025-01-09 13:46:46', '2025-01-09 14:00:00');
INSERT INTO `orders` VALUES (2, 3, 68.00, 'completed', '生日蛋糕，请准时送达', '广州市海珠区江南大道50号', '13800138002', '2025-01-09 14:00:00', '2025-01-09 18:20:43');
INSERT INTO `orders` VALUES (3, 4, 36.00, 'cancelled', NULL, '广州市越秀区中山路20号', '13800138003', '2025-01-09 14:30:00', '2025-01-09 15:00:00');
INSERT INTO `orders` VALUES (4, 2, 113.00, 'pending', '面包要新鲜的', '广州市番禺区市桥街道100号', '13800138001', '2025-01-09 15:00:00', '2025-01-09 15:00:00');
INSERT INTO `orders` VALUES (8, 4, 30.00, 'completed', '', '1', '1', '2025-01-09 15:41:19', '2025-01-09 18:20:45');
INSERT INTO `orders` VALUES (9, 4, 12.00, 'completed', '', '山西省晋城市阳城县中山路20号', '13800138003', '2025-01-09 16:59:46', '2025-01-09 18:20:46');
INSERT INTO `orders` VALUES (10, 4, 30.00, 'completed', '', '山西省晋城市阳城县中山路20号', '13800138003', '2025-01-09 18:41:08', '2025-01-09 19:21:56');
INSERT INTO `orders` VALUES (11, 4, 12.00, 'completed', '', '山西省晋城市阳城县中山路20号', '13800138003', '2025-01-09 18:41:41', '2025-01-09 19:21:54');
INSERT INTO `orders` VALUES (12, 4, 12.00, 'completed', '', '山西省晋城市阳城县中山路20号', '13800138003', '2025-01-09 18:42:06', '2025-01-09 19:21:53');
INSERT INTO `orders` VALUES (13, 4, 12.00, 'completed', '', '山西省晋城市阳城县中山路20号', '13800138003', '2025-01-09 18:43:03', '2025-01-09 19:21:52');

-- ----------------------------
-- Table structure for products
-- ----------------------------
DROP TABLE IF EXISTS `products`;
CREATE TABLE `products`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `category_id` int(11) NOT NULL,
  `name` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `description` text CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL,
  `price` decimal(10, 2) NOT NULL,
  `image_url` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `stock` int(11) NOT NULL DEFAULT 0,
  `status` tinyint(1) NOT NULL DEFAULT 1,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_product_status`(`status` ASC) USING BTREE,
  INDEX `idx_product_category`(`category_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of products
-- ----------------------------
INSERT INTO `products` VALUES (1, 1, '法式面包', '新鲜出炉的法式面包', 15.00, '/uploads/d176a810-a24b-4a08-859a-a94fc6e308bd.jpg', 96, 1, '2025-01-08 21:00:57', '2025-01-09 19:21:26');
INSERT INTO `products` VALUES (2, 1, '全麦面包', '健康全麦面包', 12.00, '/uploads/c545bbff-73fe-4772-b123-1f35f3fafbb3.jpg', 96, 1, '2025-01-08 21:00:57', '2025-01-09 19:10:15');
INSERT INTO `products` VALUES (3, 2, '草莓蛋糕', '新鲜草莓制作', 68.00, '/uploads/d478c689-6a0d-4f8b-90ba-0c5289b5965e.jpg', 50, 1, '2025-01-08 21:00:57', '2025-01-09 19:18:15');
INSERT INTO `products` VALUES (4, 3, '美式咖啡', '醇香美式咖啡', 18.00, '/uploads/de285034-63e4-468c-a6fb-1389b4ef594d.jpg', 200, 1, '2025-01-08 21:00:57', '2025-01-09 19:17:46');
INSERT INTO `products` VALUES (5, 1, 'test', 'test', 1.10, '/uploads/2eac01f1-856a-442e-9fbf-f304a63f256f.jpg', 1, 1, '2025-01-09 19:21:46', '2025-01-09 19:21:46');

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `password` varchar(100) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL,
  `phone` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `role` varchar(20) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL DEFAULT 'customer',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `avatar` varchar(200) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL,
  `status` tinyint(1) NULL DEFAULT 1,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, 'admin', 'e10adc3949ba59abbe56e057f20f883e', '13800138000', 'admin', '2025-01-08 21:00:57', '2025-01-09 19:17:21', '/uploads/e140a2f4-e40e-406f-80df-fef0eb88087a.jpg', 1);
INSERT INTO `users` VALUES (2, 'test', 'e10adc3949ba59abbe56e057f20f883e', '13800138001', 'customer', '2025-01-09 11:22:21', '2025-01-09 12:33:36', '/uploads/default-avatar.png', 1);
INSERT INTO `users` VALUES (3, 'test1', 'e10adc3949ba59abbe56e057f20f883e', '13800138002', 'customer', '2025-01-09 11:23:17', '2025-01-09 12:33:36', '/uploads/default-avatar.png', 1);
INSERT INTO `users` VALUES (4, 'test2', 'e10adc3949ba59abbe56e057f20f883e', '13800138003', 'customer', '2025-01-09 11:24:28', '2025-01-09 18:20:52', '/uploads/c563dd85-5fca-4e50-a509-211c0236050b.jpeg', 1);

SET FOREIGN_KEY_CHECKS = 1;
