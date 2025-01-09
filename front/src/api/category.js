import request from '@/utils/request'

// 获取所有分类
export function getCategories() {
    return request({
        url: '/api/categories',
        method: 'get'
    })
}

// 获取分类详情
export function getCategory(id) {
    return request({
        url: `/api/categories/${id}`,
        method: 'get'
    })
} 