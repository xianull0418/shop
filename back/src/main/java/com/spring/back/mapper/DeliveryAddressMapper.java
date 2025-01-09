package com.spring.back.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.spring.back.model.DeliveryAddress;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface DeliveryAddressMapper extends BaseMapper<DeliveryAddress> {
    
    @Update("UPDATE delivery_addresses SET is_default = 0 WHERE user_id = #{userId} AND id != #{addressId}")
    void clearOtherDefault(@Param("userId") Long userId, @Param("addressId") Long addressId);
} 