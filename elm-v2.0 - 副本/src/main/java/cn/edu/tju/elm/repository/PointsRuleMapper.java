package cn.edu.tju.elm.repository;


import cn.edu.tju.elm.model.PointsRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PointsRuleMapper extends JpaRepository<PointsRule, Long> {
    List<PointsRule> findByChannelAndRuleType(String channel, String ruleType);

    @Query("SELECT pr FROM PointsRule pr WHERE pr.channel = :channel AND pr.ruleType = :ruleType AND pr.isEnabled = true " +
           "AND (pr.startTime IS NULL OR pr.startTime <= :now) AND (pr.endTime IS NULL OR pr.endTime >= :now)")
    List<PointsRule> findActiveRulesByChannelAndType(@Param("channel") String channel, 
                                                      @Param("ruleType") String ruleType, 
                                                      @Param("now") LocalDateTime now);

    Optional<PointsRule> findByRuleName(String ruleName);
}

