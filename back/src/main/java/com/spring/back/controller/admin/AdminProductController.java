package com.spring.back.controller.admin;

import com.spring.back.model.Product;
import com.spring.back.model.Result;
import com.spring.back.service.ProductService;
import com.spring.back.annotation.RequireAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/products")
@RequireAdmin
public class AdminProductController {
    
    @Autowired
    private ProductService productService;
    
    @GetMapping
    public Result<List<Product>> findAll() {
        return productService.findAll();
    }
    
    @PostMapping
    public Result<Product> create(@RequestBody Product product) {
        return productService.create(product);
    }
    
    @PutMapping("/{id}")
    public Result<Product> update(@PathVariable Integer id, @RequestBody Product product) {
        product.setId(id);
        return productService.update(product);
    }
    
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Integer id) {
        return productService.delete(id);
    }
} 