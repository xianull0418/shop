<template>
    <div class="order-detail">
        <van-nav-bar 
            title="订单详情" 
            left-arrow 
            @click-left="onClickLeft"
        />
        
        <div v-if="order" class="content">
            <!-- 订单状态 -->
            <div class="status-card">
                <div class="status-text">{{ order.statusText }}</div>
                <div class="status-desc">
                    <template v-if="order.status === 'pending'">
                        请尽快完成支付
                    </template>
                    <template v-else-if="order.status === 'paid'">
                        商家正在处理您的订单
                    </template>
                    <template v-else-if="order.status === 'completed'">
                        订单已完成
                    </template>
                    <template v-else>
                        订单已取消
                    </template>
                </div>
            </div>

            <!-- 配送信息 -->
            <div class="info-card">
                <div class="card-title">配送信息</div>
                <div class="info-item">
                    <span class="label">收货地址：</span>
                    <span class="value">{{ order.address }}</span>
                </div>
                <div class="info-item">
                    <span class="label">联系电话：</span>
                    <span class="value">{{ order.phone }}</span>
                </div>
            </div>

            <!-- 订单信息 -->
            <div class="info-card">
                <div class="card-title">订单信息</div>
                <div class="info-item">
                    <span class="label">订单编号：</span>
                    <span class="value">{{ order.id }}</span>
                </div>
                <div class="info-item">
                    <span class="label">下单时间：</span>
                    <span class="value">{{ formatDate(order.createdAt) }}</span>
                </div>
                <div class="info-item" v-if="order.remark">
                    <span class="label">订单备注：</span>
                    <span class="value">{{ order.remark }}</span>
                </div>
            </div>

            <!-- 商品清单 -->
            <div class="info-card">
                <div class="card-title">商品清单</div>
                <div 
                    v-for="item in order.items" 
                    :key="item.id"
                    class="product-item"
                >
                    <van-image
                        width="60"
                        height="60"
                        :src="item.productImage || '/placeholder.png'"
                    />
                    <div class="product-info">
                        <div class="product-name">{{ item.productName }}</div>
                        <div class="product-price">
                            <span class="price">¥{{ item.price }}</span>
                            <span class="quantity">x{{ item.quantity }}</span>
                        </div>
                    </div>
                </div>
                <div class="total">
                    共{{ getTotalQuantity() }}件商品，
                    合计：<span class="total-price">¥{{ order.totalAmount }}</span>
                </div>
            </div>
        </div>

        <!-- 底部操作栏 -->
        <div class="bottom-bar" v-if="order && order.status === 'pending'">
            <van-button 
                plain 
                type="danger" 
                size="small"
                :loading="cancelLoading"
                @click="handleCancel"
            >
                取消订单
            </van-button>
            <van-button 
                type="primary" 
                size="small"
                :loading="payLoading"
                @click="handlePay"
            >
                立即支付
            </van-button>
        </div>
    </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast, showDialog } from 'vant'
import { getOrderDetail, cancelOrder, payOrder } from '@/api/order'
import { useUserStore } from '@/stores/user'

export default {
    name: 'OrderDetail',
    setup() {
        const route = useRoute()
        const router = useRouter()
        const userStore = useUserStore()
        const order = ref(null)
        const cancelLoading = ref(false)
        const payLoading = ref(false)

        // 检查登录状态
        if (!userStore.token) {
            router.replace({
                path: '/mobile/login',
                query: { redirect: route.fullPath }
            })
            return
        }

        const loadOrder = async () => {
            try {
                const res = await getOrderDetail(route.params.id)
                order.value = res.data
            } catch (error) {
                showToast(error.message || '获取订单详情失败')
                if (error.response?.status === 401) {
                    router.push({
                        path: '/mobile/login',
                        query: { redirect: route.fullPath }
                    })
                }
            }
        }

        const formatDate = (date) => {
            return new Date(date).toLocaleString()
        }

        const getTotalQuantity = () => {
            if (!order.value) return 0
            return order.value.items.reduce((total, item) => total + item.quantity, 0)
        }

        const handleCancel = async () => {
            try {
                await showDialog({
                    title: '取消订单',
                    message: '确定要取消该订单吗？',
                    showCancelButton: true
                })
                
                cancelLoading.value = true
                await cancelOrder(order.value.id)
                showToast('订单已取消')
                await loadOrder()
            } catch (error) {
                if (error.message !== 'cancel') {
                    showToast(error.message || '取消订单失败')
                }
            } finally {
                cancelLoading.value = false
            }
        }

        const handlePay = async () => {
            try {
                payLoading.value = true
                await payOrder(order.value.id)
                showToast('支付成功')
                await loadOrder()
            } catch (error) {
                showToast(error.message || '支付失败')
            } finally {
                payLoading.value = false
            }
        }

        const onClickLeft = () => {
            router.back()
        }

        onMounted(() => {
            loadOrder()
        })

        return {
            order,
            cancelLoading,
            payLoading,
            formatDate,
            getTotalQuantity,
            handleCancel,
            handlePay,
            onClickLeft
        }
    }
}
</script>

<style scoped>
.order-detail {
    min-height: 100vh;
    background: #f7f8fa;
    padding-bottom: 60px;
}

.content {
    padding: 12px;
}

.status-card {
    background: var(--van-primary-color);
    color: #fff;
    padding: 20px 16px;
    border-radius: 8px;
    margin-bottom: 12px;
}

.status-text {
    font-size: 20px;
    font-weight: bold;
    margin-bottom: 8px;
}

.status-desc {
    font-size: 14px;
    opacity: 0.9;
}

.info-card {
    background: #fff;
    border-radius: 8px;
    padding: 16px;
    margin-bottom: 12px;
}

.card-title {
    font-size: 16px;
    font-weight: bold;
    color: #323233;
    margin-bottom: 12px;
}

.info-item {
    display: flex;
    margin-bottom: 8px;
    font-size: 14px;
}

.info-item:last-child {
    margin-bottom: 0;
}

.label {
    color: #969799;
    width: 80px;
    flex-shrink: 0;
}

.value {
    color: #323233;
    flex: 1;
}

.product-item {
    display: flex;
    padding: 12px 0;
    border-bottom: 1px solid #f5f5f5;
}

.product-item:last-child {
    border-bottom: none;
}

.product-info {
    flex: 1;
    margin-left: 12px;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
}

.product-name {
    font-size: 14px;
    color: #323233;
}

.product-price {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.price {
    font-size: 14px;
    color: #323233;
}

.quantity {
    font-size: 12px;
    color: #969799;
}

.total {
    margin-top: 12px;
    text-align: right;
    font-size: 14px;
    color: #323233;
}

.total-price {
    font-size: 16px;
    font-weight: bold;
    color: var(--van-danger-color);
}

.bottom-bar {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    height: 50px;
    background: #fff;
    display: flex;
    align-items: center;
    justify-content: flex-end;
    padding: 0 16px;
    gap: 12px;
    box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
}
</style> 