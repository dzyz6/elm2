package cn.edu.tju.elm.repository;


import cn.edu.tju.elm.model.Transaction;
import cn.edu.tju.elm.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionMapper extends JpaRepository<Transaction,Long > {
    List<Transaction> findByFromwallet(Long fromWalletId);
    List<Transaction> findByFromwalletOrTowallet(Long fromWalletId, Long toWalletId);
    List<Transaction> findByFromwalletAndStatus(Long fromWalletId, Integer status);
    List<Transaction> findByTowalletAndStatus(Long toWalletId, Integer status);
    List<Transaction> findByStatus(Integer status);
}
