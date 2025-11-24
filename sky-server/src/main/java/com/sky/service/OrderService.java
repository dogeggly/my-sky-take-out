package com.sky.service;

import com.sky.constant.MessageConstant;
import com.sky.context.CurrentContext;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressException;
import com.sky.exception.ShoppingCartException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.vo.OrderSubmitVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Transactional
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {
        Long userId = CurrentContext.getCurrent();

        AddressBook addressBook = addressBookMapper.selectAddressBookById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null) {
            throw new AddressException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }

        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(userId).build();
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.selectShoppingCart(shoppingCart);
        if (shoppingCarts == null || shoppingCarts.isEmpty()) {
            throw new ShoppingCartException(MessageConstant.SHOPPING_CART_IS_NULL);
        }

        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setUserId(userId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setPhone(addressBook.getPhone());
        orders.setAddress(addressBook.getDetail());
        orders.setConsignee(addressBook.getConsignee());
        orderMapper.addOrder(orders);

        List<OrderDetail> orderDetails = new ArrayList<>();
        for (ShoppingCart cart : shoppingCarts) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetails.add(orderDetail);
        }
        orderDetailMapper.addOrderDetails(orderDetails);

        shoppingCartMapper.deleteAllShoppingCart(userId);

        return OrderSubmitVO.builder()
                .id(orders.getId())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .orderTime(orders.getOrderTime())
                .build();
    }
}
