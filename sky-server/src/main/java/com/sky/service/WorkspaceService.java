package com.sky.service;

import com.sky.constant.StatusConstant;
import com.sky.entity.Orders;
import com.sky.entity.StatusCount;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.UserMapper;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

@Service
public class WorkspaceService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    public BusinessDataVO getBusinessData(LocalDate begin, LocalDate end) {
        BusinessDataVO businessDataVO = orderMapper.selectToday(begin, end);
        if (businessDataVO.getTurnover() == null) {
            businessDataVO.setTurnover(0.0);
        }
        Double unitPrice = businessDataVO.getValidOrderCount() == 0 ?
                0.0 : businessDataVO.getTurnover() / businessDataVO.getValidOrderCount();
        businessDataVO.setUnitPrice(unitPrice);
        Integer orderCount = orderMapper.selectAllOrderToday(begin, end);
        Double orderCompletionRate = orderCount == 0 ?
                0.0 : (double) businessDataVO.getValidOrderCount() / orderCount;
        businessDataVO.setOrderCompletionRate(orderCompletionRate);
        businessDataVO.setNewUsers(userMapper.selectToday(begin, end));
        return businessDataVO;
    }

    public DishOverViewVO getOverviewDishes() {
        DishOverViewVO vo = new DishOverViewVO();
        Map<Integer, StatusCount> map = dishMapper.selectGroupByStatus();
        StatusCount disable = map.get(StatusConstant.DISABLE);
        if (disable != null) {
            vo.setDiscontinued(disable.getCount());
        } else {
            vo.setDiscontinued(0);
        }
        StatusCount enable = map.get(StatusConstant.ENABLE);
        if (enable != null) {
            vo.setSold(enable.getCount());
        } else {
            vo.setSold(0);
        }
        return vo;
    }

    public SetmealOverViewVO getOverviewSetmeals() {
        SetmealOverViewVO vo = new SetmealOverViewVO();
        Map<Integer, StatusCount> map = setmealMapper.selectGroupByStatus();
        StatusCount disable = map.get(StatusConstant.DISABLE);
        if (disable != null) {
            vo.setDiscontinued(disable.getCount());
        } else {
            vo.setDiscontinued(0);
        }
        StatusCount enable = map.get(StatusConstant.ENABLE);
        if (enable != null) {
            vo.setSold(enable.getCount());
        } else {
            vo.setSold(0);
        }
        return vo;
    }

    public OrderOverViewVO getOverviewOrders() {
        OrderOverViewVO vo = new OrderOverViewVO();
        Map<Integer, StatusCount> map = orderMapper.selectOrderStatistics();
        StatusCount toBC = map.get(Orders.TO_BE_CONFIRMED);
        if (toBC != null) {
            vo.setWaitingOrders(toBC.getCount());
        } else {
            vo.setWaitingOrders(0);
        }
        StatusCount confirmed = map.get(Orders.CONFIRMED);
        if (confirmed!=null) {
            vo.setDeliveredOrders(confirmed.getCount());
        }else {
            vo.setDeliveredOrders(0);
        }
        StatusCount completed = map.get(Orders.COMPLETED);
        if (completed != null) {
            vo.setCompletedOrders(completed.getCount());
        } else {
            vo.setCompletedOrders(0);
        }
        StatusCount cancelled = map.get(Orders.CANCELLED);
        if (cancelled != null) {
            vo.setCancelledOrders(cancelled.getCount());
        } else {
            vo.setCancelledOrders(0);
        }
        vo.setAllOrders(orderMapper.selectAllOrder());
        return vo;
    }
}
