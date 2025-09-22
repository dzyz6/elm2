package cn.edu.tju.elm.repository;


import cn.edu.tju.core.security.UserModelDetailsService;
import cn.edu.tju.elm.model.Food;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface FoodMapper extends JpaRepository<Food,Long> {
    List<Food> findAll();
    // 新增按商家ID查询（注意关联属性路径）
    List<Food> findByBusiness_Id(Long businessId);
    Optional<Food> findById(long id);

    List<Food> deleteById(long id);

    // 自定义更新方法（动态更新非空字段）
//    @Modifying
//    @Query("UPDATE Food f SET " +
//            "f.foodName = COALESCE(:name, f.foodName), " +
//            "f.foodPrice = COALESCE(:price, f.foodPrice), " +
//            "f.foodExplain = COALESCE(:explain, f.foodExplain), " +
//            "f.updateTime = CURRENT_TIMESTAMP, " +
//            "f.updater = COALESCE(:updater, f.updater) " +
//            "WHERE f.id = :id")
//    void updateSelective(
//            @Param("id") Long id,
//            @Param("name") String name,
//            @Param("price") BigDecimal price,
//            @Param("explain") String explain,
//            @Param("updater") Long updater
//    );
//
//    // 查找商品（包含关联业务实体）
//    @EntityGraph(attributePaths = {"business"})
//    @Query("SELECT f FROM Food f WHERE f.id = :id")
//    Optional<Food> findByIdWithBusiness(@Param("id") Long id);
}
