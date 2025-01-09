import request from '@/utils/request'

export function getProducts() {
    return request({
        url: '/api/products',
        method: 'get'
    })
}

export function getProductsByCategory(categoryId) {
    return request({
        url: `/api/products/category/${categoryId}`,
        method: 'get'
    })
}

export function getCategories() {
    return request({
        url: '/api/categories',
        method: 'get'
    })
} 