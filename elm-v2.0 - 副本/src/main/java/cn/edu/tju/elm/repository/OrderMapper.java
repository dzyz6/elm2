package cn.edu.tju.elm.repository;


import cn.edu.tju.elm.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderMapper extends JpaRepository<Order,Long> {
    // 根据用户ID查询订单（利用JPA方法命名规范）
    List<Order> findByCustomer_Id(Long customerId);
    // 可选：添加自定义查询方法（如按状态查询）
    @Query("SELECT o FROM Order o WHERE o.orderState = :status")
    List<Order> findByOrderState(@Param("status") Integer status);
}
