package cn.edu.tju.elm.repository;


import cn.edu.tju.elm.model.Cart;
import cn.edu.tju.elm.model.Food;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartMapper extends JpaRepository<Cart,Long> {

    // 使用原生SQL查询，明确指定数据库字段名（注意使用大写匹配你的数据库）
    @Query(value = "SELECT * FROM cart WHERE CUSTOMER_ID = :userId AND BUSINESS_ID = :businessId", nativeQuery = true)
    List<Cart> findByCustomerIdAndBusinessIdNative(@Param("userId") Long userId, @Param("businessId") Long businessId);
}
