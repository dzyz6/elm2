package cn.edu.tju.elm.controller;

import cn.edu.tju.core.model.User;
import cn.edu.tju.elm.model.Business;
import cn.edu.tju.core.model.HttpResult;
//import cn.edu.tju.elb.service.BusinessService;
import cn.edu.tju.core.security.service.UserService;
import cn.edu.tju.elm.repository.BusinessMapper;
import cn.edu.tju.elm.service.BusinessService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/businesses")
@Tag(name="管理店铺", description = "提供对店铺的增删改查功能")
public class BusinessController {
    @Autowired
    private UserService userService;

   @Autowired
   BusinessService businessService;

   @Autowired
    BusinessMapper businessMapper;

    @GetMapping("")
    public HttpResult<List<Business>> getBusinesses(){
        return businessService.getBusinesses();
    }

    @PostMapping("")
    @PreAuthorize("hasAuthority('ADMIN')")
    public HttpResult<Business> addBusiness(@RequestBody Business business){
        return businessService.addBusiness(business);
    }

    @GetMapping("/{id}")
    public HttpResult<Business> getBusiness(@PathVariable("id") Long id){
        return businessService.getBusiness(id);
    }

    

    @PutMapping("/{id}")
    public HttpResult<Business> updateBusiness(@PathVariable("id") Long id, @RequestBody Business business){
        return null;
    }

    @PatchMapping("/{id}")
    public HttpResult<Business> patchBusiness(@PathVariable("id") Long id, @RequestBody Business business){
        return null;
    }

    @DeleteMapping("/{id}")
    public HttpResult<Business> deleteBusiness(@PathVariable("id") Long id){
        Optional<Business> existingOpt = businessService.findByIdAndDeletedFalse(id);
        if (!existingOpt.isPresent()) {
            return HttpResult.failure("200","商户不存在或已被删除");
        }

        User currentUser = userService.getUserWithAuthorities().get();
        Business existing = existingOpt.get();

        // 逻辑删除：设置deleted=true，而非物理删除
        existing.setDeleted(true);
        existing.setUpdater(currentUser.getId());
        existing.setUpdateTime(LocalDateTime.now());

        // 保存更新后的对象，并接收返回的更新后实例
        Business deletedBusiness = businessMapper.save(existing);

        // 返回更新后的Business对象（符合success方法对Business类型的要求）
        return HttpResult.success(deletedBusiness);
    }
}
