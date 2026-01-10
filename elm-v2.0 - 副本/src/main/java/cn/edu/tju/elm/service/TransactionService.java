package cn.edu.tju.elm.service;


import cn.edu.tju.core.model.HttpResult;
import cn.edu.tju.elm.model.Transaction;
import cn.edu.tju.elm.model.Wallet;
import cn.edu.tju.elm.repository.TransactionMapper;
import cn.edu.tju.elm.repository.WalletMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TransactionService {

    @Autowired
    TransactionMapper transactionMapper;

    @Autowired
    WalletMapper walletMapper;

    @Autowired
    WalletService walletService;
    


    public HttpResult<List<Transaction>> getTransaction(Wallet wallet){
        return HttpResult.success(transactionMapper.findByFromwallet(wallet.getId()));
    }

    public HttpResult<Transaction> addTransaction(Transaction transaction){
        System.out.println("=== TransactionService.addTransaction START ===");
        System.out.println("Transaction object: " + transaction);
        System.out.println("Transaction type: " + transaction.getType());
        System.out.println("Transaction money: " + transaction.getMoney());
        System.out.println("From wallet: " + transaction.getFromwallet());
        System.out.println("To wallet: " + transaction.getTowallet());
        System.out.println("Description: " + transaction.getDescription());
        System.out.println("Transaction ID: " + transaction.getId());
        System.out.println("Transaction Status: " + transaction.getStatus());
        
        // 对于新交易，确保ID为null，避免Hibernate将其视为已存在的实体
        transaction.setId(null);
        System.out.println("Transaction ID after nullification: " + transaction.getId());
        
        try {
            HttpResult<Transaction> result = processTransaction(transaction);
            System.out.println("=== TransactionService.addTransaction SUCCESS ===");
            return result;
        } catch (Exception e) {
            System.out.println("=== TransactionService.addTransaction EXCEPTION ===");
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }


    }
    
    private HttpResult<Transaction> processTransaction(Transaction transaction) {
        System.out.println("=== processTransaction START ===");
        System.out.println("Processing transaction type: " + transaction.getType());
        
        switch (transaction.getType()) {
            case Transaction.TYPE_RECHARGE:
                System.out.println("Handling RECHARGE transaction");
                return handleRecharge(transaction);
            case Transaction.TYPE_WITHDRAW:
                System.out.println("Handling WITHDRAW transaction");
                return handleWithdraw(transaction);
            case Transaction.TYPE_PAYMENT:
                System.out.println("Handling PAYMENT transaction");
                return handlePayment(transaction);
            case Transaction.TYPE_FREEZE:
                System.out.println("Handling FREEZE transaction");
                return handleFreeze(transaction);
            case Transaction.TYPE_UNFREEZE:
                System.out.println("Handling UNFREEZE transaction");
                return handleUnfreeze(transaction);
            case Transaction.TYPE_OVERDRAFT:
                System.out.println("Handling OVERDRAFT transaction");
                return handleOverdraft(transaction);
            case Transaction.TYPE_REPAY:
                System.out.println("Handling REPAY transaction");
                return handleRepay(transaction);
            case Transaction.TYPE_INTEREST:
                System.out.println("Handling INTEREST transaction");
                return handleInterest(transaction);
            default:
                System.out.println("Unsupported transaction type: " + transaction.getType());
                return HttpResult.failure("500", "未知的交易类型");
        }
    }

    private HttpResult<Transaction> handleRecharge(Transaction transaction) {
        Wallet wallet = walletMapper.findById(transaction.getTowallet()).orElse(null);
        if (wallet == null) {
            return HttpResult.failure("500", "目标钱包不存在");
        }
        
        // 初始化rewardMoney字段（如果为null）
        if (wallet.getRewardMoney() == null) {
            wallet.setRewardMoney(0.0);
        }
        
        // 实现固定金额的充值奖励规则：充100送10，充200送20，以此类推
        Double rewardAmount = 0.0;
        if (transaction.getMoney() % 100 == 0 && transaction.getMoney() > 0) {
            rewardAmount = transaction.getMoney() * 0.1; // 10%的奖励比例
        }
        
        // 更新钱包余额：充值金额到money字段，奖励金额到rewardMoney字段
        wallet.setMoney(wallet.getMoney() + transaction.getMoney());
        wallet.setRewardMoney(wallet.getRewardMoney() + rewardAmount);
        walletMapper.save(wallet);
        
        // 更新交易描述
        if (rewardAmount > 0) {
            transaction.setDescription(transaction.getDescription() + "，奖励金额：" + rewardAmount + "元");
        }
        
        transaction.setStatus(Transaction.STATUS_COMPLETED);
        
        // 初始化version字段（如果为null）
        if (transaction.getVersion() == null) {
            transaction.setVersion(0);
            System.out.println("=== TransactionService.handleRecharge: version field initialized to 0 ===");
        }
        
        Transaction savedTransaction = transactionMapper.save(transaction);
        
        return HttpResult.success(savedTransaction);
    }

    private HttpResult<Transaction> handleWithdraw(Transaction transaction) {
        Wallet wallet = walletMapper.findById(transaction.getFromwallet()).orElse(null);
        if (wallet == null) {
            return HttpResult.failure("500", "源钱包不存在");
        }
        
        // 初始化money和rewardMoney字段（如果为null）
        if (wallet.getMoney() == null) {
            wallet.setMoney(0.0);
        }
        if (wallet.getRewardMoney() == null) {
            wallet.setRewardMoney(0.0);
        }
        
        // 计算提现手续费（这里简单实现为1%，实际应根据规则配置）
        Double feeRate = 0.01; // 1%手续费
        Double feeAmount = transaction.getMoney() * feeRate;
        
        // 检查余额是否足够支付提现金额+手续费（只能使用充值本金，不能使用奖励金额）
        Double totalAmount = transaction.getMoney() + feeAmount;
        if (wallet.getMoney() < totalAmount) {
            return HttpResult.failure("500", "余额不足，需支付手续费：" + feeAmount + "元");
        }
        
        // 扣除提现金额和手续费（只能从充值本金中扣除，奖励金额不能用于提现）
        wallet.setMoney(wallet.getMoney() - totalAmount);
        walletMapper.save(wallet);
        
        // 更新交易描述
        transaction.setDescription(transaction.getDescription() + "，手续费：" + feeAmount + "元");
        transaction.setStatus(Transaction.STATUS_COMPLETED);
        
        // 初始化主要交易对象的version字段
        if (transaction.getVersion() == null) {
            transaction.setVersion(0);
            System.out.println("=== TransactionService.handleWithdraw: version field initialized to 0 for main transaction ===");
        }
        
        // 创建手续费交易记录
        Transaction feeTransaction = new Transaction();
        feeTransaction.setType(Transaction.TYPE_INTEREST);
        feeTransaction.setFromwallet(transaction.getFromwallet());
        feeTransaction.setTowallet(-1L); // 系统账户
        feeTransaction.setMoney(feeAmount);
        feeTransaction.setStatus(Transaction.STATUS_COMPLETED);
        feeTransaction.setDescription("提现手续费");
        
        // 初始化手续费交易对象的version字段
        if (feeTransaction.getVersion() == null) {
            feeTransaction.setVersion(0);
            System.out.println("=== TransactionService.handleWithdraw: version field initialized to 0 for fee transaction ===");
        }
        
        transactionMapper.save(feeTransaction);
        return HttpResult.success(transactionMapper.save(transaction));
    }

    private HttpResult<Transaction> handlePayment(Transaction transaction) {
        Wallet fromWallet = walletMapper.findById(transaction.getFromwallet()).orElse(null);
        Wallet toWallet = walletMapper.findById(transaction.getTowallet()).orElse(null);
        
        if (fromWallet == null || toWallet == null) {
            return HttpResult.failure("500", "钱包不存在");
        }
        
        // 处理null值
        Double currentMoney = fromWallet.getMoney() != null ? fromWallet.getMoney() : 0.0;
        Double currentRewardMoney = fromWallet.getRewardMoney() != null ? fromWallet.getRewardMoney() : 0.0;
        Double currentFrozenMoney = fromWallet.getFrozenMoney() != null ? fromWallet.getFrozenMoney() : 0.0;
        
        // 检查余额（包括充值本金、奖励金额和透支额度）
        Double availableBalance = currentMoney + currentRewardMoney + (fromWallet.getIsVip() ? fromWallet.getOverdraftLimit() - fromWallet.getOverdraftAmount() : 0);
        if (availableBalance < transaction.getMoney()) {
            return HttpResult.failure("500", "余额不足");
        }
        
        // 先冻结买家的资金
        Double remainingPayment = transaction.getMoney();
        Double moneyToFreezeFromMoney = 0.0;
        Double moneyToFreezeFromRewardMoney = 0.0;
        Double moneyToFreezeFromOverdraft = 0.0;
        
        // 首先使用rewardMoney字段中的奖励金额
        if (remainingPayment > 0 && currentRewardMoney > 0) {
            moneyToFreezeFromRewardMoney = Math.min(remainingPayment, currentRewardMoney);
            remainingPayment -= moneyToFreezeFromRewardMoney;
        }
        
        // 然后使用money字段中的充值本金
        if (remainingPayment > 0 && currentMoney > 0) {
            moneyToFreezeFromMoney = Math.min(remainingPayment, currentMoney);
            remainingPayment -= moneyToFreezeFromMoney;
        }
        
        // 最后使用透支额度（如果是VIP用户）
        if (remainingPayment > 0 && fromWallet.getIsVip()) {
            Double availableOverdraft = fromWallet.getOverdraftLimit() - fromWallet.getOverdraftAmount();
            moneyToFreezeFromOverdraft = Math.min(remainingPayment, availableOverdraft);
            remainingPayment -= moneyToFreezeFromOverdraft;
        }
        
        // 更新钱包余额
        fromWallet.setMoney(currentMoney - moneyToFreezeFromMoney);
        fromWallet.setRewardMoney(currentRewardMoney - moneyToFreezeFromRewardMoney);
        fromWallet.setFrozenMoney(currentFrozenMoney + transaction.getMoney());
        
        // 处理透支
        if (moneyToFreezeFromOverdraft > 0) {
            fromWallet.setOverdraftAmount(fromWallet.getOverdraftAmount() + moneyToFreezeFromOverdraft);
            fromWallet.setOverdraftTime(java.time.LocalDateTime.now());
        }
        
        walletMapper.save(fromWallet);
        transaction.setStatus(Transaction.STATUS_PENDING);
        
        // 初始化version字段（如果为null）
        if (transaction.getVersion() == null) {
            transaction.setVersion(0);
            System.out.println("=== TransactionService.handlePayment: version field initialized to 0 ===");
        }
        
        return HttpResult.success(transactionMapper.save(transaction));
    }

    private HttpResult<Transaction> handleFreeze(Transaction transaction) {
        System.out.println("=== handleFreeze called ===");
        System.out.println("fromwallet: " + transaction.getFromwallet());
        System.out.println("money: " + transaction.getMoney());
        
        Wallet wallet = walletMapper.findById(transaction.getFromwallet()).orElse(null);
        if (wallet == null) {
            System.out.println("钱包不存在");
            return HttpResult.failure("500", "钱包不存在");
        }
        
        System.out.println("钱包余额: " + wallet.getMoney());
        System.out.println("请求冻结金额: " + transaction.getMoney());
        
        if (wallet.getMoney() < transaction.getMoney()) {
            System.out.println("余额不足");
            return HttpResult.failure("500", "余额不足");
        }
        System.out.println("???");
        try {
            // 处理null值
            Double currentMoney = wallet.getMoney() != null ? wallet.getMoney() : 0.0;
            Double currentFrozenMoney = wallet.getFrozenMoney() != null ? wallet.getFrozenMoney() : 0.0;
            
            System.out.println("当前money: " + currentMoney);
            System.out.println("当前frozenMoney: " + currentFrozenMoney);
            
            Double newMoney = currentMoney - transaction.getMoney();
            Double newFrozenMoney = currentFrozenMoney + transaction.getMoney();
            
            System.out.println("准备设置新money: " + newMoney);
            wallet.setMoney(newMoney);
            System.out.println("money设置成功");
            
            System.out.println("准备设置新frozenMoney: " + newFrozenMoney);
            wallet.setFrozenMoney(newFrozenMoney);
            System.out.println("frozenMoney设置成功");
        } catch (Exception e) {
            System.out.println("设置金额时发生异常: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        System.out.println("???");
        System.out.println("准备保存钱包...");
        Wallet savedWallet = walletMapper.save(wallet);
        System.out.println("钱包保存成功，ID: " + savedWallet.getId());
        
        transaction.setStatus(Transaction.STATUS_COMPLETED);
        
        // 初始化version字段（如果为null）
        if (transaction.getVersion() == null) {
            transaction.setVersion(0);
            System.out.println("=== TransactionService.handleFreeze: version field initialized to 0 ===");
        }
        
        System.out.println("准备保存交易...");
        Transaction savedTransaction = transactionMapper.save(transaction);
        System.out.println("交易保存成功，ID: " + savedTransaction.getId());
        
        System.out.println("冻结成功，准备返回结果");
        return HttpResult.success(savedTransaction);
    }

    private HttpResult<Transaction> handleUnfreeze(Transaction transaction) {
        Wallet wallet = walletMapper.findById(transaction.getTowallet()).orElse(null);
        if (wallet == null) {
            return HttpResult.failure("500", "钱包不存在");
        }
        
        // 处理null值
        Double frozenMoney = wallet.getFrozenMoney() != null ? wallet.getFrozenMoney() : 0.0;
        Double currentMoney = wallet.getMoney() != null ? wallet.getMoney() : 0.0;
        
        if (frozenMoney < transaction.getMoney()) {
            return HttpResult.failure("500", "冻结金额不足");
        }
        
        wallet.setFrozenMoney(frozenMoney - transaction.getMoney());
        wallet.setMoney(currentMoney + transaction.getMoney());
        walletMapper.save(wallet);
        transaction.setStatus(Transaction.STATUS_COMPLETED);
        
        // 初始化version字段（如果为null）
        if (transaction.getVersion() == null) {
            transaction.setVersion(0);
            System.out.println("=== TransactionService.handleUnfreeze: version field initialized to 0 ===");
        }
        
        return HttpResult.success(transactionMapper.save(transaction));
    }

    private HttpResult<Transaction> handleOverdraft(Transaction transaction) {
        System.out.println("=== handleOverdraft START ===");
        
        Wallet wallet = walletMapper.findById(transaction.getTowallet()).orElse(null);
        if (wallet == null) {
            return HttpResult.failure("500", "钱包不存在");
        }
        
        if (!wallet.getIsVip()) {
            return HttpResult.failure("500", "非VIP用户不能透支");
        }
        
        // 处理null值
        Double overdraftLimit = wallet.getOverdraftLimit() != null ? wallet.getOverdraftLimit() : 0.0;
        Double overdraftAmount = wallet.getOverdraftAmount() != null ? wallet.getOverdraftAmount() : 0.0;
        Double currentMoney = wallet.getMoney() != null ? wallet.getMoney() : 0.0;
        
        Double availableOverdraft = overdraftLimit - overdraftAmount;
        if (availableOverdraft < transaction.getMoney()) {
            return HttpResult.failure("500", "透支额度不足");
        }
        
        wallet.setMoney(currentMoney + transaction.getMoney());
        wallet.setOverdraftAmount(overdraftAmount + transaction.getMoney());
        wallet.setOverdraftTime(java.time.LocalDateTime.now());
        walletMapper.save(wallet);
        transaction.setStatus(Transaction.STATUS_COMPLETED);
        
        // 初始化version字段（如果为null）
        if (transaction.getVersion() == null) {
            transaction.setVersion(0);
            System.out.println("=== TransactionService.handleOverdraft: version field initialized to 0 ===");
        }
        
        return HttpResult.success(transactionMapper.save(transaction));
    }

    private HttpResult<Transaction> handleRepay(Transaction transaction) {
        Wallet wallet = walletMapper.findById(transaction.getFromwallet()).orElse(null);
        if (wallet == null) {
            return HttpResult.failure("500", "钱包不存在");
        }
        
        // 处理null值
        Double currentMoney = wallet.getMoney() != null ? wallet.getMoney() : 0.0;
        Double overdraftAmount = wallet.getOverdraftAmount() != null ? wallet.getOverdraftAmount() : 0.0;
        
        if (currentMoney < transaction.getMoney()) {
            return HttpResult.failure("500", "余额不足");
        }
        
        Double repayAmount = Math.min(transaction.getMoney(), overdraftAmount);
        wallet.setMoney(currentMoney - repayAmount);
        wallet.setOverdraftAmount(overdraftAmount - repayAmount);
        if (wallet.getOverdraftAmount() == 0) {
            wallet.setOverdraftTime(null);
        }
        walletMapper.save(wallet);
        transaction.setStatus(Transaction.STATUS_COMPLETED);
        
        // 初始化version字段（如果为null）
        try {
            java.lang.reflect.Field versionField = transaction.getClass().getDeclaredField("version");
            versionField.setAccessible(true);
            if (versionField.get(transaction) == null) {
                versionField.set(transaction, 0);
                System.out.println("=== TransactionService.handleRepay: version field initialized to 0 ===");
            }
        } catch (Exception e) {
            System.out.println("=== TransactionService.handleRepay: Exception initializing version field ===");
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }
        
        return HttpResult.success(transactionMapper.save(transaction));
    }

    private HttpResult<Transaction> handleInterest(Transaction transaction) {
        Wallet wallet = walletMapper.findById(transaction.getFromwallet()).orElse(null);
        if (wallet == null) {
            return HttpResult.failure("500", "钱包不存在");
        }
        
        // 处理null值
        Double currentMoney = wallet.getMoney() != null ? wallet.getMoney() : 0.0;
        
        if (currentMoney < transaction.getMoney()) {
            return HttpResult.failure("500", "余额不足");
        }
        
        wallet.setMoney(currentMoney - transaction.getMoney());
        walletMapper.save(wallet);
        transaction.setStatus(Transaction.STATUS_COMPLETED);
        
        // 初始化version字段（如果为null）
        try {
            java.lang.reflect.Field versionField = transaction.getClass().getDeclaredField("version");
            versionField.setAccessible(true);
            if (versionField.get(transaction) == null) {
                versionField.set(transaction, 0);
                System.out.println("=== TransactionService.handleInterest: version field initialized to 0 ===");
            }
        } catch (Exception e) {
            System.out.println("=== TransactionService.handleInterest: Exception initializing version field ===");
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }
        
        return HttpResult.success(transactionMapper.save(transaction));
    }

    // 确认收货 - 解冻买家的冻结资金并转移给卖家
    public HttpResult<Transaction> confirmOrder(Long fromWalletId, Long toWalletId, Double amount) {
        Wallet fromWallet = walletMapper.findById(fromWalletId).orElse(null); // 买家钱包
        Wallet toWallet = walletMapper.findById(toWalletId).orElse(null);     // 卖家钱包
        
        if (fromWallet == null || toWallet == null) {
            return HttpResult.failure("500", "钱包不存在");
        }
        
        // 检查买家冻结资金是否足够
        if (fromWallet.getFrozenMoney() < amount) {
            return HttpResult.failure("500", "买家冻结金额不足");
        }
        
        // 解冻买家的冻结资金
        fromWallet.setFrozenMoney(fromWallet.getFrozenMoney() - amount);
        walletMapper.save(fromWallet);
        
        // 将资金转移到卖家的可用余额
        toWallet.setMoney(toWallet.getMoney() + amount);
        walletMapper.save(toWallet);
        
        // 更新原始支付交易状态为已完成
        List<Transaction> pendingTransactions = transactionMapper.findByFromwalletAndStatus(fromWalletId, Transaction.STATUS_PENDING);
        for (Transaction pendingTx : pendingTransactions) {
            if (pendingTx.getTowallet().equals(toWalletId) && pendingTx.getMoney().equals(amount)) {
                pendingTx.setStatus(Transaction.STATUS_COMPLETED);
                pendingTx.setDescription("订单确认，资金已转移给卖家");
                
                // 初始化version字段（如果为null）
                try {
                    java.lang.reflect.Field versionField = pendingTx.getClass().getDeclaredField("version");
                    versionField.setAccessible(true);
                    if (versionField.get(pendingTx) == null) {
                        versionField.set(pendingTx, 0);
                        System.out.println("=== TransactionService.confirmOrder: version field initialized to 0 for pending transaction ===");
                    }
                } catch (Exception e) {
                    System.out.println("=== TransactionService.confirmOrder: Exception initializing version field for pending transaction ===");
                    System.out.println("Exception: " + e.getMessage());
                    e.printStackTrace();
                }
                
                transactionMapper.save(pendingTx);
                break;
            }
        }
        
        // 创建解冻交易记录
        Transaction unfreezeTransaction = new Transaction();
        unfreezeTransaction.setFromwallet(fromWalletId);
        unfreezeTransaction.setTowallet(toWalletId);
        unfreezeTransaction.setMoney(amount);
        unfreezeTransaction.setType(Transaction.TYPE_UNFREEZE);
        unfreezeTransaction.setStatus(Transaction.STATUS_COMPLETED);
        unfreezeTransaction.setDescription("确认收货，资金从买家解冻并转移给卖家");
        
        // 初始化version字段（如果为null）
        try {
            java.lang.reflect.Field versionField = unfreezeTransaction.getClass().getDeclaredField("version");
            versionField.setAccessible(true);
            if (versionField.get(unfreezeTransaction) == null) {
                versionField.set(unfreezeTransaction, 0);
                System.out.println("=== TransactionService.confirmOrder: version field initialized to 0 for unfreeze transaction ===");
            }
        } catch (Exception e) {
            System.out.println("=== TransactionService.confirmOrder: Exception initializing version field for unfreeze transaction ===");
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }
        
        return HttpResult.success(transactionMapper.save(unfreezeTransaction));
    }

    // 计算透支利息
    public HttpResult<Transaction> calculateOverdraftInterest(Long walletId) {
        Wallet wallet = walletMapper.findById(walletId).orElse(null);
        if (wallet == null) {
            return HttpResult.failure("500", "钱包不存在");
        }
        
        if (wallet.getOverdraftAmount() <= 0 || wallet.getOverdraftTime() == null) {
            return HttpResult.failure("500", "无透支金额");
        }
        
        // 计算透支天数（超过7天开始计息）
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.Duration duration = java.time.Duration.between(wallet.getOverdraftTime(), now);
        long days = duration.toDays();
        
        if (days <= 7) {
            return HttpResult.failure("500", "透支未超过7天，不计息");
        }
        
        // 计算利息：日利率0.1%
        double dailyRate = 0.001;
        double interest = wallet.getOverdraftAmount() * dailyRate * (days - 7);
        
        // 创建利息交易记录
        Transaction interestTransaction = new Transaction();
        interestTransaction.setFromwallet(walletId);
        interestTransaction.setTowallet(-1L); // 系统账户
        interestTransaction.setMoney(interest);
        interestTransaction.setType(Transaction.TYPE_INTEREST);
        interestTransaction.setStatus(Transaction.STATUS_PENDING);
        interestTransaction.setDescription("透支利息：" + days + "天");
        
        // 初始化version字段（如果为null）
        try {
            java.lang.reflect.Field versionField = interestTransaction.getClass().getDeclaredField("version");
            versionField.setAccessible(true);
            if (versionField.get(interestTransaction) == null) {
                versionField.set(interestTransaction, 0);
                System.out.println("=== TransactionService.calculateOverdraftInterest: version field initialized to 0 for interest transaction ===");
            }
        } catch (Exception e) {
            System.out.println("=== TransactionService.calculateOverdraftInterest: Exception initializing version field for interest transaction ===");
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }
        
        return HttpResult.success(transactionMapper.save(interestTransaction));
    }

    // 查询待处理交易
    public List<Transaction> getPendingTransactions(Long walletId) {
        return transactionMapper.findByFromwalletAndStatus(walletId, Transaction.STATUS_PENDING);
    }

    // 更新交易状态
    public HttpResult<Transaction> updateTransaction(Long transactionId, Integer status) {
        Transaction transaction = transactionMapper.findById(transactionId).orElse(null);
        if (transaction == null) {
            return HttpResult.failure("500", "交易不存在");
        }
        transaction.setStatus(status);
        
        // 初始化version字段（如果为null）
        if (transaction.getVersion() == null) {
            transaction.setVersion(0);
            System.out.println("=== TransactionService.updateTransaction: version field initialized to 0 ===");
        }
        
        return HttpResult.success(transactionMapper.save(transaction));
    }

}