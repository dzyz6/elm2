package cn.edu.tju.elm.model;


import cn.edu.tju.core.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.criteria.CriteriaBuilder;

@Entity
@Table(name = "wallet")
public class Wallet extends BaseEntity {
    @Column(name = "money")
    private Double money;

    @Column(name = "userid", unique = true, nullable = false)
    private Long userid;

    @Column(name = "frozen_money")
    private Double frozenMoney;

    @Column(name = "overdraft_limit")
    private Double overdraftLimit;

    @Column(name = "overdraft_amount")
    private Double overdraftAmount;

    @Column(name = "overdraft_time")
    private java.time.LocalDateTime overdraftTime;

    @Column(name = "is_vip")
    private Boolean isVip;

    @Column(name = "reward_money")
    private Double rewardMoney;

    public Double getMoney(){return this.money;}
    public void setMoney(Double money){this.money= money;}
    public Long getUserid(){return this.userid;}
    public void setUserid(Long userid){this.userid= userid;}
    public Double getFrozenMoney(){return this.frozenMoney;}
    public void setFrozenMoney(Double frozenMoney){this.frozenMoney= frozenMoney;}
    public Double getOverdraftLimit(){return this.overdraftLimit;}
    public void setOverdraftLimit(Double overdraftLimit){this.overdraftLimit= overdraftLimit;}
    public Double getOverdraftAmount(){return this.overdraftAmount;}
    public void setOverdraftAmount(Double overdraftAmount){this.overdraftAmount= overdraftAmount;}
    public java.time.LocalDateTime getOverdraftTime(){return this.overdraftTime;}
    public void setOverdraftTime(java.time.LocalDateTime overdraftTime){this.overdraftTime= overdraftTime;}
    public Boolean getIsVip(){return this.isVip;}
    public void setIsVip(Boolean isVip){this.isVip= isVip;}

    public Double getRewardMoney(){return this.rewardMoney;}
    public void setRewardMoney(Double rewardMoney){this.rewardMoney= rewardMoney;
    }
    
    // 初始化钱包
    public void initialize() {
        this.money = 0.0;
        this.frozenMoney = 0.0;
        this.overdraftLimit = 0.0;
        this.overdraftAmount = 0.0;
        this.isVip = false;
        this.rewardMoney = 0.0;
    }
    
    // 获取可用余额（包括充值本金、奖励金额和可用透支额度）
    public Double getAvailableBalance() {
        Double currentMoney = this.money != null ? this.money : 0.0;
        Double currentRewardMoney = this.rewardMoney != null ? this.rewardMoney : 0.0;
        Double availableOverdraft = this.getIsVip() ? 
            (this.getOverdraftLimit() - this.getOverdraftAmount()) : 0.0;
        return currentMoney + currentRewardMoney + availableOverdraft;
    }
    
    // 冻结资金（从充值本金扣除）
    public boolean freezeMoney(Double amount) {
        Double currentMoney = this.money != null ? this.money : 0.0;
        if (currentMoney < amount) {
            return false; // 余额不足
        }
        this.money = currentMoney - amount;
        this.frozenMoney = (this.frozenMoney != null ? this.frozenMoney : 0.0) + amount;
        return true;
    }
    
    // 冻结资金（先使用奖励金额，再使用充值本金）
    public boolean freezeMoneyWithRewardPriority(Double amount) {
        Double currentMoney = this.money != null ? this.money : 0.0;
        Double currentRewardMoney = this.rewardMoney != null ? this.rewardMoney : 0.0;
        Double currentFrozenMoney = this.frozenMoney != null ? this.frozenMoney : 0.0;
        
        // 检查总可用余额是否足够
        if (currentMoney + currentRewardMoney < amount) {
            return false; // 余额不足
        }
        
        Double remainingAmount = amount;
        
        // 首先使用奖励金额
        if (remainingAmount > 0 && currentRewardMoney > 0) {
            Double amountFromReward = Math.min(remainingAmount, currentRewardMoney);
            this.rewardMoney = currentRewardMoney - amountFromReward;
            remainingAmount -= amountFromReward;
        }
        
        // 然后使用充值本金
        if (remainingAmount > 0 && currentMoney > 0) {
            Double amountFromMoney = Math.min(remainingAmount, currentMoney);
            this.money = currentMoney - amountFromMoney;
            remainingAmount -= amountFromMoney;
        }
        
        // 增加冻结金额
        this.frozenMoney = currentFrozenMoney + amount;
        
        return true;
    }
    
    // 解冻资金
    public boolean unfreezeMoney(Double amount) {
        Double currentFrozenMoney = this.frozenMoney != null ? this.frozenMoney : 0.0;
        if (currentFrozenMoney < amount) {
            return false; // 冻结金额不足
        }
        this.frozenMoney = currentFrozenMoney - amount;
        // 将解冻的资金加到money字段中
        // 注意：由于冻结资金是作为一个整体管理的，解冻时不区分原始来源
        this.money = (this.money != null ? this.money : 0.0) + amount;
        return true;
    }
    
    // 设置VIP状态和透支额度
    public void setVipStatus(Boolean isVip, Double overdraftLimit) {
        this.isVip = isVip;
        this.overdraftLimit = isVip ? overdraftLimit : 0.0;
    }
    
    // 透支功能
    public boolean overdraft(Double amount) {
        if (!this.isVip) {
            return false; // 非VIP用户不能透支
        }
        Double availableOverdraft = this.overdraftLimit - this.overdraftAmount;
        if (availableOverdraft < amount) {
            return false; // 透支额度不足
        }
        this.money = (this.money != null ? this.money : 0.0) + amount;
        this.overdraftAmount += amount;
        this.overdraftTime = java.time.LocalDateTime.now();
        return true;
    }
    
    // 还款功能
    public boolean repayOverdraft(Double amount) {
        if (this.overdraftAmount <= 0) {
            return false; // 没有透支金额需要还款
        }
        Double currentMoney = this.money != null ? this.money : 0.0;
        if (currentMoney < amount) {
            return false; // 余额不足
        }
        Double repayAmount = Math.min(amount, this.overdraftAmount);
        this.money = currentMoney - repayAmount;
        this.overdraftAmount -= repayAmount;
        if (this.overdraftAmount == 0) {
            this.overdraftTime = null;
        }
        return true;
    }
    
    // 计算透支利息（日利率0.1%）
    public Double calculateInterest() {
        if (this.overdraftAmount <= 0 || this.overdraftTime == null) {
            return 0.0;
        }
        long daysBetween = java.time.Duration.between(this.overdraftTime, java.time.LocalDateTime.now()).toDays();
        return this.overdraftAmount * 0.001 * daysBetween; // 日利率0.1%
    }
    
    // 充值功能
    public void recharge(Double amount, Double rewardAmount) {
        this.money = (this.money != null ? this.money : 0.0) + amount;
        if (rewardAmount > 0) {
            this.rewardMoney = (this.rewardMoney != null ? this.rewardMoney : 0.0) + rewardAmount;
        }
    }
    
    // 支付功能（从充值本金扣除）
    public boolean payFromPrincipal(Double amount) {
        Double currentMoney = this.money != null ? this.money : 0.0;
        if (currentMoney < amount) {
            return false; // 余额不足
        }
        this.money = currentMoney - amount;
        return true;
    }
    
    // 支付功能（从奖励金额扣除）
    public boolean payFromReward(Double amount) {
        Double currentRewardMoney = this.rewardMoney != null ? this.rewardMoney : 0.0;
        if (currentRewardMoney < amount) {
            return false; // 奖励金额不足
        }
        this.rewardMoney = currentRewardMoney - amount;
        return true;
    }
    
    // 转移资金到另一个钱包
    public void transferTo(Wallet toWallet, Double amount) {
        // 从当前钱包扣除金额
        Double remainingAmount = amount;
        
        // 先从奖励金额扣除
        Double currentRewardMoney = this.rewardMoney != null ? this.rewardMoney : 0.0;
        if (currentRewardMoney >= remainingAmount) {
            this.rewardMoney -= remainingAmount;
            remainingAmount = 0.0;
        } else {
            remainingAmount -= currentRewardMoney;
            this.rewardMoney = 0.0;
        }
        
        // 再从充值本金扣除
        if (remainingAmount > 0) {
            this.money -= remainingAmount;
        }
        
        // 增加目标钱包的余额
        toWallet.money = (toWallet.money != null ? toWallet.money : 0.0) + amount;
    }

}