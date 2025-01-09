import request from '@/utils/request'

// 创建订单
export function createOrder(data) {
    return request({
        url: '/api/orders',
        method: 'post',
        data
    })
}

// 获取订单列表
export function getMyOrders() {
    return request({
        url: '/api/orders/my',
        method: 'get'
    })
}

// 获取订单详情
export function getOrderDetail(id) {
    return request({
        url: `/api/orders/${id}`,
        method: 'get'
    })
}

// 取消订单
export function cancelOrder(id) {
    return request({
        url: `/api/orders/${id}/cancel`,
        method: 'post'
    })
}

// 支付订单
export function payOrder(id) {
    return request({
        url: `/api/orders/${id}/pay`,
        method: 'post'
    })
} 