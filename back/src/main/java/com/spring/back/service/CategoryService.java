package com.spring.back.service;

import com.spring.back.model.Category;
import com.spring.back.model.Result;
import com.spring.back.model.PageRequest;
import com.spring.back.model.PageResult;
import java.util.List;

public interface CategoryService {
    Result<List<Category>> findAll();
    Result<Category> findById(Integer id);
    Result<Category> create(Category category);
    Result<Category> update(Category category);
    Result<Boolean> delete(Integer id);
    Result<PageResult<Category>> findPage(PageRequest pageRequest);
} 