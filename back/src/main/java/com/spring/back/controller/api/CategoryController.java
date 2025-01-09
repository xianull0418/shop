package com.spring.back.controller.api;

import com.spring.back.model.Category;
import com.spring.back.model.Result;
import com.spring.back.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    
    @Autowired
    private CategoryService categoryService;
    
    @GetMapping
    public Result<List<Category>> findAll() {
        return categoryService.findAll();
    }
    
    @GetMapping("/{id}")
    public Result<Category> findById(@PathVariable Integer id) {
        return categoryService.findById(id);
    }
} 