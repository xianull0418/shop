package com.spring.back.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.spring.back.model.DeliveryAddress;
import com.spring.back.mapper.DeliveryAddressMapper;
import com.spring.back.service.DeliveryAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DeliveryAddressServiceImpl implements DeliveryAddressService {

    @Autowired
    private DeliveryAddressMapper addressMapper;

    @Override
    public List<DeliveryAddress> getUserAddresses(Long userId) {
        return addressMapper.selectList(
            new LambdaQueryWrapper<DeliveryAddress>()
                .eq(DeliveryAddress::getUserId, userId)
                .orderByDesc(DeliveryAddress::getIsDefault)
                .orderByDesc(DeliveryAddress::getUpdatedAt)
        );
    }

    @Override
    @Transactional
    public DeliveryAddress addAddress(DeliveryAddress address) {
        // 如果是默认地址，清除其他默认地址
        if (Boolean.TRUE.equals(address.getIsDefault())) {
            addressMapper.clearOtherDefault(address.getUserId(), null);
        }
        addressMapper.insert(address);
        return address;
    }

    @Override
    @Transactional
    public void updateAddress(DeliveryAddress address) {
        // 如果是默认地址，清除其他默认地址
        if (Boolean.TRUE.equals(address.getIsDefault())) {
            addressMapper.clearOtherDefault(address.getUserId(), address.getId());
        }
        addressMapper.updateById(address);
    }

    @Override
    public void deleteAddress(Long addressId, Long userId) {
        addressMapper.delete(
            new LambdaQueryWrapper<DeliveryAddress>()
                .eq(DeliveryAddress::getId, addressId)
                .eq(DeliveryAddress::getUserId, userId)
        );
    }

    @Override
    @Transactional
    public void setDefaultAddress(Long addressId, Long userId) {
        // 清除其他默认地址
        addressMapper.clearOtherDefault(userId, addressId);
        
        // 设置新的默认地址
        DeliveryAddress address = new DeliveryAddress();
        address.setId(addressId);
        address.setIsDefault(true);
        addressMapper.updateById(address);
    }

    @Override
    public DeliveryAddress getDefaultAddress(Long userId) {
        return addressMapper.selectOne(
            new LambdaQueryWrapper<DeliveryAddress>()
                .eq(DeliveryAddress::getUserId, userId)
                .eq(DeliveryAddress::getIsDefault, true)
        );
    }
} 