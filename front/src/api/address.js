import request from '@/utils/request'

// 获取地址列表
export function getAddressList() {
    return request({
        url: '/api/address/list',
        method: 'get'
    })
}

// 获取地址详情
export function getAddressById(id) {
    return request({
        url: `/api/address/${id}`,
        method: 'get'
    })
}

// 添加地址
export function addAddress(data) {
    return request({
        url: '/api/address',
        method: 'post',
        data
    })
}

// 更新地址
export function updateAddress(data) {
    return request({
        url: '/api/address',
        method: 'put',
        data
    })
}

// 删除地址
export function deleteAddress(id) {
    return request({
        url: `/api/address/${id}`,
        method: 'delete'
    })
}

// 设置默认地址
export function setDefaultAddress(id) {
    return request({
        url: `/api/address/${id}/default`,
        method: 'put'
    })
}

// 获取默认地址
export function getDefaultAddress() {
    return request({
        url: '/api/address/default',
        method: 'get'
    })
} 