package com.sky.controller.user;

import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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
    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/list")
    public Result<List<DishVO>> selectDishesByCategoryId(Long categoryId) {
        log.info("根据分类id查询菜品：{}", categoryId);
        String key = "dish_" + categoryId;
        List<DishVO> dishes = (List<DishVO>) redisTemplate.opsForValue().get(key);
        if (dishes != null) {
            return Result.success(dishes);
        }
        dishes = dishService.selectDishesAndFlavorsByCategoryId(categoryId);
        redisTemplate.opsForValue().set(key, dishes);
        return Result.success(dishes);
    }

}
