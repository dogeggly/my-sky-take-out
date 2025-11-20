package com.sky.service;

import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.UserLoginException;
import com.sky.mapper.UserMapper;
import com.sky.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {

    @Autowired
    private HttpClientUtil httpClientUtil;
    @Autowired
    private UserMapper userMapper;

    public User login(UserLoginDTO userLoginDTO) {
        String code = userLoginDTO.getCode();
        String openid = httpClientUtil.getOpenid(code);

        if (openid == null) {
            throw new UserLoginException(MessageConstant.LOGIN_FAILED);
        }

        User user = userMapper.selectByOpenid(openid);

        if (user == null) {
            User newUser = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.addUser(newUser);
            return newUser;
        } else return user;
    }

}
