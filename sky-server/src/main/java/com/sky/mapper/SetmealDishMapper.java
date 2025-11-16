package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    List<Long> selectSetmealIdsByDishIds(List<Long> dishIds);

    void createSetmealDishes(List<SetmealDish> setmealDishes);

    void deleteSetmealDishes(List<Long> setmealIds);

    @Select("select d.status from setmeal_dish sd left join dish d on sd.dish_id = d.id where sd.setmeal_id = #{id}")
    List<Integer> selectDishStatusesBySetmealId(Long id);
}
