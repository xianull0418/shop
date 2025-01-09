package com.spring.back.service.impl;

import com.spring.back.mapper.CategoryMapper;
import com.spring.back.mapper.ProductMapper;
import com.spring.back.model.Category;
import com.spring.back.model.Result;
import com.spring.back.model.PageRequest;
import com.spring.back.model.PageResult;
import com.spring.back.service.CategoryService;
import com.spring.back.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    
    @Autowired
    private CategoryMapper categoryMapper;
    
    @Autowired
    private ProductMapper productMapper;

    @Override
    public Result<List<Category>> findAll() {
        return Result.success(categoryMapper.findAll());
    }

    @Override
    public Result<Category> findById(Integer id) {
        Category category = categoryMapper.findById(id);
        return category != null ? Result.success(category) : Result.error("分类不存在");
    }

    @Override
    public Result<Category> create(Category category) {
        Category existCategory = categoryMapper.findByName(category.getName());
        if (existCategory != null) {
            throw new BusinessException("分类名称已存在");
        }
        
        categoryMapper.insert(category);
        return Result.success(category);
    }

    @Override
    public Result<Category> update(Category category) {
        Category existCategory = categoryMapper.findById(category.getId());
        if (existCategory == null) {
            throw new BusinessException("分类不存在");
        }
        
        Category nameExist = categoryMapper.findByName(category.getName());
        if (nameExist != null && !nameExist.getId().equals(category.getId())) {
            throw new BusinessException("分类名称已存在");
        }
        
        categoryMapper.update(category);
        return Result.success(category);
    }

    @Override
    public Result<Boolean> delete(Integer id) {
        Category category = categoryMapper.findById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        
        // 检查是否有商品使用该分类
        if (productMapper.countByCategoryId(id) > 0) {
            throw new BusinessException("该分类下有商品，无法删除");
        }
        
        categoryMapper.delete(id);
        return Result.success(true);
    }

    @Override
    public Result<PageResult<Category>> findPage(PageRequest pageRequest) {
        List<Category> list = categoryMapper.findPage(
            pageRequest.getKeyword(),
            pageRequest.getOffset(),
            pageRequest.getSize()
        );
        Long total = categoryMapper.count(pageRequest.getKeyword());
        
        return Result.success(PageResult.of(list, total, pageRequest.getSize()));
    }
} 