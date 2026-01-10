package cn.edu.tju.elm.repository;


import cn.edu.tju.elm.model.PointsDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PointsDetailMapper extends JpaRepository<PointsDetail, Long> {
    List<PointsDetail> findByUserid(Long userid);

    List<PointsDetail> findByUseridAndStatus(Long userid, String status);

    @Query("SELECT pd FROM PointsDetail pd WHERE pd.userid = :userid AND pd.status = 'AVAILABLE' AND pd.expireTime > :now ORDER BY pd.expireTime ASC")
    List<PointsDetail> findAvailablePointsByUseridOrderByExpireTime(@Param("userid") Long userid, @Param("now") LocalDateTime now);

    @Query("SELECT pd FROM PointsDetail pd WHERE pd.userid = :userid AND pd.status = 'AVAILABLE' AND pd.expireTime <= :now")
    List<PointsDetail> findExpiredPointsByUserid(@Param("userid") Long userid, @Param("now") LocalDateTime now);

    @Query("SELECT pd FROM PointsDetail pd WHERE pd.status = 'AVAILABLE' AND pd.expireTime IS NOT NULL AND pd.expireTime <= :now")
    List<PointsDetail> findAllExpiredPoints(@Param("now") LocalDateTime now);
}

