package com.sky.controller.user;

import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequestMapping("/user/dish")
@RestController("userDishController")
public class DishController {

    @Autowired
    private DishService dishService;

    @GetMapping("/list")
    public Result<List<DishVO>> selectDishesByCategoryId(Long categoryId) {
        log.info("根据分类id查询菜品：{}", categoryId);
        List<DishVO> dishes = dishService.selectDishesAndFlavorsByCategoryId(categoryId);
        return Result.success(dishes);
    }

}
