package com.sky.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.UpdateStatusException;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Transactional
    public void createSetmeal(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmeal.setStatus(StatusConstant.DISABLE);
        setmealMapper.createSetmeal(setmeal);
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && !setmealDishes.isEmpty()) {
            for (SetmealDish setmealDish : setmealDishes) {
                setmealDish.setSetmealId(setmeal.getId());
            }
            setmealDishMapper.createSetmealDishes(setmealDishes);
        }
    }

    @Transactional
    public void updateSetmeal(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.updateSetmeal(setmeal);
        List<Long> ids = new ArrayList<>();
        ids.add(setmeal.getId());
        setmealDishMapper.deleteSetmealDishes(ids);
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        if (setmealDishes != null && !setmealDishes.isEmpty()) {
            for (SetmealDish setmealDish : setmealDishes) {
                setmealDish.setSetmealId(setmeal.getId());
            }
            setmealDishMapper.createSetmealDishes(setmealDishes);
        }
    }

    @Transactional
    public void UpdateSetmealStatus(Integer status, Long id) {
        if (status.equals(StatusConstant.ENABLE)) {
            List<Integer> dishStatuses = setmealDishMapper.selectDishStatusesBySetmealId(id);
            if (dishStatuses != null && !dishStatuses.isEmpty()) {
                for (Integer dishStatus : dishStatuses) {
                    if (dishStatus.equals(StatusConstant.DISABLE)) {
                        throw new UpdateStatusException(MessageConstant.SETMEAL_ENABLE_FAILED);
                    }
                }
            }
        }
        Setmeal setmeal = Setmeal.builder().id(id).status(status).build();
        setmealMapper.updateSetmeal(setmeal);
    }

    public PageResult<SetmealVO> selectSetmealByPage(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setmealMapper.selectSetmealByPage(setmealPageQueryDTO);
        return new PageResult<>(page.getTotal(), page.getResult());
    }

    public SetmealVO selectSetmealById(Long id) {
        return setmealMapper.selectSetmealById(id);
    }

    @Transactional
    public void deleteSetmealByIds(List<Long> ids) {
        List<Setmeal> setmeals = setmealMapper.selectSetmealsByIds(ids);
        if (setmeals != null && !setmeals.isEmpty()) {
            for (Setmeal setmeal : setmeals) {
                if (setmeal.getStatus().equals(StatusConstant.ENABLE)) {
                    throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
                }
            }
        }
        setmealMapper.deleteSetmeals(ids);
        setmealDishMapper.deleteSetmealDishes(ids);
    }

    public List<Setmeal> selectSetmealByCategoryId(Long categoryId) {
        Integer status = StatusConstant.ENABLE;
        return setmealDishMapper.selectSetmealByCategoryId(categoryId, status);
    }
}
