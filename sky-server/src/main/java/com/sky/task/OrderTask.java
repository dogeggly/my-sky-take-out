package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    @Scheduled(cron = "0 * * * * ?")
    public void processTimeOutOrder() {
        log.info("定时处理超时订单");
        LocalDateTime orderTime = LocalDateTime.now().plusMinutes(-15);
        List<Integer> list = orderMapper.selectByOrderTime(Orders.PENDING_PAYMENT, orderTime);
        Integer status = Orders.CANCELLED;
        String cancelReason = "订单超时未支付，自动取消";
        LocalDateTime cancelTime = LocalDateTime.now();
        orderMapper.updateOrders(list, status, cancelReason, cancelTime);
    }

}
