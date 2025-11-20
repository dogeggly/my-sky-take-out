package com.sky.controller.admin;

import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController("adminShopController")
@RequestMapping("/admin/shop")
public class ShopController {

    public static final String KEY = "SHOP_STATUS";

    @Autowired
    private RedisTemplate redisTemplate;

    @PutMapping("/{status}")
    public Result setStatus(@PathVariable Integer status) {
        if (status == null) {
            throw new BaseException("传参有误");
        }
        redisTemplate.opsForValue().set(KEY, status);
        log.info("修改店铺营业状态：{}", status == 0 ? "打烊中" : "营业中");
        return Result.success();
    }

    @GetMapping("status")
    public Result<Integer> getStatus() {
        Integer status = (Integer) redisTemplate.opsForValue().get(KEY);
        if (status == null) {
            throw new BaseException("店铺状态未设置");
        }
        log.info("查询店铺营业状态：{}", status == 0 ? "打烊中" : "营业中");
        return Result.success(status);
    }

}
