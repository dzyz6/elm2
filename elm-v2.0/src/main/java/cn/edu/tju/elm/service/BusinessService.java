package cn.edu.tju.elm.service;

import cn.edu.tju.core.model.HttpResult;
import cn.edu.tju.core.model.User;
import cn.edu.tju.core.security.service.UserService;
import cn.edu.tju.elm.model.Business;
import cn.edu.tju.elm.repository.BusinessMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BusinessService {

    @Autowired
    BusinessMapper businessMapper;

    private final UserService userService;

    public BusinessService(UserService userService) {
        this.userService = userService;
    }


    public HttpResult<Business> addBusiness(Business business){
        User user = userService.getUserWithAuthorities().get();
        LocalDateTime now = LocalDateTime.now();
        business.setBusinessOwner(user);
        business.setCreator(user.getId());
        business.setCreateTime(now);
        business.setUpdater(user.getId());
        business.setUpdateTime(now);
        business.setDeleted(false);
        return HttpResult.success(businessMapper.save(business));
    }

    public HttpResult<List<Business>> getBusinesses(){
        return HttpResult.success(businessMapper.findByDeletedFalse());
    }

    public Optional<Business> findByIdAndDeletedFalse(long id){
        return businessMapper.findByIdAndDeletedFalse(id);
    }

    public HttpResult<Business> getBusiness(@PathVariable("id") Long id){
        Optional<Business> businessOptional = businessMapper.findById(id);

        // 从Optional中获取Business对象，如果不存在则返回错误结果
        return businessOptional.map(HttpResult::success)
                .orElseGet(() -> HttpResult.failure("200","业务数据不存在，ID: " + id));
    }

    public HttpResult<Business> updateBusiness(@PathVariable("id") Long id, @RequestBody Business business){
        Optional<Business> businessOptional = businessMapper.findById(id);
        return businessOptional.map(HttpResult::success)
                .orElseGet(() -> HttpResult.failure("200","业务数据不存在，ID: " + id));
    }


}
