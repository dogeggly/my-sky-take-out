package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController("adminSetmealController")
@RequestMapping("/admin/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @CacheEvict(value = "setmealCache", key = "#setmealDTO.categoryId")
    @PostMapping
    public Result CreateSetmeal(@RequestBody SetmealDTO setmealDTO) {
        log.info("添加套餐：{}", setmealDTO);
        setmealService.createSetmeal(setmealDTO);
        return Result.success();
    }

    @Caching(evict = {
            @CacheEvict(value = "setmealDishCache", key = "#setmealDTO.id"),
            @CacheEvict(value = "setmealCache", key = "#setmealDTO.categoryId")
    })
    @PutMapping
    public Result UpdateSetmeal(@RequestBody SetmealDTO setmealDTO) {
        log.info("修改套餐信息：{}", setmealDTO);
        setmealService.updateSetmeal(setmealDTO);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<SetmealVO> selectSetmealById(@PathVariable Long id) {
        log.info("根据id查询套餐：{}", id);
        SetmealVO setmealVO = setmealService.selectSetmealById(id);
        return Result.success(setmealVO);
    }

    @CacheEvict(value = "setmealCache", allEntries = true)
    @PostMapping("/status/{status}")
    public Result UpdateSetmealStatus(@PathVariable Integer status, Long id) {
        log.info("修改套餐状态：{}, {}", status, id);
        setmealService.UpdateSetmealStatus(status, id);
        return Result.success();
    }

    @GetMapping("/page")
    public Result<PageResult<SetmealVO>> selectSetmealByPage(SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("分页查询套餐：{}", setmealPageQueryDTO);
        PageResult<SetmealVO> pageResult = setmealService.selectSetmealByPage(setmealPageQueryDTO);
        return Result.success(pageResult);
    }


    @Caching(evict = {
            @CacheEvict(value = "setmealDishCache", allEntries = true),
            @CacheEvict(value = "setmealCache", allEntries = true)
    })
    @DeleteMapping
    public Result deleteSetmealByIds(@RequestParam List<Long> ids) {
        log.info("删除套餐：{}", ids);
        setmealService.deleteSetmealByIds(ids);
        return Result.success();
    }

}
