package cn.edu.tju.elm.controller;

import cn.edu.tju.core.model.ResultCodeEnum;
import cn.edu.tju.elm.model.Food;
import cn.edu.tju.core.model.HttpResult;
//import cn.edu.tju.elb.service.BusinessService;
//import cn.edu.tju.elb.service.FoodService;
import cn.edu.tju.core.security.service.UserService;
import cn.edu.tju.elm.repository.FoodMapper;
import cn.edu.tju.elm.service.BusinessService;
import cn.edu.tju.elm.service.FoodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.*;

@RestController
@RequestMapping("/api/foods")
@Tag(name = "管理商品")
public class FoodController {
    @Autowired
    private UserService userService;

   @Autowired
   private FoodService foodService;

   @Autowired
   private BusinessService businessService;

    @GetMapping("/{id}")
    @Operation(summary = "返回查询到的一条商品记录", method = "GET")
    public HttpResult<Food> getFoodById(@PathVariable Long id){
        // 1. 参数校验
        if (id == null || id <= 0) {
            return HttpResult.failure(ResultCodeEnum.BAD_REQUEST, "ID参数无效");
        }

        // 2. 调用Service并解包HttpResult
        HttpResult<Optional<Food>> serviceResult = foodService.getFoodById(id);

        // 3. 处理Service层返回结果
        if (!serviceResult.getSuccess()) {
            // 直接透传Service层的错误码和消息
            return HttpResult.failure(
                    serviceResult.getCode(),
                    serviceResult.getMessage()
            );
        }

        // 4. 处理Optional数据
        Optional<Food> foodOptional = serviceResult.getData();
        // 成功返回Food对象
        return foodOptional.map(HttpResult::success).
                orElseGet(() -> HttpResult.failure(ResultCodeEnum.NOT_FOUND, "商品不存在"));

    }

    @GetMapping("")
    public HttpResult<List<Food>> getAllFoods(@RequestParam(name = "business", required = false) Long businessId,
                                              @RequestParam(name = "order", required = false) Long orderId){
        return foodService.getAllFoods(businessId, orderId);
    }

    @PostMapping("")
    public HttpResult<Food> addFood(@RequestBody Food food) {

        return foodService.addFood(food);
    }

//    @PutMapping("/{id}")
//    @PreAuthorize("isAuthenticated()")
//    public ResponseEntity<Void> updateFood(
//            @PathVariable Long id,
//            @Valid @RequestBody FoodUpdateDTO dto
//    ) {
//        foodService.updateFood(id, dto);
//        return ResponseEntity.noContent().build();
//    }
}
