package com.sky.service;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.SelectDate;
import com.sky.entity.SelectDateResult;
import com.sky.exception.BaseException;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.vo.*;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
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
    @Autowired
    private WorkspaceService workspaceService;

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

    public void export(HttpServletResponse response) throws IOException {
        LocalDate end = LocalDate.now();
        LocalDate begin = end.plusDays(-30);
        BusinessDataVO vo = workspaceService.getBusinessData(begin, end);

        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");

        if (in == null) {
            throw new BaseException("模板文件未找到");
        }
        XSSFWorkbook excel = new XSSFWorkbook(in);
        XSSFSheet sheet = excel.getSheet("Sheet1");
        sheet.getRow(1).getCell(1).setCellValue("时间：" + begin + " 至 " + end.plusDays(-1));
        sheet.getRow(3).getCell(2).setCellValue(vo.getTurnover());
        sheet.getRow(3).getCell(4).setCellValue(vo.getOrderCompletionRate());
        sheet.getRow(3).getCell(6).setCellValue(vo.getNewUsers());
        sheet.getRow(4).getCell(2).setCellValue(vo.getValidOrderCount());
        sheet.getRow(4).getCell(4).setCellValue(vo.getUnitPrice());

        List<SelectDate> list = new ArrayList<>();
        LocalDate beginDate = begin;
        while (!begin.equals(end)) {
            list.add(new SelectDate(begin, begin.plusDays(1)));
            begin = begin.plusDays(1);
        }
        Map<LocalDate, SelectDateResult> mapTurnover = orderMapper.selectTurnover(list);
        Map<LocalDate, SelectDateResult> mapAll = orderMapper.selectOrderCount(list);
        Map<LocalDate, SelectDateResult> mapValid = orderMapper.selectOrderValid(list);
        Map<LocalDate, SelectDateResult> mapUser = userMapper.selectUserOneDate(list, beginDate);
        for (int i = 0; i < list.size(); i++) {
            SelectDate selectDate = list.get(i);
            LocalDate localDate = selectDate.getBeginDate();
            SelectDateResult resultTurnover = mapTurnover.get(localDate);
            SelectDateResult resultAll = mapAll.get(localDate);
            SelectDateResult resultValid = mapValid.get(localDate);
            SelectDateResult resultUser = mapUser.get(localDate);
            double turnover = resultTurnover == null ? 0.0 : resultTurnover.getAmount();
            int orderCount = resultAll == null ? 0 : resultAll.getAmount().intValue();
            int validOrderCount = resultValid == null ? 0 : resultValid.getAmount().intValue();
            int newUsers = resultUser == null ? 0 : resultUser.getAmount().intValue();
            double unitPrice = validOrderCount == 0 ? 0.0 : turnover / validOrderCount;
            double orderCompletionRate = orderCount == 0 ? 0.0 : (double) validOrderCount / orderCount;
            sheet.getRow(i + 7).getCell(1).setCellValue(localDate.toString());
            sheet.getRow(i + 7).getCell(2).setCellValue(turnover);
            sheet.getRow(i + 7).getCell(3).setCellValue(validOrderCount);
            sheet.getRow(i + 7).getCell(4).setCellValue(orderCompletionRate);
            sheet.getRow(i + 7).getCell(5).setCellValue(unitPrice);
            sheet.getRow(i + 7).getCell(6).setCellValue(newUsers);
        }

        ServletOutputStream out = response.getOutputStream();
        excel.write(out);

    }
}
