package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.StatusCount;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishItemVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface DishMapper {

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @AutoFill(value = OperationType.INSERT)
    @Insert("insert into dish (name, category_id, price, image, description, status, create_time, update_time, create_user, update_user) " +
            "values (#{name}, #{categoryId}, #{price}, #{image}, #{description}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    void createDish(Dish dish);

    Page<DishVO> selectDishByPage(DishPageQueryDTO dishPageQueryDTO);

    List<Dish> selectDishByIds(List<Long> ids);

    void deleteDish(List<Long> ids);

    DishVO selectDishById(Long id);

    @AutoFill(value = OperationType.UPDATE)
    void updateDish(Dish dish);

    @Select("select * from dish where category_id = #{categoryId}")
    List<Dish> selectDishesByCategoryId(Long categoryId);

    List<DishVO> selectDishesAndFlavorsByCategoryId(Long categoryId);

    @Select("select d.name, d.image, d.description, sd.copies from dish d " +
            "left join setmeal_dish sd on d.id = sd.dish_id " +
            "where sd.setmeal_id = #{setmealId}")
    List<DishItemVO> selectDishBySetmealId(Long setmealId);

    @MapKey("status")
    @Select("select status, count(*) count from dish group by status")
    Map<Integer, StatusCount> selectGroupByStatus();
}
