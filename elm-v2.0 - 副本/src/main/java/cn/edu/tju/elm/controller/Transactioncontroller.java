package cn.edu.tju.elm.controller;


import cn.edu.tju.core.model.HttpResult;
import cn.edu.tju.elm.model.Transaction;
import cn.edu.tju.elm.model.Wallet;
import cn.edu.tju.elm.repository.WalletMapper;
import cn.edu.tju.elm.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transaction")
public class Transactioncontroller {
    @Autowired
    TransactionService transactionService;

    @Autowired
    WalletMapper walletMapper;

    @PostMapping("/addTransaction")
    public HttpResult<Transaction> addTransaction(@RequestBody Transaction transaction){
        System.out.println("=== TransactionController.addTransaction called ===");
        System.out.println("Request transaction: " + transaction);
        System.out.println("Transaction type: " + transaction.getType());
        System.out.println("Transaction money: " + transaction.getMoney());
        System.out.println("From wallet: " + transaction.getFromwallet());
        System.out.println("To wallet: " + transaction.getTowallet());
        
        try {
            HttpResult<Transaction> result = transactionService.addTransaction(transaction);
            System.out.println("TransactionService result: " + result);
            return result;
        } catch (Exception e) {
            System.out.println("Exception in addTransaction: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }


    @PostMapping("/getTransaction")
    public HttpResult<List<Transaction>> getTransaction(@RequestBody java.util.Map<String, Long> request){
        Long userid = request.get("userid");
        Wallet wallet=walletMapper.findByUserid(userid).orElseThrow();
        return transactionService.getTransaction(wallet);
    }

    @PostMapping("/confirmOrder")
    public HttpResult<Transaction> confirmOrder(@RequestParam Long orderId, @RequestParam Long buyerId, @RequestParam Long sellerId){
        // 查找待处理的支付交易
        Wallet buyerWallet = walletMapper.findByUserid(buyerId).orElse(null);
        Wallet sellerWallet = walletMapper.findByUserid(sellerId).orElse(null);
        
        if (buyerWallet == null || sellerWallet == null) {
            return HttpResult.failure("500", "钱包不存在");
        }
        
        // 查找相关的待处理交易
        List<Transaction> pendingTransactions = transactionService.getPendingTransactions(buyerWallet.getId());
        
        for (Transaction transaction : pendingTransactions) {
            if (transaction.getOrderId() != null && transaction.getOrderId().equals(orderId)) {
                // 解冻买家资金并转移到卖家
                if (buyerWallet.getFrozenMoney() >= transaction.getMoney()) {
                    // 从买家冻结资金中扣除
                    buyerWallet.setFrozenMoney(buyerWallet.getFrozenMoney() - transaction.getMoney());
                    walletMapper.save(buyerWallet);
                    
                    // 转移到卖家可用余额
                    sellerWallet.setMoney(sellerWallet.getMoney() + transaction.getMoney());
                    walletMapper.save(sellerWallet);
                    
                    // 更新交易状态
                    transaction.setStatus(Transaction.STATUS_COMPLETED);
                    transaction.setDescription("订单确认，资金已转移给卖家");
                    return transactionService.updateTransaction(transaction.getId(), Transaction.STATUS_COMPLETED);
                }
            }
        }
        
        return HttpResult.failure("500", "未找到相关订单或订单状态不正确");
    }

    @PostMapping("/confirmReceive")
    public HttpResult<Transaction> confirmReceive(@RequestParam Long fromWalletId, @RequestParam Long toWalletId, @RequestParam Double amount){
        return transactionService.confirmOrder(fromWalletId, toWalletId, amount);
    }

    @PostMapping("/calculateInterest")
    public HttpResult<Transaction> calculateInterest(@RequestParam Long walletId){
        return transactionService.calculateOverdraftInterest(walletId);
    }

    @GetMapping("/pendingTransactions")
    public HttpResult<List<Transaction>> getPendingTransactions(@RequestParam Long walletId){
        return HttpResult.success(transactionService.getPendingTransactions(walletId));
    }

    @PostMapping("/updateTransaction")
    public HttpResult<Transaction> updateTransaction(@RequestParam Long transactionId, @RequestParam Integer status){
        return transactionService.updateTransaction(transactionId, status);
    }
}
