import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import NotFound from '@/views/NotFound.vue'

const routes = [
    {
        path: '/',
        redirect: '/mobile'
    },
    {
        path: '/mobile/login',
        name: 'Login',
        component: () => import('@/views/auth/Login.vue'),
        meta: { title: '登录' }
    },
    {
        path: '/mobile/register',
        name: 'Register',
        component: () => import('@/views/auth/Register.vue'),
        meta: { title: '注册' }
    },
    {
        path: '/mobile',
        component: () => import('@/layouts/MobileLayout.vue'),
        children: [
            {
                path: '',
                name: 'Home',
                component: () => import('@/views/mobile/home/index.vue'),
                meta: { title: '首页' }
            },
            {
                path: 'menu',
                name: 'Menu',
                component: () => import('@/views/mobile/menu/index.vue'),
                meta: { title: '点单' }
            },
            {
                path: 'order',
                name: 'OrderList',
                component: () => import('@/views/mobile/order/index.vue'),
                meta: { title: '订单', requireAuth: true }
            },
            {
                path: 'order/checkout',
                name: 'OrderCheckout',
                component: () => import('@/views/mobile/order/checkout.vue'),
                meta: { title: '订单结算', requireAuth: true }
            },
            {
                path: 'order/:id',
                name: 'OrderDetail',
                component: () => import('@/views/mobile/order/detail.vue'),
                meta: { title: '订单详情', requireAuth: true }
            },
            {
                path: 'mine',
                name: 'Mine',
                component: () => import('@/views/mobile/mine/index.vue'),
                meta: { title: '我的' }
            },
            {
                path: 'address',
                component: () => import('@/views/mobile/address/index.vue'),
                meta: { requireAuth: true }
            },
            {
                path: 'address/edit/:id?',
                component: () => import('@/views/mobile/address/edit.vue'),
                meta: { requireAuth: true }
            }
        ]
    },
    {
        path: '/admin/login',
        name: 'AdminLogin',
        component: () => import('@/views/admin/login/index.vue'),
        meta: { title: '管理员登录' }
    },
    {
        path: '/admin',
        component: () => import('@/layouts/AdminLayout.vue'),
        meta: { requireAuth: true, requireAdmin: true },
        children: [
            {
                path: '',
                redirect: '/admin/products'
            },
            {
                path: 'products',
                name: 'AdminProducts',
                component: () => import('@/views/admin/products/index.vue'),
                meta: { title: '商品管理' }
            },
            {
                path: 'orders',
                name: 'AdminOrders',
                component: () => import('@/views/admin/orders/index.vue'),
                meta: { title: '订单管理' }
            },
            {
                path: 'categories',
                name: 'AdminCategories',
                component: () => import('@/views/admin/categories/index.vue'),
                meta: { title: '分类管理' }
            },
            {
                path: 'users',
                name: 'AdminUsers',
                component: () => import('@/views/admin/users/index.vue'),
                meta: { title: '用户管理' }
            }
        ]
    },
    {
        path: '/:pathMatch(.*)*',
        name: 'NotFound',
        component: NotFound
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
    const userStore = useUserStore()
    const token = userStore.token

    // 设置页面标题
    if (to.meta.title) {
        document.title = `${to.meta.title} - 面包商店`
    }

    // 已登录用户访问登录页或注册页，重定向到首页
    if (token && ['/mobile/login', '/mobile/register', '/admin/login'].includes(to.path)) {
        next(userStore.userInfo?.role === 'admin' ? '/admin/products' : '/mobile')
        return
    }

    // 需要登录但没有token的页面
    if (to.meta.requireAuth && !token) {
        next({
            path: to.path.startsWith('/admin') ? '/admin/login' : '/mobile/login',
            query: { redirect: to.fullPath }
        })
        return
    }

    // 需要管理员权限
    if (to.meta.requireAdmin && userStore.userInfo?.role !== 'admin') {
        next('/admin/login')
        return
    }

    // 其他情况正常放行
    next()
})

export default router 