package com.sky.controller.user;

import com.sky.entity.Setmeal;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController("userSetmealController")
@RequestMapping("/user/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;
    @Autowired
    private DishService dishService;

    @GetMapping("/list")
    public Result<List<Setmeal>> selectSetmealByCategoryId(Long categoryId) {
        log.info("根据分类id查询套餐：{}", categoryId);
        List<Setmeal> setmeals = setmealService.selectSetmealByCategoryId(categoryId);
        return Result.success(setmeals);
    }

    @GetMapping("/dish/{id}")
    public Result<List<DishItemVO>> selectDishBySetmealId(@PathVariable Long id){
        log.info("查询套餐内包含的菜品：{}", id);
        List<DishItemVO> dishItemVO = dishService.selectDishBySetmealId(id);
        return Result.success(dishItemVO);
    }

}
