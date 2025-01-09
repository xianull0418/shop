package com.spring.back.controller.api;

import com.spring.back.model.Product;
import com.spring.back.model.Result;
import com.spring.back.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @Autowired
    private ProductService productService;

    @GetMapping
    public Result<List<Product>> findAll() {
        log.info("查询所有商品");
        return productService.findAll();
    }

    @GetMapping("/category/{categoryId}")
    public Result<List<Product>> findByCategory(@PathVariable Integer categoryId) {
        log.info("查询分类商品: {}", categoryId);
        return productService.findByCategory(categoryId);
    }

    @GetMapping("/{id}")
    public Result<Product> findById(@PathVariable Integer id) {
        log.info("查询商品详情: {}", id);
        return productService.findById(id);
    }
} 