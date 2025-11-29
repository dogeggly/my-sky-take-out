package com.sky.mapper;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface OrderDetailMapper {
    void addOrderDetails(List<OrderDetail> orderDetails);

    List<OrderDetail> selectOrderDetails(List<Long> orderIdList);

    List<GoodsSalesDTO> selectTop10(LocalDate begin, LocalDate end);
}
