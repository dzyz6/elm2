package cn.edu.tju.elm.repository;


import cn.edu.tju.elm.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletMapper extends JpaRepository<Wallet,Long > {
    Optional<Wallet> findByUserid(Long userid);


}
