package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @PostMapping
    public Result createDish(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品：{}", dishDTO);
        dishService.createDish(dishDTO);
        return Result.success();
    }

    @GetMapping("/page")
    public Result<PageResult<DishVO>> selectDishByPage(DishPageQueryDTO dishPageQueryDTO) {
        log.info("分页查询菜品：{}", dishPageQueryDTO);
        PageResult<DishVO> pageResult = dishService.selectDishByPage(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    @DeleteMapping
    public Result deleteDish(@RequestParam List<Long> ids) {
        log.info("删除菜品：{}", ids);
        dishService.deleteDish(ids);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<DishVO> selectDishById(@PathVariable Long id) {
        log.info("根据id查询菜品：{}", id);
        DishVO dishVO = dishService.selectDishById(id);
        return Result.success(dishVO);
    }

    @PutMapping
    public Result updateDish(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品信息：{}", dishDTO);
        dishService.updateDish(dishDTO);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<Dish>> selectDishesByCategoryId(Long categoryId) {
        log.info("根据分类id查询菜品：{}", categoryId);
        List<Dish> dishes = dishService.selectDishesByCategoryId(categoryId);
        return Result.success(dishes);
    }

    @PostMapping("/status/{status}")
    public Result updateDishStatus(@PathVariable Integer status, Long id) {
        log.info("修改菜品状态：{},{}", status, id);
        dishService.updateDishStatus(status, id);
        return Result.success();
    }
}
