package com.sky.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DishService {

    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Transactional
    public void createDish(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dish.setStatus(StatusConstant.DISABLE);
        dishMapper.createDish(dish);
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            for (DishFlavor flavor : flavors) {
                flavor.setDishId(dish.getId());
            }
            dishFlavorMapper.createDishFlavors(flavors);
        }
    }

    public PageResult<DishVO> selectDishByPage(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.selectDishByPage(dishPageQueryDTO);
        return new PageResult<>(page.getTotal(), page.getResult());
    }

    @Transactional
    public void deleteDish(List<Long> ids) {
        List<Dish> dishes = dishMapper.selectDishByIds(ids);
        if (dishes != null && !dishes.isEmpty()) {
            for (Dish dish : dishes) {
                if (dish.getStatus().equals(StatusConstant.ENABLE)) {
                    throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
                }
            }
        }
        List<Long> setmealIds = setmealDishMapper.selectSetmealIdsByDishIds(ids);
        if (setmealIds != null && !setmealIds.isEmpty()) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        dishMapper.deleteDish(ids);
        dishFlavorMapper.deleteDishFlavors(ids);
    }

    public DishVO selectDishById(Long id) {
        return dishMapper.selectDishById(id);
    }

    @Transactional
    public void updateDish(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.updateDish(dish);
        List<Long> ids = new ArrayList<>();
        ids.add(dish.getId());
        dishFlavorMapper.deleteDishFlavors(ids);
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            for (DishFlavor flavor : flavors) {
                flavor.setDishId(dish.getId());
            }
            dishFlavorMapper.createDishFlavors(flavors);
        }
    }

    public List<Dish> selectDishesByCategoryId(Long categoryId) {
        return dishMapper.selectDishesByCategoryId(categoryId);
    }

    public void updateDishStatus(Integer status, Long id) {
        Dish dish = Dish.builder()
                .id(id)
                .status(status)
                .build();
        dishMapper.updateDish(dish);
    }

    public List<DishVO> selectDishesAndFlavorsByCategoryId(Long categoryId) {
        String key = "dish_" + categoryId;
        List<DishVO> dishes = (List<DishVO>) redisTemplate.opsForValue().get(key);
        if (dishes != null) {
            return dishes;
        }
        dishes = dishMapper.selectDishesAndFlavorsByCategoryId(categoryId);
        redisTemplate.opsForValue().set(key, dishes);
        return dishes;
    }

    @Cacheable(cacheNames = "setmealDishCache", key = "#setmealId")
    public List<DishItemVO> selectDishBySetmealId(Long setmealId) {
        return dishMapper.selectDishBySetmealId(setmealId);
    }

}
