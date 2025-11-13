package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/category")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public Result createCategory(@RequestBody CategoryDTO categoryDTO) {
        log.info("添加分类：{}", categoryDTO);
        categoryService.createCategory(categoryDTO);
        return Result.success();
    }

    @DeleteMapping
    public Result deleteCategory(Long id) {
        log.info("删除分类：{}", id);
        categoryService.deleteCategory(id);
        return Result.success();
    }

    @PutMapping
    public Result updateCategory(@RequestBody CategoryDTO categoryDTO) {
        log.info("修改分类信息：{}", categoryDTO);
        categoryService.updateCategory(categoryDTO);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    public Result updateCategoryStatus(@PathVariable Integer status, Long id) {
        log.info("修改分类状态：{},{}", status, id);
        categoryService.updateCategoryStatus(status, id);
        return Result.success();
    }

    @GetMapping("/page")
    public Result<PageResult<Category>> page(CategoryPageQueryDTO categoryPageQueryDTO) {
        log.info("分页查询分类：{}", categoryPageQueryDTO);
        PageResult<Category> pageResult = categoryService.selectByPage(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    @GetMapping("/list")
    public Result<List<Category>> list(Integer type) {
        log.info("根据类型查询分类：{}", type);
        List<Category> list = categoryService.selectCategoryByType(type);
        return Result.success(list);
    }

}
