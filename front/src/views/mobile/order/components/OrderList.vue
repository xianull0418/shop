<template>
    <div class="order-list-component">
        <van-empty v-if="orders.length === 0" description="暂无订单" />
        <div
            v-for="order in orders"
            :key="order.id"
            class="order-item"
        >
            <div class="order-header">
                <span class="order-no">订单号：{{ order.id }}</span>
                <span class="order-status">{{ getStatusText(order.status) }}</span>
            </div>
            
            <div class="order-content">
                <div 
                    v-for="item in order.items" 
                    :key="item.id" 
                    class="order-product"
                >
                    <van-image
                        width="60"
                        height="60"
                        :src="item.productImage || '/placeholder.png'"
                    />
                    <div class="product-info">
                        <div class="product-name">{{ item.productName }}</div>
                        <div class="product-price">
                            <span>¥{{ item.price }}</span>
                            <span class="quantity">x{{ item.quantity }}</span>
                        </div>
                    </div>
                </div>
            </div>
            
            <div class="order-footer">
                <div class="total-amount">
                    共{{ getTotalQuantity(order) }}件商品，
                    合计：<span class="price">¥{{ order.totalAmount }}</span>
                </div>
                <div class="order-actions">
                    <template v-if="order.status === 'pending'">
                        <van-button 
                            size="small" 
                            @click="handleCancel(order.id)"
                            :loading="cancelLoading === order.id"
                        >
                            取消订单
                        </van-button>
                        <van-button 
                            type="primary" 
                            size="small" 
                            @click="handlePay(order.id)"
                            :loading="payLoading === order.id"
                        >
                            立即支付
                        </van-button>
                    </template>
                    <van-button 
                        v-if="order.status === 'paid'"
                        size="small" 
                        type="primary"
                        plain
                        @click="handleViewDetail(order.id)"
                    >
                        查看详情
                    </van-button>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showDialog } from 'vant'
import { cancelOrder, payOrder } from '@/api/order'

const statusMap = {
    'pending': '待付款',
    'paid': '已支付',
    'completed': '已完成',
    'cancelled': '已取消'
}

export default {
    name: 'OrderList',
    props: {
        orders: {
            type: Array,
            required: true
        }
    },
    emits: ['refresh'],
    setup(props, { emit }) {
        const router = useRouter()
        const cancelLoading = ref(null)
        const payLoading = ref(null)

        const getStatusText = (status) => {
            return statusMap[status] || status
        }

        const getTotalQuantity = (order) => {
            return order.items.reduce((total, item) => total + item.quantity, 0)
        }

        const handleCancel = async (orderId) => {
            try {
                await showDialog({
                    title: '取消订单',
                    message: '确定要取消该订单吗？',
                    showCancelButton: true
                })
                
                cancelLoading.value = orderId
                await cancelOrder(orderId)
                showToast('订单已取消')
                // 通知父组件刷新列表
                emit('refresh')
            } catch (error) {
                if (error.message !== 'cancel') {
                    showToast(error.message || '取消订单失败')
                }
            } finally {
                cancelLoading.value = null
            }
        }

        const handlePay = async (orderId) => {
            try {
                payLoading.value = orderId
                await payOrder(orderId)
                showToast('支付成功')
                // 通知父组件刷新列表
                emit('refresh')
            } catch (error) {
                showToast(error.message || '支付失败')
            } finally {
                payLoading.value = null
            }
        }

        const handleViewDetail = (orderId) => {
            router.push(`/mobile/order/${orderId}`)
        }

        return {
            cancelLoading,
            payLoading,
            getTotalQuantity,
            handleCancel,
            handlePay,
            handleViewDetail,
            getStatusText
        }
    }
}
</script>

<style scoped>
.order-list-component {
    padding: 12px;
}

.order-item {
    background: #fff;
    border-radius: 8px;
    margin-bottom: 12px;
    padding: 16px;
}

.order-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-bottom: 12px;
    border-bottom: 1px solid #f5f5f5;
}

.order-no {
    font-size: 14px;
    color: #323233;
}

.order-status {
    font-size: 14px;
    color: var(--van-primary-color);
}

.order-content {
    padding: 12px 0;
}

.order-product {
    display: flex;
    margin-bottom: 12px;
}

.order-product:last-child {
    margin-bottom: 0;
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
    font-size: 14px;
    color: #969799;
}

.quantity {
    margin-left: 8px;
}

.order-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-top: 12px;
    border-top: 1px solid #f5f5f5;
}

.total-amount {
    font-size: 14px;
    color: #323233;
}

.price {
    font-weight: bold;
    color: var(--van-danger-color);
}

.order-actions {
    display: flex;
    gap: 8px;
}
</style> 