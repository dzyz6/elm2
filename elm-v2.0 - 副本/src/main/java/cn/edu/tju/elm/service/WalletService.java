package cn.edu.tju.elm.service;


import cn.edu.tju.core.model.HttpResult;
import cn.edu.tju.elm.model.Wallet;
import cn.edu.tju.elm.repository.WalletMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WalletService {

    @Autowired
    WalletMapper walletMapper;

    public HttpResult<Wallet> addWallet(Wallet wallet){
        wallet.setMoney(0.0);
        wallet.setFrozenMoney(0.0);
        wallet.setOverdraftLimit(0.0);
        wallet.setOverdraftAmount(0.0);
        wallet.setIsVip(false);
        return HttpResult.success( walletMapper.save(wallet));
    }

    public HttpResult<Wallet> setWallet(Wallet wallet){
        Wallet existingWallet = walletMapper.findByUserid(wallet.getUserid()).orElse(null);
        if(existingWallet == null){
            return HttpResult.failure("500", "用户没有钱包，请先创建");
        }
        // 更新现有钱包的信息，保持ID不变
        existingWallet.setMoney(wallet.getMoney());
        existingWallet.setFrozenMoney(wallet.getFrozenMoney());
        existingWallet.setOverdraftLimit(wallet.getOverdraftLimit());
        existingWallet.setOverdraftAmount(wallet.getOverdraftAmount());
        existingWallet.setOverdraftTime(wallet.getOverdraftTime());
        existingWallet.setIsVip(wallet.getIsVip());
        return HttpResult.success(walletMapper.save(existingWallet));
    }

    // 冻结资金
    public HttpResult<Wallet> freezeMoney(Long userid, Double amount) {
        Wallet wallet = walletMapper.findByUserid(userid).orElse(null);
        if (wallet == null) {
            return HttpResult.failure("500", "用户没有钱包");
        }
        if (wallet.getMoney() < amount) {
            return HttpResult.failure("500", "余额不足");
        }
        wallet.setMoney(wallet.getMoney() - amount);
        wallet.setFrozenMoney(wallet.getFrozenMoney() + amount);
        return HttpResult.success(walletMapper.save(wallet));
    }

    // 解冻资金
    public HttpResult<Wallet> unfreezeMoney(Long userid, Double amount) {
        Wallet wallet = walletMapper.findByUserid(userid).orElse(null);
        if (wallet == null) {
            return HttpResult.failure("500", "用户没有钱包");
        }
        if (wallet.getFrozenMoney() < amount) {
            return HttpResult.failure("500", "冻结金额不足");
        }
        wallet.setFrozenMoney(wallet.getFrozenMoney() - amount);
        wallet.setMoney(wallet.getMoney() + amount);
        return HttpResult.success(walletMapper.save(wallet));
    }

    // 设置VIP状态和透支额度
    public HttpResult<Wallet> setVipStatus(Long userid, Boolean isVip, Double overdraftLimit) {
        Wallet wallet = walletMapper.findByUserid(userid).orElse(null);
        if (wallet == null) {
            return HttpResult.failure("500", "用户没有钱包");
        }
        wallet.setIsVip(isVip);
        wallet.setOverdraftLimit(overdraftLimit);
        return HttpResult.success(walletMapper.save(wallet));
    }

    // 透支功能
    public HttpResult<Wallet> overdraft(Long userid, Double amount) {
        Wallet wallet = walletMapper.findByUserid(userid).orElse(null);
        if (wallet == null) {
            return HttpResult.failure("500", "用户没有钱包");
        }
        if (!wallet.getIsVip()) {
            return HttpResult.failure("500", "非VIP用户不能透支");
        }
        Double availableOverdraft = wallet.getOverdraftLimit() - wallet.getOverdraftAmount();
        if (availableOverdraft < amount) {
            return HttpResult.failure("500", "透支额度不足");
        }
        wallet.setMoney(wallet.getMoney() + amount);
        wallet.setOverdraftAmount(wallet.getOverdraftAmount() + amount);
        wallet.setOverdraftTime(java.time.LocalDateTime.now());
        return HttpResult.success(walletMapper.save(wallet));
    }

    // 还款功能
    public HttpResult<Wallet> repayOverdraft(Long userid, Double amount) {
        Wallet wallet = walletMapper.findByUserid(userid).orElse(null);
        if (wallet == null) {
            return HttpResult.failure("500", "用户没有钱包");
        }
        if (wallet.getOverdraftAmount() <= 0) {
            return HttpResult.failure("500", "没有透支金额需要还款");
        }
        if (wallet.getMoney() < amount) {
            return HttpResult.failure("500", "余额不足");
        }
        Double repayAmount = Math.min(amount, wallet.getOverdraftAmount());
        wallet.setMoney(wallet.getMoney() - repayAmount);
        wallet.setOverdraftAmount(wallet.getOverdraftAmount() - repayAmount);
        if (wallet.getOverdraftAmount() == 0) {
            wallet.setOverdraftTime(null);
        }
        return HttpResult.success(walletMapper.save(wallet));
    }

    // 计算透支利息（简单示例：日利率0.1%）
    public HttpResult<Double> calculateInterest(Long userid) {
        Wallet wallet = walletMapper.findByUserid(userid).orElse(null);
        if (wallet == null) {
            return HttpResult.failure("500", "用户没有钱包");
        }
        if (wallet.getOverdraftAmount() <= 0 || wallet.getOverdraftTime() == null) {
            return HttpResult.success(0.0);
        }
        long daysBetween = java.time.Duration.between(wallet.getOverdraftTime(), java.time.LocalDateTime.now()).toDays();
        Double interest = wallet.getOverdraftAmount() * 0.001 * daysBetween; // 日利率0.1%
        return HttpResult.success(interest);
    }

    public HttpResult<Wallet> getWallet(Long userid){
        if(!walletMapper.findByUserid(userid).isPresent()){
            // 如果用户没有钱包，自动创建一个
            Wallet newWallet = new Wallet();
            newWallet.setUserid(userid);
            newWallet.setMoney(0.0);
            newWallet.setFrozenMoney(0.0);
            newWallet.setOverdraftLimit(0.0);
            newWallet.setOverdraftAmount(0.0);
            newWallet.setIsVip(false);
            System.out.println("为用户 " + userid + " 创建新钱包");
            return HttpResult.success(walletMapper.save(newWallet));
        }
        return HttpResult.success(walletMapper.findByUserid(userid).get());
    }
}
