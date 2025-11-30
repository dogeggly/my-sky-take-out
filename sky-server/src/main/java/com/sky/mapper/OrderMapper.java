package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.entity.StatusCount;
import com.sky.entity.SelectDate;
import com.sky.entity.SelectDateResult;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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
    Map<Integer, StatusCount> selectOrderStatistics();

    Page<OrderVO> selectOrdersByConditions(OrdersPageQueryDTO ordersPageQueryDTO);

    @Select("select id from orders where status = #{pendingPayment} and order_time < #{orderTime}")
    List<Integer> selectByOrderTime(Integer pendingPayment, LocalDateTime orderTime);

    void updateOrders(List<Integer> list, Integer status, String cancelReason, LocalDateTime cancelTime);

    @Select("select id from orders where number = #{number}")
    Integer selectByNumber(String number);

    @MapKey("date")
    Map<LocalDate, SelectDateResult> selectTurnover(List<SelectDate> list);

    @MapKey("date")
    Map<LocalDate, SelectDateResult> selectOrderCount(List<SelectDate> list);

    @MapKey("date")
    Map<LocalDate, SelectDateResult> selectOrderValid(List<SelectDate> list);

    @Select("select count(*) validOrderCount, sum(amount) turnover from orders " +
            "where order_time >= #{begin} and order_time < #{end} and status = 5")
    BusinessDataVO selectToday(LocalDate begin, LocalDate end);

    @Select("select count(*) from orders " +
            "where order_time >= #{begin} and order_time < #{end}")
    Integer selectAllOrderToday(LocalDate begin, LocalDate end);

    @Select("select count(*) from orders")
    Integer selectAllOrder();
}
