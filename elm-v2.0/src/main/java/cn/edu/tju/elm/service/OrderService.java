package cn.edu.tju.elm.service;

import cn.edu.tju.core.model.HttpResult;
import cn.edu.tju.core.model.User;
import cn.edu.tju.core.security.service.UserService;
import cn.edu.tju.elm.model.Food;
import cn.edu.tju.elm.model.Order;
import cn.edu.tju.elm.repository.FoodMapper;
import cn.edu.tju.elm.repository.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class OrderService {

    @Autowired
    OrderMapper orderMapper;

    private final UserService userService;

    public OrderService(UserService userService) {
        this.userService = userService;
    }


    public HttpResult<Order> addOrders(@RequestBody Order order) {
        User user = userService.getUserWithAuthorities().get();
        LocalDateTime now = LocalDateTime.now();
        order.setOrderTotal(BigDecimal.valueOf(10));
        order.setCustomer(user);
        return HttpResult.success(orderMapper.save(order));
    }
}
