<template>
    <div class="order-list">
        <van-nav-bar title="我的订单" />
        
        <!-- 筛选器 -->
        <div class="filter-section">
            <van-dropdown-menu>
                <van-dropdown-item
                    v-model="currentStatus"
                    :options="statusOptions"
                />
            </van-dropdown-menu>
        </div>

        <van-tabs v-model:active="currentStatus" @change="handleStatusChange">
            <van-tab title="全部" name="all">
                <order-list :orders="orders" @refresh="loadOrders" />
            </van-tab>
            <van-tab title="待付款" name="pending">
                <order-list :orders="pendingOrders" @refresh="loadOrders" />
            </van-tab>
            <van-tab title="已付款" name="paid">
                <order-list :orders="paidOrders" @refresh="loadOrders" />
            </van-tab>
            <van-tab title="已完成" name="completed">
                <order-list :orders="completedOrders" @refresh="loadOrders" />
            </van-tab>
            <van-tab title="已取消" name="cancelled">
                <order-list :orders="cancelledOrders" @refresh="loadOrders" />
            </van-tab>
        </van-tabs>
    </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getMyOrders } from '@/api/order'
import OrderList from './components/OrderList.vue'
import { showToast } from 'vant'

export default {
    name: 'OrderIndex',
    components: {
        OrderList
    },
    setup() {
        const route = useRoute()
        const router = useRouter()
        const userStore = useUserStore()
        const currentStatus = ref('all')
        const orders = ref([])
        const loading = ref(false)
        const finished = ref(false)

        // 状态选项
        const statusOptions = [
            { text: '全部订单', value: 'all' },
            { text: '待付款', value: 'pending' },
            { text: '已付款', value: 'paid' },
            { text: '已完成', value: 'completed' },
            { text: '已取消', value: 'cancelled' }
        ]

        // 为每个状态创建单独的计算属性
        const pendingOrders = computed(() => 
            orders.value.filter(order => order.status === 'pending')
        )
        
        const paidOrders = computed(() => 
            orders.value.filter(order => order.status === 'paid')
        )
        
        const completedOrders = computed(() => 
            orders.value.filter(order => order.status === 'completed')
        )
        
        const cancelledOrders = computed(() => 
            orders.value.filter(order => order.status === 'cancelled')
        )
        
        const loadOrders = async () => {
            if (!userStore.token) {
                router.push({
                    path: '/mobile/login',
                    query: { redirect: '/mobile/order' }
                })
                return
            }
            
            try {
                loading.value = true
                const res = await getMyOrders()
                orders.value = res.data
                
                // 如果URL中有status参数，设置当前状态
                const status = route.query.status
                if (status) {
                    currentStatus.value = status
                }
                
                finished.value = true
            } catch (error) {
                showToast(error.message || '获取订单失败')
                if (error.response?.status === 401) {
                    router.push({
                        path: '/mobile/login',
                        query: { redirect: '/mobile/order' }
                    })
                }
            } finally {
                loading.value = false
            }
        }
        
        const handleStatusChange = (status) => {
            // 更新路由参数
            if (status === 'all') {
                router.replace('/mobile/order')
            } else {
                router.replace(`/mobile/order?status=${status}`)
            }
        }
        
        onMounted(() => {
            // 初始化时根据路由参数设置状态
            const status = route.query.status
            if (status) {
                currentStatus.value = status
            }
            loadOrders()
        })
        
        return {
            currentStatus,
            statusOptions,
            orders,
            pendingOrders,
            paidOrders,
            completedOrders,
            cancelledOrders,
            handleStatusChange,
            loading,
            finished,
            loadOrders
        }
    }
}
</script>

<style scoped>
.order-list {
    min-height: 100vh;
    background: #f7f8fa;
}

.filter-section {
    background: #fff;
    margin-bottom: 8px;
}

:deep(.van-dropdown-menu) {
    height: 48px;
}

:deep(.van-dropdown-menu__bar) {
    box-shadow: none;
}

:deep(.van-dropdown-menu__item) {
    justify-content: center;
}

:deep(.van-dropdown-menu__title) {
    font-size: 14px;
}
</style> 