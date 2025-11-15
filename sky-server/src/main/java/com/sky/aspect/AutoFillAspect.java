package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.CurrentContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill))")
    public void autoFillPointcut() {
    }

    @Before("autoFillPointcut()")
    public void autoFill(JoinPoint joinPoint) throws Exception {

        log.info("开始进行数据填充");

        //获取签名，并向下转型为方法签名
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        //获取方法
        Method method = methodSignature.getMethod();
        //获取方法上的注解
        AutoFill autoFill = method.getAnnotation(AutoFill.class);
        //获取注解中的属性，即枚举
        OperationType value = autoFill.value();

        //获取参数
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            throw new Exception();
        }
        Object entity = args[0];

        if (value == OperationType.INSERT) {
            Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
            Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
            Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
            Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

            setCreateTime.invoke(entity, LocalDateTime.now());
            setUpdateTime.invoke(entity, LocalDateTime.now());
            setCreateUser.invoke(entity, CurrentContext.getCurrent());
            setUpdateUser.invoke(entity, CurrentContext.getCurrent());
        } else if (value == OperationType.UPDATE) {
            Method setUpdateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
            Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

            setUpdateTime.invoke(entity, LocalDateTime.now());
            setUpdateUser.invoke(entity, CurrentContext.getCurrent());
        } else {
            throw new Exception();
        }
    }

}
