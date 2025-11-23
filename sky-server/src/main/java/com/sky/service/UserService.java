package com.sky.service;

import com.sky.constant.JwtClaimsConstant;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.UserLoginException;
import com.sky.mapper.UserMapper;
import com.sky.properties.JwtProperties;
import com.sky.utils.HttpClientUtil;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private HttpClientUtil httpClientUtil;
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private UserMapper userMapper;

    public UserLoginVO login(UserLoginDTO userLoginDTO) {
        String code = userLoginDTO.getCode();
        String openid = httpClientUtil.getOpenid(code);

        if (openid == null) {
            throw new UserLoginException(MessageConstant.LOGIN_FAILED);
        }

        User user = userMapper.selectByOpenid(openid);

        if (user == null) {
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.addUser(user);
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);
        return UserLoginVO.builder()
                .id(user.getId())
                .openid(user.getOpenid())
                .token(token)
                .build();
    }

}
