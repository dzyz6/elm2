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
}
