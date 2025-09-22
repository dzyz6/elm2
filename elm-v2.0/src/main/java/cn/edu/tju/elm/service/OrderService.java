package cn.edu.tju.elm.service;

import cn.edu.tju.core.model.HttpResult;
import cn.edu.tju.core.model.ResultCodeEnum;
import cn.edu.tju.core.model.User;
import cn.edu.tju.core.security.service.UserService;
import cn.edu.tju.elm.model.Food;
import cn.edu.tju.elm.model.Order;
import cn.edu.tju.elm.repository.FoodMapper;
import cn.edu.tju.elm.repository.OrderMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    OrderMapper orderMapper;

    private final UserService userService;

    public OrderService(UserService userService) {
        this.userService = userService;
    }


//    public HttpResult<Order> addOrders(@RequestBody Order order) {
//        User user = userService.getUserWithAuthorities().get();
//        LocalDateTime now = LocalDateTime.now();
//        order.setOrderTotal(BigDecimal.valueOf(10));
//        order.setCustomer(user);
//        return HttpResult.success(orderMapper.save(order));
//    }

    public HttpResult<Order> addOrders(Order order) {
        // 获取当前用户（需确保用户已认证）
        User user = userService.getUserWithAuthorities()
                .orElseThrow(() -> new RuntimeException("用户未登录"));

        // 设置订单默认值
        order.setOrderTotal(BigDecimal.TEN);
        order.setCustomer(user);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderState(1); // 1-待处理

        // 保存并返回结果
        Order savedOrder = orderMapper.save(order);
        return HttpResult.success(savedOrder);
    }

    public HttpResult<Order> getOrderById(Long id) {
        // 1. 获取当前认证用户（Spring Security自动管理）
        User currentUser = getCurrentUser();

        // 精确查询订单
        Order order = orderMapper.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("订单不存在"));

        return HttpResult.success(order);
    }

    public HttpResult<List<Order>> listOrdersByUserId(Long userId) {
        // 1. 获取当前认证用户
        User currentUser = getCurrentUser();
        Long currentUserId = currentUser.getId();
        if(!currentUserId.equals(userId)) {
//            return HttpResult.failure("403","权限不足");
            throw new AccessDeniedException("无权查看他人订单");
        }
        // 根据用户ID查询订单
        List<Order> orders = orderMapper.findByCustomer_Id(userId);

        return HttpResult.success(orders);
    }

    // 统一获取当前用户方法
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("用户未认证");
        }
        return (User) authentication.getPrincipal();
    }

}
