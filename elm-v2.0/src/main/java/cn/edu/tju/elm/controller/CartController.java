package cn.edu.tju.elm.controller;

import cn.edu.tju.elm.model.Cart;
//import cn.edu.tju.elb.service.CartItemService;
import cn.edu.tju.core.model.HttpResult;
import cn.edu.tju.core.security.service.UserService;
import cn.edu.tju.elm.model.CartDTO;
import cn.edu.tju.elm.service.CartItemService;
import cn.edu.tju.elm.service.CartService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name = "管理购物车", description = "对购物车内的商品增删改查")
public class CartController {
    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @PostMapping("/carts1")
    public HttpResult<Cart>  addCartItem(@RequestBody CartDTO cartDTO){
        return cartService.addCartItem(cartDTO);
    }

    @PostMapping("/carts")
    public HttpResult<Cart>  addCartItem(@RequestBody Cart cart){
        return cartService.addCartItem2(cart);
    }

    @PostMapping("/updatecarts")
    public HttpResult<Cart>  updateCartItem(@RequestBody Cart cart){
        return cartService.updateCartItem(cart);
    }

    @PostMapping("/deletecarts")
    public HttpResult<Cart>  deleteCartItem(@RequestBody Cart cart){
        return cartService.deleteCartItem(cart);
    }

    @GetMapping("/cartlist")
    public HttpResult<List<Cart>>  cartItemList(@RequestParam Long businessId){
        return cartService.cartItemList(businessId);
    }
}
