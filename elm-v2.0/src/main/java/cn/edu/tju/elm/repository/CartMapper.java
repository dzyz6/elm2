package cn.edu.tju.elm.repository;


import cn.edu.tju.elm.model.Cart;
import cn.edu.tju.elm.model.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartMapper extends JpaRepository<Cart,Long> {
}
