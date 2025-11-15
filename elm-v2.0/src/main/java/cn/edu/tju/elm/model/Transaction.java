package cn.edu.tju.elm.model;

import cn.edu.tju.core.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.annotations.Collate;

@Entity
@Table(name = "transaction")
public class Transaction extends BaseEntity {

    // 交易类型常量
    public static final int TYPE_RECHARGE = 0;      // 充值
    public static final int TYPE_WITHDRAW = 1;      // 提现
    public static final int TYPE_PAYMENT = 2;       // 支付
    public static final int TYPE_FREEZE = 3;        // 冻结
    public static final int TYPE_UNFREEZE = 4;      // 解冻
    public static final int TYPE_OVERDRAFT = 5;     // 透支
    public static final int TYPE_REPAY = 6;        // 还款
    public static final int TYPE_INTEREST = 7;     // 利息

    // 交易状态常量
    public static final int STATUS_PENDING = 0;    // 待处理
    public static final int STATUS_COMPLETED = 1;  // 已完成
    public static final int STATUS_FAILED = 2;     // 失败
    public static final int STATUS_CANCELLED = 3;  // 已取消

    @Column(name="money")
    private Double money;

    @Column(name = "type")
    private Integer type;

    @Column(name = "fromwallet")
    private Long fromwallet;

    @Column(name = "towallet")
    private Long towallet;

    @Column(name = "status")
    private Integer status;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "description")
    private String description;

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    // type 的 getter 和 setter
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    // fromwallet 的 getter 和 setter
    public Long getFromwallet() {
        return fromwallet;
    }

    public void setFromwallet(Long fromwallet) {
        this.fromwallet = fromwallet;
    }

    // towallet 的 getter 和 setter
    public Long getTowallet() {
        return towallet;
    }

    public void setTowallet(Long towallet) {
        this.towallet = towallet;
    }

    // status 的 getter 和 setter
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    // orderId 的 getter 和 setter
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    // description 的 getter 和 setter
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
