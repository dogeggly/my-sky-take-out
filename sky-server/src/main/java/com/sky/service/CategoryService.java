package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.List;

public interface CategoryService {
    void createCategory(CategoryDTO categoryDTO);

    void deleteCategory(Long id);

    void updateCategory(CategoryDTO categoryDTO);

    void updateCategoryStatus(Integer status, Long id);

    PageResult<Category> selectByPage(CategoryPageQueryDTO categoryPageQueryDTO);

    List<Category> selectCategoryByType(Integer type);
}
