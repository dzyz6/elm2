package cn.edu.tju.elm.service;

import cn.edu.tju.core.model.HttpResult;
import cn.edu.tju.core.model.User;
import cn.edu.tju.core.security.SecurityUtils;
import cn.edu.tju.core.security.service.UserService;
import cn.edu.tju.elm.model.Business;
import cn.edu.tju.elm.repository.BusinessMappper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;

@Service
public class BusinessService {

    @Autowired
    BusinessMappper businessMappper;

    private final UserService userService;

    public BusinessService(UserService userService) {
        this.userService = userService;
    }


    public HttpResult<Business> addBusiness(@RequestBody Business business){
        User user = userService.getUserWithAuthorities().get();
        LocalDateTime now = LocalDateTime.now();
        business.setBusinessOwner(user);
        business.setBusinessImg("string");
        business.setCreator(user.getId());
        business.setCreateTime(now);
        business.setUpdater(user.getId());
        business.setUpdateTime(now);
        business.setDeleted(false);
        return HttpResult.success(businessMappper.save(business));
    }


}
