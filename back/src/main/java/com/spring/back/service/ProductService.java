package com.spring.back.service;

import com.spring.back.model.Product;
import com.spring.back.model.Result;
import java.util.List;

public interface ProductService {
    Result<List<Product>> findAll();
    Result<List<Product>> findByCategory(Integer categoryId);
    Result<Product> findById(Integer id);
    Result<Product> create(Product product);
    Result<Product> update(Product product);
    Result<Boolean> delete(Integer id);
} 