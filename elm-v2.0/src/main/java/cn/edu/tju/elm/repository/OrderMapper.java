package cn.edu.tju.elm.repository;


import cn.edu.tju.elm.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderMapper extends JpaRepository<Order,Long> {
}
