package cn.edu.tju.elm.model;


import cn.edu.tju.core.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "points_detail")
public class PointsDetail extends BaseEntity {
    @Column(name = "userid", nullable = false)
    private Long userid;

    @Column(name = "points")
    private Integer points;  // 积分数量（正数为获得，负数为消费）

    @Column(name = "type")
    private String type;  // 类型：ORDER（订单）、COMMENT（评论）、ACTIVITY（活动）、EXCHANGE（兑换）、DEDUCTION（抵扣）、EXPIRE（过期）

    @Column(name = "expire_time")
    private java.time.LocalDateTime expireTime;  // 过期时间

    @Column(name = "related_id")
    private Long relatedId;  // 关联的业务ID（如订单ID、活动ID等）

    @Column(name = "description")
    private String description;  // 描述信息

    @Column(name = "status")
    private String status;  // 状态：AVAILABLE（可用）、USED（已使用）、EXPIRED（已过期）

    public Long getUserid() {
        return this.userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public Integer getPoints() {
        return this.points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public java.time.LocalDateTime getExpireTime() {
        return this.expireTime;
    }

    public void setExpireTime(java.time.LocalDateTime expireTime) {
        this.expireTime = expireTime;
    }

    public Long getRelatedId() {
        return this.relatedId;
    }

    public void setRelatedId(Long relatedId) {
        this.relatedId = relatedId;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

