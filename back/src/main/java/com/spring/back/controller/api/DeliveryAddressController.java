package com.spring.back.controller.api;

import com.spring.back.model.Result;
import com.spring.back.model.DeliveryAddress;
import com.spring.back.service.DeliveryAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/address")
public class DeliveryAddressController {

    @Autowired
    private DeliveryAddressService addressService;

    @GetMapping("/list")
    public Result<List<DeliveryAddress>> getUserAddresses(@RequestAttribute Long userId) {
        return Result.success(addressService.getUserAddresses(userId));
    }

    @GetMapping("/{id}")
    public Result<DeliveryAddress> getAddressById(@PathVariable Long id, @RequestAttribute Long userId) {
        List<DeliveryAddress> addresses = addressService.getUserAddresses(userId);
        DeliveryAddress address = addresses.stream()
            .filter(addr -> addr.getId().equals(id))
            .findFirst()
            .orElse(null);
        return Result.success(address);
    }

    @PostMapping
    public Result<DeliveryAddress> addAddress(@RequestBody DeliveryAddress address, @RequestAttribute Long userId) {
        address.setUserId(userId);
        return Result.success(addressService.addAddress(address));
    }

    @PutMapping
    public Result<Void> updateAddress(@RequestBody DeliveryAddress address, @RequestAttribute Long userId) {
        address.setUserId(userId);
        addressService.updateAddress(address);
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteAddress(@PathVariable Long id, @RequestAttribute Long userId) {
        addressService.deleteAddress(id, userId);
        return Result.success(null);
    }

    @PutMapping("/{id}/default")
    public Result<Void> setDefaultAddress(@PathVariable Long id, @RequestAttribute Long userId) {
        addressService.setDefaultAddress(id, userId);
        return Result.success(null);
    }

    @GetMapping("/default")
    public Result<DeliveryAddress> getDefaultAddress(@RequestAttribute Long userId) {
        return Result.success(addressService.getDefaultAddress(userId));
    }
} 