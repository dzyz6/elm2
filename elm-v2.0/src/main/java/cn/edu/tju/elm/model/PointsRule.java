package cn.edu.tju.elm.model;


import cn.edu.tju.core.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "points_rule")
public class PointsRule extends BaseEntity {
    @Column(name = "rule_name", nullable = false)
    private String ruleName;  // 规则名称

    @Column(name = "rule_type", nullable = false)
    private String ruleType;  // 规则类型：EARN（获取）、CONSUME（消费）

    @Column(name = "channel", nullable = false)
    private String channel;  // 渠道：ORDER（订单）、COMMENT（评论）、ACTIVITY（活动）、EXCHANGE（兑换商城）、DEDUCTION（订单抵扣）

    @Column(name = "ratio")
    private Double ratio;  // 兑换比例（如订单金额：积分 = 1：10，则ratio=10）

    @Column(name = "fixed_points")
    private Integer fixedPoints;  // 固定积分数量（如果使用固定积分而非比例）

    @Column(name = "valid_days")
    private Integer validDays;  // 有效天数（从获得积分开始计算）

    @Column(name = "is_enabled")
    private Boolean isEnabled;  // 是否启用

    @Column(name = "start_time")
    private java.time.LocalDateTime startTime;  // 规则生效开始时间

    @Column(name = "end_time")
    private java.time.LocalDateTime endTime;  // 规则生效结束时间

    @Column(name = "description")
    private String description;  // 规则描述

    public String getRuleName() {
        return this.ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleType() {
        return this.ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public String getChannel() {
        return this.channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public Double getRatio() {
        return this.ratio;
    }

    public void setRatio(Double ratio) {
        this.ratio = ratio;
    }

    public Integer getFixedPoints() {
        return this.fixedPoints;
    }

    public void setFixedPoints(Integer fixedPoints) {
        this.fixedPoints = fixedPoints;
    }

    public Integer getValidDays() {
        return this.validDays;
    }

    public void setValidDays(Integer validDays) {
        this.validDays = validDays;
    }

    public Boolean getIsEnabled() {
        return this.isEnabled;
    }

    public void setIsEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public java.time.LocalDateTime getStartTime() {
        return this.startTime;
    }

    public void setStartTime(java.time.LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public java.time.LocalDateTime getEndTime() {
        return this.endTime;
    }

    public void setEndTime(java.time.LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

