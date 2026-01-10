package cn.edu.tju.elm.exception; // 专用异常处理包

import cn.edu.tju.core.model.HttpResult;
import cn.edu.tju.core.model.ResultCodeEnum;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.HttpStatus;

@ControllerAdvice(basePackages = "cn.edu.tju.elm.controller") // 指定扫描范围
public class GlobalExceptionHandler {

    // 处理权限校验异常
    @ExceptionHandler(AccessDeniedException.class)
    public HttpResult<Void> handleAccessDenied() {
        return HttpResult.failure("403", "无访问权限");
    }

    // 处理资源不存在异常
    @ExceptionHandler(EntityNotFoundException.class)
    public HttpResult<Void> handleNotFound() {
        return HttpResult.failure(ResultCodeEnum.NOT_FOUND, "资源不存在");
    }

    // 通用异常处理
    @ExceptionHandler(Exception.class)
    public HttpResult<Void> handleException(Exception e) {
        return HttpResult.failure(ResultCodeEnum.BAD_REQUEST, "校验失败");
    }
}