package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public Result addShoppingCart(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("添加购物车：{}",shoppingCartDTO);
        shoppingCartService.addShoppingCart(shoppingCartDTO);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<ShoppingCart>> selectAllShoppingCart(){
        log.info("查询购物车");
        List<ShoppingCart> shoppingCartList = shoppingCartService.selectAllShoppingCart();
        return Result.success(shoppingCartList);
    }

    @DeleteMapping("/clean")
    public Result deleteAllShoppingCart(){
        log.info("清空购物车");
        shoppingCartService.deleteAllShoppingCart();
        return Result.success();
    }

    @PostMapping("/sub")
    public Result deleteSubShoppingCart(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("删除购物车中的一条数据：{}",shoppingCartDTO);
        shoppingCartService.deleteSubShoppingCart(shoppingCartDTO);
        return Result.success();
    }

}
