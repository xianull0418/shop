package com.spring.back.service;

import com.spring.back.model.DeliveryAddress;
import java.util.List;

public interface DeliveryAddressService {
    
    // 获取用户的所有收货地址
    List<DeliveryAddress> getUserAddresses(Long userId);
    
    // 添加收货地址
    DeliveryAddress addAddress(DeliveryAddress address);
    
    // 更新收货地址
    void updateAddress(DeliveryAddress address);
    
    // 删除收货地址
    void deleteAddress(Long addressId, Long userId);
    
    // 设置默认地址
    void setDefaultAddress(Long addressId, Long userId);
    
    // 获取用户的默认地址
    DeliveryAddress getDefaultAddress(Long userId);
} 