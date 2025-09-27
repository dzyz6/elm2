package cn.edu.tju.elm.service;

import cn.edu.tju.core.model.HttpResult;
import cn.edu.tju.core.model.User;
import cn.edu.tju.core.security.repository.UserRepository;
import cn.edu.tju.core.security.service.UserService;
import cn.edu.tju.elm.model.Business;
import cn.edu.tju.elm.model.Cart;
import cn.edu.tju.elm.model.CartDTO;
import cn.edu.tju.elm.model.Food;
import cn.edu.tju.elm.repository.BusinessMapper;
import cn.edu.tju.elm.repository.CartMapper;
import cn.edu.tju.elm.repository.FoodMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Date;
import java.util.List;

@Service
public class CartService {
    @Autowired
    CartMapper cartMapper;

    @Autowired
    UserRepository userMapper;

    @Autowired
    FoodMapper foodMapper;

    @Autowired
    BusinessMapper businessMapper;

    private final UserService userService;

    private final BusinessService businessService;

    public CartService(BusinessService businessService,UserService userService) {
        this.userService = userService;
        this.businessService = businessService;
    }

    public HttpResult<Cart> addCartItem(CartDTO cartDTO){
        User customer = userMapper.findById(cartDTO.getCustomerId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        Business business = businessMapper.findById(cartDTO.getBusinessId())
                .orElseThrow(() -> new RuntimeException("商家不存在"));

        Food food = foodMapper.findById(cartDTO.getFoodId())
                .orElseThrow(() -> new RuntimeException("食品不存在"));

        // 3. 创建Cart实体并设置关联对象
        Cart cart = new Cart();
        cart.setCustomer(customer);  // 使用查询到的托管实体
        cart.setBusiness(business);
        cart.setFood(food);

        // 4. 设置其他字段
        cart.setQuantity(cartDTO.getQuantity() != null ? cartDTO.getQuantity() : 1);
        cart.setDeleted(false);

        // 5. 保存到数据库
        Cart savedCart = cartMapper.save(cart);
        return HttpResult.success(savedCart);
    }

    public HttpResult<Cart> addCartItem2(Cart cart){
        User user = userService.getUserWithAuthorities().get();
        Food food=foodMapper.findById(cart.getFood().getId()).orElseThrow();
        Business business=businessMapper.findById(food.getBusiness().getId()).orElseThrow();
        cart.setBusiness(business);
        cart.setCustomer(user);
        cart.setFood(food);
        return HttpResult.success(cartMapper.save(cart));
    }

    public HttpResult<Cart>  updateCartItem(Cart cart){
        User user = userService.getUserWithAuthorities().get();
        Food food=foodMapper.findById(cart.getFood().getId()).orElseThrow();
        Business business=businessMapper.findById(food.getBusiness().getId()).orElseThrow();
        cart.setBusiness(business);
        cart.setCustomer(user);
        cart.setFood(food);
        return  HttpResult.success(cartMapper.save(cart));
    }

    public HttpResult<Cart> deleteCartItem(Cart cart){
        User user = userService.getUserWithAuthorities().get();
        Food food=foodMapper.findById(cart.getFood().getId()).orElseThrow();
        Business business=businessMapper.findById(food.getBusiness().getId()).orElseThrow();
        cart.setBusiness(business);
        cart.setCustomer(user);
        cart.setFood(food);
        cart.setDeleted(true);
        return HttpResult.success(cartMapper.save(cart));
    }


    public HttpResult<List<Cart>>  cartItemList(Long businessId){
        User user = userService.getUserWithAuthorities().get();
        System.out.println("用户ID：" + user.getId());
        System.out.println("商家ID：" + businessId);
        List<Cart> carts = cartMapper.findByCustomerIdAndBusinessIdNative(user.getId(), businessId);
        System.out.println("????");
        return HttpResult.success(cartMapper.findByCustomerIdAndBusinessIdNative(user.getId(), businessId));
    }
}
