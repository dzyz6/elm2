package cn.edu.tju.elm.repository;


import cn.edu.tju.elm.model.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BusinessMapper extends JpaRepository<Business,Long> {
}
