package cn.edu.tju.elm.service;

import cn.edu.tju.core.model.HttpResult;
import cn.edu.tju.core.model.ResultCodeEnum;
import cn.edu.tju.core.model.User;
import cn.edu.tju.core.security.UserModelDetailsService;
import cn.edu.tju.core.security.repository.PersonRepository;
import cn.edu.tju.core.security.repository.UserRepository;
import cn.edu.tju.core.security.service.UserService;
import cn.edu.tju.elm.model.Food;
import cn.edu.tju.elm.repository.FoodMapper;
import org.aspectj.bridge.IMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class FoodService {

    @Autowired
    FoodMapper foodMapper;

    private final UserService userService;

    public FoodService(UserService userService) {
        this.userService = userService;
    }

    // 获取所有商品
    public HttpResult<List<Food>> getAllFoods(Long businessId, Long orderId) {
        // 处理参数：如果两个参数都为空，返回所有食品
        List<Food> foods = null;
        if (businessId == null) {
            foods = foodMapper.findAll();
        }

        // 根据参数查询
        else {
            foods = foodMapper.findByBusiness_Id(businessId);
        }

        // 检查是否查询到结果
        if (foods.isEmpty()) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND, "未找到相关食品");
        }

        return HttpResult.success(foods);
    }

    // 根据ID获取商品
    public HttpResult<Optional<Food>> getFoodById(long id) {
        // 查询单个食品（返回 Optional）
        Optional<Food> foodOptional = foodMapper.findById(id);

        // 检查是否找到
        if (foodOptional.isEmpty()) {
            return HttpResult.failure(ResultCodeEnum.NOT_FOUND, "未找到商品"); // 假设定义了一个“未找到”的错误码
        }

        // 如果找到，返回成功结果
        return HttpResult.success(foodOptional);
    }

    //添加商品
    public HttpResult<Food> addFood(@RequestBody Food food){
        User user = userService.getUserWithAuthorities().get();
        LocalDateTime now = LocalDateTime.now();
        food.setCreator(user.getId());
        food.setCreateTime(now);
        return HttpResult.success(foodMapper.save(food));

    }

//    @Transactional
//    public void updateFood(Long id, FoodUpdateDTO dto) {
//        // 1. 权限验证
//        User user = userService.getUserWithAuthorities();
//        if (!hasPermission(user)) {
//            throw new AccessDeniedException("无修改权限");
//        }
//
//        // 2. 执行更新（动态更新非空字段）
//        foodMapper.updateSelective(
//                id,
//                dto.getName(),
//                dto.getPrice(),
//                dto.getExplain(),
//                user.getId()
//        );
//    }
//
//    private boolean hasPermission(User user) {
//        // 权限验证逻辑（示例：管理员或商品创建者）
//        return user.getAuthorities().stream()
//                .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()))
//                || foodMapper.findByIdWithBusiness(id)
//                .map(f -> f.getCreator().equals(user.getId()))
//                .orElse(false);
//    }
}
