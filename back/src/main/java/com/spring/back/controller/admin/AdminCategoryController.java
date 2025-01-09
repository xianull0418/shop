package com.spring.back.controller.admin;

import com.spring.back.model.Category;
import com.spring.back.model.PageRequest;
import com.spring.back.model.PageResult;
import com.spring.back.model.Result;
import com.spring.back.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
public class AdminCategoryController {
    
    @Autowired
    private CategoryService categoryService;
    
    @GetMapping
    public Result<List<Category>> findAll() {
        return categoryService.findAll();
    }
    
    @PostMapping
    public Result<Category> create(@Valid @RequestBody Category category) {
        return categoryService.create(category);
    }
    
    @PutMapping("/{id}")
    public Result<Category> update(@PathVariable Integer id, @Valid @RequestBody Category category) {
        category.setId(id);
        return categoryService.update(category);
    }
    
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Integer id) {
        return categoryService.delete(id);
    }
    
    @GetMapping("/page")
    public Result<PageResult<Category>> findPage(PageRequest pageRequest) {
        return categoryService.findPage(pageRequest);
    }
} 