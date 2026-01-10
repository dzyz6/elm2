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
        wallet.initialize();
        return HttpResult.success(walletMapper.save(wallet));
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

    // 冻结资金（仅从充值本金扣除）
    public HttpResult<Wallet> freezeMoney(Long userid, Double amount) {
        Wallet wallet = walletMapper.findByUserid(userid).orElse(null);
        if (wallet == null) {
            return HttpResult.failure("500", "用户没有钱包");
        }
        if (!wallet.freezeMoney(amount)) {
            return HttpResult.failure("500", "余额不足");
        }
        return HttpResult.success(walletMapper.save(wallet));
    }
    
    // 冻结资金（先使用奖励金额，再使用充值本金）
    public HttpResult<Wallet> freezeMoneyWithRewardPriority(Long userid, Double amount) {
        Wallet wallet = walletMapper.findByUserid(userid).orElse(null);
        if (wallet == null) {
            return HttpResult.failure("500", "用户没有钱包");
        }
        if (!wallet.freezeMoneyWithRewardPriority(amount)) {
            return HttpResult.failure("500", "余额不足");
        }
        return HttpResult.success(walletMapper.save(wallet));
    }

    // 解冻资金
    public HttpResult<Wallet> unfreezeMoney(Long userid, Double amount) {
        Wallet wallet = walletMapper.findByUserid(userid).orElse(null);
        if (wallet == null) {
            return HttpResult.failure("500", "用户没有钱包");
        }
        if (!wallet.unfreezeMoney(amount)) {
            return HttpResult.failure("500", "冻结金额不足");
        }
        return HttpResult.success(walletMapper.save(wallet));
    }

    // 设置VIP状态和透支额度
    public HttpResult<Wallet> setVipStatus(Long userid, Boolean isVip, Double overdraftLimit) {
        Wallet wallet = walletMapper.findByUserid(userid).orElse(null);
        if (wallet == null) {
            return HttpResult.failure("500", "用户没有钱包");
        }
        wallet.setVipStatus(isVip, overdraftLimit);
        return HttpResult.success(walletMapper.save(wallet));
    }

    // 透支功能
    public HttpResult<Wallet> overdraft(Long userid, Double amount) {
        Wallet wallet = walletMapper.findByUserid(userid).orElse(null);
        if (wallet == null) {
            return HttpResult.failure("500", "用户没有钱包");
        }
        if (!wallet.overdraft(amount)) {
            return HttpResult.failure("500", "非VIP用户不能透支或透支额度不足");
        }
        return HttpResult.success(walletMapper.save(wallet));
    }

    // 还款功能
    public HttpResult<Wallet> repayOverdraft(Long userid, Double amount) {
        Wallet wallet = walletMapper.findByUserid(userid).orElse(null);
        if (wallet == null) {
            return HttpResult.failure("500", "用户没有钱包");
        }
        if (!wallet.repayOverdraft(amount)) {
            return HttpResult.failure("500", "没有透支金额需要还款或余额不足");
        }
        return HttpResult.success(walletMapper.save(wallet));
    }

    // 计算透支利息（简单示例：日利率0.1%）
    public HttpResult<Double> calculateInterest(Long userid) {
        Wallet wallet = walletMapper.findByUserid(userid).orElse(null);
        if (wallet == null) {
            return HttpResult.failure("500", "用户没有钱包");
        }
        return HttpResult.success(wallet.calculateInterest());
    }

    public HttpResult<Wallet> getWallet(Long userid){
        if(!walletMapper.findByUserid(userid).isPresent()){
            // 如果用户没有钱包，自动创建一个
            Wallet newWallet = new Wallet();
            newWallet.setUserid(userid);
            newWallet.initialize();
            System.out.println("为用户 " + userid + " 创建新钱包");
            return HttpResult.success(walletMapper.save(newWallet));
        }
        return HttpResult.success(walletMapper.findByUserid(userid).get());
    }
}
