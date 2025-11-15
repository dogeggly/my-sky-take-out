package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    void createDishFlavors(List<DishFlavor> flavors);

    void deleteDishFlavors(List<Long> dishIds);
}
