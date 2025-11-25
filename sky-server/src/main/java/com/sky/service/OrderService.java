package com.sky.service;

import cn.hutool.core.util.IdUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.CurrentContext;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressException;
import com.sky.exception.BaseException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.result.PageResult;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        orders.setNumber(IdUtil.getSnowflakeNextIdStr());
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

    public void pay(OrdersPaymentDTO ordersPaymentDTO) {
        //需要完成支付操作
        Orders orders = Orders.builder()
                .number(ordersPaymentDTO.getOrderNumber())
                .status(Orders.TO_BE_CONFIRMED)
                .checkoutTime(LocalDateTime.now())
                .payMethod(ordersPaymentDTO.getPayMethod())
                .payStatus(Orders.PAID)
                .build();
        orderMapper.updateOrder(orders);
    }

    public void cancel(Long id) {
        Orders order = orderMapper.selectById(id);
        if (order == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        if (order.getStatus() > 2) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        if (order.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            //需要执行退款操作
            order.setPayStatus(Orders.REFUND);
        }
        order.setStatus(Orders.CANCELLED);
        order.setCancelReason("用户取消了订单");
        order.setCancelTime(LocalDateTime.now());
        orderMapper.updateOrder(order);
    }

    public OrderVO selectOrder(Long id) {
        Orders order = Orders.builder()
                .id(id).build();
        Page<OrderVO> orderVOS = orderMapper.selectOrders(order);
        if (orderVOS != null && orderVOS.size() == 1) {
            OrderVO orderVO = orderVOS.getFirst();
            List<OrderDetail> details = orderDetailMapper.selectOrderDetails(List.of(id));
            orderVO.setOrderDetailList(details);
            return orderVO;
        } else throw new BaseException("查询订单出错");
    }

    public PageResult<OrderVO> selectHistoryOrders(int pageNum, int pageSize, Integer status) {
        PageHelper.startPage(pageNum, pageSize);
        Orders order = Orders.builder()
                .userId(CurrentContext.getCurrent()).status(status).build();
        Page<OrderVO> page = orderMapper.selectOrders(order);
        List<OrderVO> result = page.getResult();
        List<Long> idList = result.stream().map(Orders::getId).toList();
        List<OrderDetail> details = orderDetailMapper.selectOrderDetails(idList);
        Map<Long, List<OrderDetail>> detailMap = details.stream()
                .collect(Collectors.groupingBy(OrderDetail::getOrderId));
        for (OrderVO orderVO : result) {
            orderVO.setOrderDetailList(detailMap.get(orderVO.getId()));
        }
        return new PageResult<>(page.getTotal(), result);
    }

    public void repetition(Long id) {
        Orders order = Orders.builder()
                .id(id).build();
        Page<OrderVO> orderVOS = orderMapper.selectOrders(order);
        List<OrderDetail> details;
        if (orderVOS != null && orderVOS.size() == 1) {
            details = orderVOS.getFirst().getOrderDetailList();
        } else throw new BaseException("查询订单出错");
        Long userId = CurrentContext.getCurrent();
        List<ShoppingCart> carts = details.stream().map(detail -> {
            ShoppingCart cart = new ShoppingCart();
            BeanUtils.copyProperties(detail, cart);
            cart.setUserId(userId);
            cart.setCreateTime(LocalDateTime.now());
            return cart;
        }).toList();
        shoppingCartMapper.addShoppingCarts(carts);
    }
}
