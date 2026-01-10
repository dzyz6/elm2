package cn.edu.tju.elm.service;

import cn.edu.tju.core.model.HttpResult;
import cn.edu.tju.core.model.User;
import cn.edu.tju.core.security.SecurityUtils;
import cn.edu.tju.core.security.service.UserService;
import cn.edu.tju.elm.model.DeliveryAddress;
import cn.edu.tju.elm.repository.AddressMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AddressService {

    @Autowired
    private AddressMapper addressMapper;

    private final UserService userService;

    public AddressService(UserService userService) {
        this.userService = userService;
    }

    public HttpResult<DeliveryAddress> addDeliveryAddress(DeliveryAddress deliveryAddress) {
        User user = userService.getUserWithAuthorities().get();
        deliveryAddress.setCustomer(user);
        return HttpResult.success(addressMapper.save(deliveryAddress));
    }

    public HttpResult<List<DeliveryAddress>> listDeliveryAddressByUserId(){
        User user = userService.getUserWithAuthorities().get();
        return HttpResult.success(addressMapper.findByCustomerId(user.getId()));
    }
}
