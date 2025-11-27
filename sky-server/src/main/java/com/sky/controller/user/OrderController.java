package com.sky.controller.user;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController("userOrderController")
@RequestMapping("/user/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/submit")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {
        log.info("用户端提交订单: {}", ordersSubmitDTO);
        OrderSubmitVO orderSubmitVO = orderService.submit(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    @PutMapping("/payment")
    public Result<OrderPaymentVO> pay(@RequestBody OrdersPaymentDTO ordersPaymentDTO) {
        log.info("用户端支付订单: {}", ordersPaymentDTO);
        orderService.pay(ordersPaymentDTO);
        return Result.error("支付功能未开发");
    }

    @PutMapping("/cancel/{id}")
    public Result cancel(@PathVariable Long id) {
        log.info("用户取消订单：{}", id);
        OrdersCancelDTO ordersCancelDTO = new OrdersCancelDTO();
        ordersCancelDTO.setId(id);
        ordersCancelDTO.setCancelReason("用户取消订单");
        orderService.cancel(ordersCancelDTO);
        return Result.success();
    }

    @GetMapping("/orderDetail/{id}")
    public Result<OrderVO> selectOrderDetail(@PathVariable Long id) {
        log.info("查看订单详情：{}", id);
        OrderVO orderVO = orderService.selectOrder(id);
        return Result.success(orderVO);
    }

    @GetMapping("/historyOrders")
    public Result<PageResult<OrderVO>> selectHistoryOrders(int page, int pageSize, Integer status) {
        log.info("查询历史订单：{} ,{} ,{}", page, pageSize, status);
        PageResult<OrderVO> pageResult = orderService.selectHistoryOrders(page, pageSize, status);
        return Result.success(pageResult);
    }

    @PostMapping("/repetition/{id}")
    public Result repetition(@PathVariable Long id) {
        log.info("再来一单：{}", id);
        orderService.repetition(id);
        return Result.success();
    }

}
