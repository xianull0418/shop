import request from '@/utils/request'

// 商品管理
export function getAdminProducts() {
    return request({
        url: '/api/admin/products',
        method: 'get'
    })
}

export function createProduct(data) {
    return request({
        url: '/api/admin/products',
        method: 'post',
        data
    })
}

export function updateProduct(id, data) {
    return request({
        url: `/api/admin/products/${id}`,
        method: 'put',
        data
    })
}

export function deleteProduct(id) {
    return request({
        url: `/api/admin/products/${id}`,
        method: 'delete'
    })
}

// 订单管理
export function getAdminOrders() {
    return request({
        url: '/api/admin/orders',
        method: 'get'
    })
}

export function updateOrderStatus(id, status) {
    return request({
        url: `/api/admin/orders/${id}/status`,
        method: 'put',
        params: { status }
    })
}

// 分类管理
export function getAdminCategories(params) {
    return request({
        url: '/api/admin/categories/page',
        method: 'get',
        params
    })
}

export function createCategory(data) {
    return request({
        url: '/api/admin/categories',
        method: 'post',
        data
    })
}

export function updateCategory(id, data) {
    return request({
        url: `/api/admin/categories/${id}`,
        method: 'put',
        data
    })
}

export function deleteCategory(id) {
    return request({
        url: `/api/admin/categories/${id}`,
        method: 'delete'
    })
}

// 文件上传
export function uploadFile(data) {
    return request({
        url: '/api/admin/upload',
        method: 'post',
        headers: {
            'Content-Type': 'multipart/form-data'
        },
        data
    })
}

// 用户管理
export function getAdminUsers(params) {
    return request({
        url: '/api/admin/users',
        method: 'get',
        params
    })
}

export function updateUserStatus(id, status) {
    return request({
        url: `/api/admin/users/${id}/status`,
        method: 'put',
        params: { status }
    })
}

export function deleteUser(id) {
    return request({
        url: `/api/admin/users/${id}`,
        method: 'delete'
    })
} 