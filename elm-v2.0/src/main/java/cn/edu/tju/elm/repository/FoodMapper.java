package cn.edu.tju.elm.repository;


import cn.edu.tju.elm.model.Business;
import cn.edu.tju.elm.model.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodMapper extends JpaRepository<Food,Long> {
}
