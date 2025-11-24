package com.sky.mapper;

import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

@Mapper
public interface OrderMapper {
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into orders (number, user_id, address_book_id, order_time, checkout_time, amount, remark, phone, address, consignee, cancel_reason, rejection_reason, cancel_time, estimated_delivery_time, delivery_time, pack_amount, tableware_number) " +
            "values " +
            "(#{number}, #{userId}, #{addressBookId}, #{orderTime}, #{checkoutTime}, #{amount}, #{remark}, #{phone}, #{address}, #{consignee}, #{cancelReason}, #{rejectionReason}, #{cancelTime}, #{estimatedDeliveryTime}, #{deliveryTime}, #{packAmount}, #{tablewareNumber})")
    void addOrder(Orders orders);
}
