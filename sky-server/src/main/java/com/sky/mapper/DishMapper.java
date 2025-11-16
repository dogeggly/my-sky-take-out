package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

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

}
