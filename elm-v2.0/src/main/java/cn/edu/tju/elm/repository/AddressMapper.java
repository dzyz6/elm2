package cn.edu.tju.elm.repository;

import cn.edu.tju.elm.model.DeliveryAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressMapper extends JpaRepository<DeliveryAddress,Long> {
    List<DeliveryAddress> findByCustomerId(Long userId);
}
