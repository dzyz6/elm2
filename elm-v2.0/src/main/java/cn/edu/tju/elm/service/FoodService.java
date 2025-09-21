package cn.edu.tju.elm.service;

import cn.edu.tju.core.model.HttpResult;
import cn.edu.tju.core.model.User;
import cn.edu.tju.core.security.UserModelDetailsService;
import cn.edu.tju.core.security.repository.PersonRepository;
import cn.edu.tju.core.security.repository.UserRepository;
import cn.edu.tju.core.security.service.UserService;
import cn.edu.tju.elm.model.Food;
import cn.edu.tju.elm.repository.FoodMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;

@Service
public class FoodService {

    @Autowired
    FoodMapper foodMapper;

    private final UserService userService;

    public FoodService(UserService userService) {
        this.userService = userService;
    }

    public HttpResult<Food> addFood(@RequestBody Food food){
        User user = userService.getUserWithAuthorities().get();
        LocalDateTime now = LocalDateTime.now();
        return HttpResult.success(foodMapper.save(food));
    }
}
