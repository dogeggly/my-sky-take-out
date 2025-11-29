package com.sky.service;

import com.sky.entity.SelectDate;
import com.sky.entity.SelectDateResult;
import com.sky.mapper.OrderMapper;
import com.sky.vo.TurnoverReportVO;
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

    public TurnoverReportVO selectTurnover(LocalDate begin, LocalDate end) {
        List<SelectDate> list = new ArrayList<>();
        while (!begin.equals(end)) {
            list.add(new SelectDate(begin, begin.plusDays(1)));
            begin = begin.plusDays(1);
        }
        list.add(new SelectDate(begin, begin.plusDays(1)));
        Map<String, SelectDateResult> map = orderMapper.selectTurnover(list);
        StringJoiner dateString = new StringJoiner(",");
        StringJoiner turnoverString = new StringJoiner(",");
        for (SelectDate selectDate : list) {
            LocalDate localDate = selectDate.getBeginDate();
            SelectDateResult selectDateResult = map.get(localDate.toString());
            double turnover = selectDateResult == null ? 0.0 : selectDateResult.getAmount();
            dateString.add(localDate.toString());
            turnoverString.add(String.valueOf(turnover));
        }
        return new TurnoverReportVO(dateString.toString(), turnoverString.toString());
    }
}
