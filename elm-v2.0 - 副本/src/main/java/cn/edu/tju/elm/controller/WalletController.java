package cn.edu.tju.elm.controller;


import cn.edu.tju.core.model.HttpResult;
import cn.edu.tju.elm.model.Order;
import cn.edu.tju.elm.model.Transaction;
import cn.edu.tju.elm.model.Wallet;
import cn.edu.tju.elm.service.TransactionService;
import cn.edu.tju.elm.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {
    @Autowired
    WalletService walletService;
    
    @Autowired
    TransactionService transactionService;

    @PostMapping("/addWallet")
    public HttpResult<Wallet> addWallet(@RequestBody Wallet wallet) throws Exception {
        return walletService.addWallet(wallet);
    }

    @PostMapping("/setWallet")
    public HttpResult<Wallet> setWallet(@RequestBody Wallet wallet) throws Exception {
        return walletService.setWallet(wallet);
    }

    @GetMapping("/getWallet")
    public HttpResult<Wallet> getWallet(@RequestParam Long userid) throws Exception {
        return walletService.getWallet(userid);
    }

    @PostMapping("/freezeMoney")
    public HttpResult<Wallet> freezeMoney(@RequestParam Long userid, @RequestParam Double amount) throws Exception {
        return walletService.freezeMoney(userid, amount);
    }

    @PostMapping("/unfreezeMoney")
    public HttpResult<Wallet> unfreezeMoney(@RequestParam Long userid, @RequestParam Double amount) throws Exception {
        return walletService.unfreezeMoney(userid, amount);
    }

    @PostMapping("/setVipStatus")
    public HttpResult<Wallet> setVipStatus(@RequestParam Long userid, @RequestParam Boolean isVip, @RequestParam Double overdraftLimit) throws Exception {
        return walletService.setVipStatus(userid, isVip, overdraftLimit);
    }

    @PostMapping("/overdraft")
    public HttpResult<Wallet> overdraft(@RequestParam Long userid, @RequestParam Double amount) throws Exception {
        return walletService.overdraft(userid, amount);
    }

    @PostMapping("/repayOverdraft")
    public HttpResult<Wallet> repayOverdraft(@RequestParam Long userid, @RequestParam Double amount) throws Exception {
        return walletService.repayOverdraft(userid, amount);
    }

    @GetMapping("/calculateInterest")
    public HttpResult<Double> calculateInterest(@RequestParam Long userid) throws Exception {
        return walletService.calculateInterest(userid);
    }
    
    // 充值接口
    @PostMapping("/recharge")
    public HttpResult<Transaction> recharge(@RequestParam Long userid, @RequestParam Double amount) throws Exception {
        // 获取用户钱包
        Wallet wallet = walletService.getWallet(userid).getData();
        
        // 创建充值交易
        Transaction transaction = new Transaction();
        transaction.setType(Transaction.TYPE_RECHARGE);
        transaction.setFromwallet(-1L); // 系统账户
        transaction.setTowallet(wallet.getId());
        transaction.setMoney(amount);
        transaction.setStatus(Transaction.STATUS_COMPLETED);
        transaction.setDescription("充值");
        
        return transactionService.addTransaction(transaction);
    }
    
    // 提现接口
    @PostMapping("/withdraw")
    public HttpResult<Transaction> withdraw(@RequestParam Long userid, @RequestParam Double amount) throws Exception {
        // 获取用户钱包
        Wallet wallet = walletService.getWallet(userid).getData();
        
        // 创建提现交易
        Transaction transaction = new Transaction();
        transaction.setType(Transaction.TYPE_WITHDRAW);
        transaction.setFromwallet(wallet.getId());
        transaction.setTowallet(-2L); // 提现账户
        transaction.setMoney(amount);
        transaction.setStatus(Transaction.STATUS_COMPLETED);
        transaction.setDescription("提现");
        
        return transactionService.addTransaction(transaction);
    }
    
    // 支付接口
    @PostMapping("/pay")
    public HttpResult<Transaction> pay(@RequestParam Long fromUserid, @RequestParam Long toUserid, @RequestParam Double amount, @RequestParam Long orderId) throws Exception {
        // 获取买卖双方钱包
        Wallet fromWallet = walletService.getWallet(fromUserid).getData();
        Wallet toWallet = walletService.getWallet(toUserid).getData();
        
        // 创建支付交易
        Transaction transaction = new Transaction();
        transaction.setType(Transaction.TYPE_PAYMENT);
        transaction.setFromwallet(fromWallet.getId());
        transaction.setTowallet(toWallet.getId());
        transaction.setMoney(amount);
        transaction.setStatus(Transaction.STATUS_PENDING);
        transaction.setOrderId(orderId);
        transaction.setDescription("订单支付");
        
        return transactionService.addTransaction(transaction);
    }
    
    // 交易流水查询
    @GetMapping("/transactionHistory")
    public HttpResult<List<Transaction>> transactionHistory(@RequestParam Long userid) throws Exception {
        // 获取用户钱包
        Wallet wallet = walletService.getWallet(userid).getData();
        
        // 这里简化实现，实际应该查询该用户钱包的所有交易记录
        // 可以通过TransactionService添加一个查询方法来实现
        return transactionService.getTransaction(wallet);
    }

}
