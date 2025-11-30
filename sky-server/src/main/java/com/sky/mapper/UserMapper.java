package com.sky.mapper;

import com.sky.entity.SelectDate;
import com.sky.entity.SelectDateResult;
import com.sky.entity.User;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface UserMapper {

    @Select("select * from user where openid = #{openid}")
    User selectByOpenid(String openid);

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into user (openid, name, phone, sex, id_number, avatar, create_time) " +
            "values (#{openid}, #{name}, #{phone}, #{sex}, #{idNumber}, #{avatar}, #{createTime})")
    void addUser(User user);

    @MapKey("date")
    Map<LocalDate, SelectDateResult> selectUserOneDate(List<SelectDate> list, LocalDate begin);

    @Select("select count(*) from user where create_time >= #{begin} and create_time < #{end}")
    Integer selectToday(LocalDate begin, LocalDate end);
}
