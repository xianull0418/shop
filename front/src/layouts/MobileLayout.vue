<template>
    <div class="mobile-layout" :class="{ 'no-tabbar': !showTabbar }">
        <router-view />
        <van-tabbar v-model="active" v-if="showTabbar">
            <van-tabbar-item name="home" icon="home-o" @click="goTo('/mobile')">首页</van-tabbar-item>
            <van-tabbar-item name="menu" icon="apps-o" @click="goTo('/mobile/menu')">点单</van-tabbar-item>
            <van-tabbar-item name="order" icon="orders-o" @click="goTo('/mobile/order')">订单</van-tabbar-item>
            <van-tabbar-item name="mine" icon="user-o" @click="goTo('/mobile/mine')">我的</van-tabbar-item>
        </van-tabbar>
    </div>
</template>

<script>
import { ref, watch, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

export default {
    name: 'MobileLayout',
    setup() {
        const route = useRoute()
        const router = useRouter()
        const userStore = useUserStore()
        const active = ref('home')

        // 控制底部导航栏的显示
        const showTabbar = computed(() => {
            // 在这些路径下不显示底部导航栏
            const hideTabbarPaths = [
                '/mobile/order/checkout',
                '/mobile/login',
                '/mobile/register'
            ]
            return !hideTabbarPaths.some(path => route.path.startsWith(path))
        })

        const goTo = (path) => {
            // 检查是否需要登录
            if ((path === '/mobile/order' || path === '/mobile/menu') && !userStore.token) {
                router.push({
                    path: '/mobile/login',
                    query: { redirect: path }
                })
                return
            }
            
            // 如果当前已在目标路径，不进行跳转
            if (route.path === path) {
                return
            }
            
            router.push(path)
        }

        // 根据当前路由路径设置激活的标签
        const setActiveByPath = (path) => {
            if (path === '/mobile/login' || path === '/mobile/register') {
                // 保持之前的激活状态
                return
            }

            const pathSegments = path.split('/')
            const lastSegment = pathSegments[pathSegments.length - 1]
            
            switch (lastSegment) {
                case 'mobile':
                case '':
                    active.value = 'home'
                    break
                case 'menu':
                    active.value = 'menu'
                    break
                case 'order':
                    active.value = 'order'
                    break
                case 'mine':
                    active.value = 'mine'
                    break
            }
        }

        // 监听路由变化
        watch(() => route.path, (newPath) => {
            setActiveByPath(newPath)
        }, { immediate: true })

        return {
            active,
            goTo,
            showTabbar
        }
    }
}
</script>

<style scoped>
.mobile-layout {
    min-height: 100vh;
    padding-bottom: 50px;
    background: #f7f8fa;
}

.mobile-layout.no-tabbar {
    padding-bottom: 0;
}
</style> 