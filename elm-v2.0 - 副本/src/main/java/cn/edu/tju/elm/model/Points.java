package cn.edu.tju.elm.model;


import cn.edu.tju.core.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "points")
public class Points extends BaseEntity {
    @Column(name = "userid", unique = true, nullable = false)
    private Long userid;

    @Column(name = "total_points")
    private Integer totalPoints;  // 总积分

    @Column(name = "available_points")
    private Integer availablePoints;  // 可用积分（未过期）

    public Long getUserid() {
        return this.userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public Integer getTotalPoints() {
        return this.totalPoints;
    }

    public void setTotalPoints(Integer totalPoints) {
        this.totalPoints = totalPoints;
    }

    public Integer getAvailablePoints() {
        return this.availablePoints;
    }

    public void setAvailablePoints(Integer availablePoints) {
        this.availablePoints = availablePoints;
    }
}

