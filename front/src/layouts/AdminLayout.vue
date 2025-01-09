<template>
    <el-container class="admin-layout">
        <el-aside width="200px">
            <el-menu router>
                <el-menu-item index="/admin/products">
                    <el-icon><Goods /></el-icon>
                    <span>商品管理</span>
                </el-menu-item>
                <el-menu-item index="/admin/orders">
                    <el-icon><List /></el-icon>
                    <span>订单管理</span>
                </el-menu-item>
                <el-menu-item index="/admin/categories">
                    <el-icon><Menu /></el-icon>
                    <span>分类管理</span>
                </el-menu-item>
                <el-menu-item index="/admin/users">
                    <el-icon><User /></el-icon>
                    <span>用户管理</span>
                </el-menu-item>
            </el-menu>
        </el-aside>
        <el-container>
            <el-header>面包商店后台管理</el-header>
            <el-main>
                <router-view />
            </el-main>
        </el-container>
    </el-container>
</template>

<script>
import { Goods, List, Menu, User } from '@element-plus/icons-vue'

export default {
    name: 'AdminLayout',
    components: {
        Goods,
        List,
        Menu,
        User
    }
}
</script>

<style scoped>
.admin-layout {
    height: 100vh;
}
.el-header {
    background-color: #fff;
    border-bottom: 1px solid #dcdfe6;
    line-height: 60px;
    text-align: center;
}
</style> 