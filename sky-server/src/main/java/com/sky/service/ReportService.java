package com.sky.service;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.SelectDate;
import com.sky.entity.SelectDateResult;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

@Service
public class ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;

    public TurnoverReportVO selectTurnover(LocalDate begin, LocalDate end) {
        List<SelectDate> list = new ArrayList<>();
        while (!begin.equals(end)) {
            list.add(new SelectDate(begin, begin.plusDays(1)));
            begin = begin.plusDays(1);
        }
        list.add(new SelectDate(begin, begin.plusDays(1)));
        Map<LocalDate, SelectDateResult> map = orderMapper.selectTurnover(list);
        StringJoiner dateString = new StringJoiner(",");
        StringJoiner turnoverString = new StringJoiner(",");
        for (SelectDate selectDate : list) {
            LocalDate localDate = selectDate.getBeginDate();
            SelectDateResult selectDateResult = map.get(localDate);
            double turnover = selectDateResult == null ? 0.0 : selectDateResult.getAmount();
            dateString.add(localDate.toString());
            turnoverString.add(Double.toString(turnover));
        }
        return new TurnoverReportVO(dateString.toString(), turnoverString.toString());
    }

    public UserReportVO selectUser(LocalDate begin, LocalDate end) {
        LocalDate beginDate = begin;
        List<SelectDate> list = new ArrayList<>();
        while (!begin.equals(end)) {
            list.add(new SelectDate(begin, begin.plusDays(1)));
            begin = begin.plusDays(1);
        }
        list.add(new SelectDate(begin, begin.plusDays(1)));
        Map<LocalDate, SelectDateResult> map = userMapper.selectUserOneDate(list, beginDate);
        StringJoiner dateString = new StringJoiner(",");
        StringJoiner newUserString = new StringJoiner(",");
        StringJoiner totalUserString = new StringJoiner(",");
        int totalUser = 0;
        SelectDateResult oldUser = map.get(LocalDate.parse("1970-01-01"));
        totalUser += oldUser == null ? 0 : oldUser.getAmount().intValue();
        for (SelectDate selectDate : list) {
            LocalDate localDate = selectDate.getBeginDate();
            SelectDateResult selectDateResult = map.get(localDate);
            int newUser = selectDateResult == null ? 0 : selectDateResult.getAmount().intValue();
            totalUser += newUser;
            dateString.add(localDate.toString());
            newUserString.add(String.valueOf(newUser));
            totalUserString.add(String.valueOf(totalUser));
        }
        return new UserReportVO(dateString.toString(), totalUserString.toString(), newUserString.toString());
    }

    public OrderReportVO selectOrder(LocalDate begin, LocalDate end) {
        List<SelectDate> list = new ArrayList<>();
        while (!begin.equals(end)) {
            list.add(new SelectDate(begin, begin.plusDays(1)));
            begin = begin.plusDays(1);
        }
        list.add(new SelectDate(begin, begin.plusDays(1)));
        Map<LocalDate, SelectDateResult> mapAll = orderMapper.selectOrderCount(list);
        Map<LocalDate, SelectDateResult> mapValid = orderMapper.selectOrderValid(list);
        StringJoiner dateString = new StringJoiner(",");
        StringJoiner orderCountString = new StringJoiner(",");
        StringJoiner validOrderCountString = new StringJoiner(",");
        int totalOrderCount = 0;
        int totalValidOrderCount = 0;
        for (SelectDate selectDate : list) {
            LocalDate localDate = selectDate.getBeginDate();
            SelectDateResult count = mapAll.get(localDate);
            int orderCount = count == null ? 0 : count.getAmount().intValue();
            totalOrderCount += orderCount;
            SelectDateResult validCount = mapValid.get(localDate);
            int validOrderCount = validCount == null ? 0 : validCount.getAmount().intValue();
            totalValidOrderCount += validOrderCount;
            dateString.add(localDate.toString());
            orderCountString.add(String.valueOf(orderCount));
            validOrderCountString.add(String.valueOf(validOrderCount));
        }
        double orderCompletionRate = totalOrderCount == 0 ?
                0.0 : (double) totalValidOrderCount / totalOrderCount;
        return new OrderReportVO(dateString.toString(),
                orderCountString.toString(),
                validOrderCountString.toString(),
                totalOrderCount,
                totalValidOrderCount,
                orderCompletionRate);
    }

    public SalesTop10ReportVO SalesTop10Statistics(LocalDate begin, LocalDate end) {
        LocalDate endDate = end.plusDays(1);
        List<GoodsSalesDTO> top10 = orderDetailMapper.selectTop10(begin, endDate);
        StringJoiner nameString = new StringJoiner(",");
        StringJoiner numberString = new StringJoiner(",");
        for (GoodsSalesDTO goodsSalesDTO : top10) {
            nameString.add(goodsSalesDTO.getName());
            numberString.add(goodsSalesDTO.getNumber().toString());
        }
        return new SalesTop10ReportVO(nameString.toString(), numberString.toString());
    }
}
