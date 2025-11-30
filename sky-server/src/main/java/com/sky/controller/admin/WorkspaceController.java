package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/admin/workspace")
public class WorkspaceController {

    @Autowired
    private WorkspaceService workspaceService;

    @GetMapping("/businessData")
    public Result<BusinessDataVO> getBusinessData() {
        log.info("查询今天运营数据");
        LocalDate begin = LocalDate.now();
        LocalDate end = begin.plusDays(1);
        BusinessDataVO vo = workspaceService.getBusinessData(begin, end);
        return Result.success(vo);
    }

    @GetMapping("/overviewDishes")
    public Result<DishOverViewVO> overviewDishes() {
        log.info("查询菜品总览");
        DishOverViewVO vo = workspaceService.getOverviewDishes();
        return Result.success(vo);
    }

    @GetMapping("/overviewSetmeals")
    public Result<SetmealOverViewVO> overviewSetmeals() {
        log.info("查询套餐总览");
        SetmealOverViewVO vo = workspaceService.getOverviewSetmeals();
        return Result.success(vo);
    }

    @GetMapping("/overviewOrders")
    public Result<OrderOverViewVO> overviewOrders() {
        log.info("查询订单管理数据");
        OrderOverViewVO vo = workspaceService.getOverviewOrders();
        return Result.success(vo);
    }

}
