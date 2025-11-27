package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.entity.OrderCount;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.*;

import java.util.Map;

@Mapper
public interface OrderMapper {
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into orders (number, user_id, address_book_id, order_time, checkout_time, amount, remark, phone, address, consignee, cancel_reason, rejection_reason, cancel_time, estimated_delivery_time, delivery_time, pack_amount, tableware_number) " +
            "values " +
            "(#{number}, #{userId}, #{addressBookId}, #{orderTime}, #{checkoutTime}, #{amount}, #{remark}, #{phone}, #{address}, #{consignee}, #{cancelReason}, #{rejectionReason}, #{cancelTime}, #{estimatedDeliveryTime}, #{deliveryTime}, #{packAmount}, #{tablewareNumber})")
    void addOrder(Orders orders);

    void updateOrder(Orders orders);

    @Select("select * from orders where id = #{id}")
    Orders selectById(Long id);

    Page<OrderVO> selectOrders(Orders orders);

    @MapKey("status")
    @Select("select status, count(*) count from orders group by status;")
    Map<Integer, OrderCount> selectOrderStatistics();

    Page<OrderVO> selectOrdersByConditions(OrdersPageQueryDTO ordersPageQueryDTO);
}
