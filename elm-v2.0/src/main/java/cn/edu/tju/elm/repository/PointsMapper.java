package cn.edu.tju.elm.repository;


import cn.edu.tju.elm.model.Points;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PointsMapper extends JpaRepository<Points, Long> {
    Optional<Points> findByUserid(Long userid);
}

