package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealMapper {

    @AutoFill(value = OperationType.INSERT)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into setmeal(category_id, name, price, status, description, image, create_time, update_time, create_user, update_user) " +
            "values(#{categoryId}, #{name}, #{price}, #{status}, #{description}, #{image}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    void createSetmeal(Setmeal setmeal);

    @AutoFill(value = OperationType.UPDATE)
    void updateSetmeal(Setmeal setmeal);

    Page<SetmealVO> selectSetmealByPage(SetmealPageQueryDTO setmealPageQueryDTO);

    SetmealVO selectSetmealById(Long id);

    void deleteSetmeals(List<Long> ids);

    List<Setmeal> selectDishByIds(List<Long> ids);

    @Select("select id from setmeal where category_id = #{categoryId}")
    List<Setmeal> selectSetmealsByCategoryId(Long categoryId);

}
