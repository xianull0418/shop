package com.spring.back.service.impl;

import com.spring.back.mapper.ProductMapper;
import com.spring.back.model.Product;
import com.spring.back.model.Result;
import com.spring.back.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    
    @Autowired
    private ProductMapper productMapper;

    @Override
    public Result<List<Product>> findAll() {
        return Result.success(productMapper.findAll());
    }

    @Override
    public Result<List<Product>> findByCategory(Integer categoryId) {
        return Result.success(productMapper.findByCategory(categoryId));
    }

    @Override
    public Result<Product> findById(Integer id) {
        Product product = productMapper.findById(id);
        return product != null ? Result.success(product) : Result.error("商品不存在");
    }

    @Override
    public Result<Product> create(Product product) {
        productMapper.insert(product);
        return Result.success(product);
    }

    @Override
    public Result<Product> update(Product product) {
        if (productMapper.update(product) > 0) {
            return Result.success(product);
        }
        return Result.error("更新失败");
    }

    @Override
    public Result<Boolean> delete(Integer id) {
        return Result.success(productMapper.delete(id) > 0);
    }
} 