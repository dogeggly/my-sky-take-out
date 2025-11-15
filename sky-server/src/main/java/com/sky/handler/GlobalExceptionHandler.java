package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.DataTruncation;
import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 捕获业务异常
     *
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex) {
        log.error("自定义异常：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    @ExceptionHandler
    public Result handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        // 获取底层被包装的原生异常
        Throwable cause = ex.getCause();
        // 如果底层是 SQLIntegrityConstraintViolationException（如主键/唯一键冲突）
        if (cause instanceof SQLIntegrityConstraintViolationException sqlEx) {
            String message = sqlEx.getMessage();
            log.error("SQL完整性约束异常：{}", message);
            if (message.contains("Duplicate entry")) {
                String[] split = message.split(" ");
                if (split.length > 3) {
                    message = split[2] + MessageConstant.AlREADY_EXIST;
                    return Result.error(message);
                }
            }
            log.error("数据库未知异常: {}", cause.getMessage());
            return Result.error(MessageConstant.DATABASE_UNKNOWN_ERROR);
        }
        // 如果底层是 DataTruncation（数据截断，如字符串超长）
        if (cause instanceof DataTruncation truncationEx) {
            String message = truncationEx.getMessage();
            log.error("数据截断异常：{}", message);
            if (message.contains("Data too long")) {
                String[] split = message.split(" ");
                if (split.length > 8) {
                    message = split[7] + MessageConstant.OUT_OF_RANGE;
                    return Result.error(message);
                }
            }
            log.error("数据库未知异常: {}", cause.getMessage());
            return Result.error(MessageConstant.DATABASE_UNKNOWN_ERROR);
        }
        // 其他未识别的完整性异常
        log.error("数据库未知异常: {}", cause.getMessage());
        return Result.error(MessageConstant.DATABASE_UNKNOWN_ERROR);
    }

    @ExceptionHandler
    public Result exceptionHandler(Exception ex) {
        log.error("未知异常：{}", ex.getMessage());
        return Result.error(MessageConstant.UNKNOWN_ERROR);
    }
}
