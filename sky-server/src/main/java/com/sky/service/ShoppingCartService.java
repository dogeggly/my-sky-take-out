package com.sky.service;

import com.sky.context.CurrentContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.exception.BaseException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private DishMapper dishMapper;

    //private static final String DEFAULT_DISH_FLAVOR = "默认口味";
    private static final Integer DEFAULT_NUMBER = 1;

    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        //如果一个Dish有Flavor，前端就必须传来Flavor，否则会出现问题
        //不相信前端的话，就加上一个默认的Flavor
        /*
        if (shoppingCartDTO.getDishId() != null && shoppingCartDTO.getDishFlavor() == null) {
            shoppingCartDTO.setDishFlavor(DEFAULT_DISH_FLAVOR);
        }
        */
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(CurrentContext.getCurrent());
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.selectShoppingCart(shoppingCart);
        if (shoppingCarts != null && !shoppingCarts.isEmpty()) {
            ShoppingCart resultCart = shoppingCarts.getFirst();
            Integer number = resultCart.getNumber();
            resultCart.setNumber(number + 1);
            shoppingCartMapper.updateShoppingCartNumber(resultCart);
        } else {
            if (shoppingCart.getSetmealId() != null) {
                SetmealVO setmealVO = setmealMapper.selectSetmealById(shoppingCart.getSetmealId());
                shoppingCart.setName(setmealVO.getName());
                shoppingCart.setAmount(setmealVO.getPrice());
                shoppingCart.setImage(setmealVO.getImage());
            } else {
                DishVO dishVO = dishMapper.selectDishById(shoppingCart.getDishId());
                shoppingCart.setName(dishVO.getName());
                shoppingCart.setAmount(dishVO.getPrice());
                shoppingCart.setImage(dishVO.getImage());
            }
            shoppingCart.setNumber(DEFAULT_NUMBER);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartMapper.addShoppingCart(shoppingCart);
        }
    }

    public List<ShoppingCart> selectAllShoppingCart() {
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(CurrentContext.getCurrent()).build();
        return shoppingCartMapper.selectShoppingCart(shoppingCart);
    }

    public void deleteAllShoppingCart() {
        Long userId = CurrentContext.getCurrent();
        shoppingCartMapper.deleteAllShoppingCart(userId);
    }

    public void deleteSubShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(CurrentContext.getCurrent());
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.selectShoppingCart(shoppingCart);
        if (shoppingCarts != null && !shoppingCarts.isEmpty()) {
            ShoppingCart resultCart = shoppingCarts.getFirst();
            Integer number = resultCart.getNumber();
            if (number.equals(DEFAULT_NUMBER)){
                shoppingCartMapper.deleteSubShoppingCart(resultCart);
            }else {
                resultCart.setNumber(number - 1);
                shoppingCartMapper.updateShoppingCartNumber(resultCart);
            }
        }else {
            throw new BaseException("购物车数据异常");
        }
    }
}
