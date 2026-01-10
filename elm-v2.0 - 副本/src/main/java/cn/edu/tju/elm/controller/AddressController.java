package cn.edu.tju.elm.controller;

import cn.edu.tju.core.model.HttpResult;
import cn.edu.tju.core.security.service.UserService;
import cn.edu.tju.elm.model.DeliveryAddress;
//import cn.edu.tju.elb.service.AddressService;
import cn.edu.tju.elm.repository.AddressMapper;
import cn.edu.tju.elm.service.AddressService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(name="管理地址", description = "对配送地址的增删改查")
public class AddressController {

   @Autowired
   private AddressService addressService;

   @Autowired
   private AddressMapper addressMapper;

    @Autowired
    private UserService userService;

    @PostMapping("/addresses")
    public HttpResult<DeliveryAddress> addDeliveryAddress(@RequestBody DeliveryAddress deliveryAddress) {
        return addressService.addDeliveryAddress(deliveryAddress);
    }

    @GetMapping("/getaddresses")
    public HttpResult<List<DeliveryAddress>> listDeliveryAddressByUserId(){
        return addressService.listDeliveryAddressByUserId();
    }

    @PostMapping("/removeaddress")
    public HttpResult<DeliveryAddress> removeDeliveryAddress(@RequestBody Long id) {
        DeliveryAddress deliveryAddress=addressMapper.findById(id).orElseThrow();
        deliveryAddress.setDeleted(true);
        return addressService.addDeliveryAddress(deliveryAddress);
    }

//    @PostMapping("/updateaddress")
//    public HttpResult<DeliveryAddress> updateDeliveryAddress(@RequestBody DeliveryAddress deliveryAddress) {
//
//        return addressService.addDeliveryAddress(deliveryAddress);
//    }
}
