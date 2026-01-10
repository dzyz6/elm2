package cn.edu.tju.elm.service;


import cn.edu.tju.core.model.HttpResult;
import cn.edu.tju.elm.model.Points;
import cn.edu.tju.elm.model.PointsDetail;
import cn.edu.tju.elm.model.PointsRule;
import cn.edu.tju.elm.repository.PointsDetailMapper;
import cn.edu.tju.elm.repository.PointsMapper;
import cn.edu.tju.elm.repository.PointsRuleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PointsService {

    @Autowired
    PointsMapper pointsMapper;

    @Autowired
    PointsDetailMapper pointsDetailMapper;

    @Autowired
    PointsRuleMapper pointsRuleMapper;

    /**
     * 获取或创建积分账户
     */
    private Points getOrCreatePoints(Long userid) {
        Optional<Points> pointsOpt = pointsMapper.findByUserid(userid);
        if (pointsOpt.isPresent()) {
            return pointsOpt.get();
        }
        // 如果用户没有积分账户，自动创建一个
        Points newPoints = new Points();
        newPoints.setUserid(userid);
        newPoints.setTotalPoints(0);
        newPoints.setAvailablePoints(0);
        return pointsMapper.save(newPoints);
    }

    /**
     * 更新积分账户的可用积分
     */
    private void updateAvailablePoints(Long userid) {
        Points points = getOrCreatePoints(userid);
        LocalDateTime now = LocalDateTime.now();
        List<PointsDetail> availableDetails = pointsDetailMapper.findAvailablePointsByUseridOrderByExpireTime(userid, now);
        int availablePoints = availableDetails.stream()
                .filter(detail -> detail.getStatus().equals("AVAILABLE") && detail.getPoints() > 0)
                .mapToInt(PointsDetail::getPoints)
                .sum();
        points.setAvailablePoints(availablePoints);
        pointsMapper.save(points);
    }

    /**
     * 获取积分账户
     */
    public HttpResult<Points> getPoints(Long userid) {
        Points points = getOrCreatePoints(userid);
        // 更新可用积分
        updateAvailablePoints(userid);
        points = pointsMapper.findByUserid(userid).orElse(points);
        return HttpResult.success(points);
    }

    /**
     * 添加积分（根据规则）
     * @param userid 用户ID
     * @param channel 渠道（ORDER、COMMENT、ACTIVITY等）
     * @param amount 金额或数量（用于计算积分）
     * @param relatedId 关联业务ID
     * @param description 描述
     */
    @Transactional
    public HttpResult<Points> addPoints(Long userid, String channel, Double amount, Long relatedId, String description) {
        // 查找有效的积分获取规则
        LocalDateTime now = LocalDateTime.now();
        List<PointsRule> rules = pointsRuleMapper.findActiveRulesByChannelAndType(channel, "EARN", now);
        
        if (rules.isEmpty()) {
            return HttpResult.failure("500", "未找到有效的积分获取规则");
        }

        // 使用第一个有效规则（可以扩展为支持多个规则）
        PointsRule rule = rules.get(0);
        
        // 计算积分数量
        Integer pointsAmount;
        if (rule.getFixedPoints() != null && rule.getFixedPoints() > 0) {
            pointsAmount = rule.getFixedPoints();
        } else if (rule.getRatio() != null && rule.getRatio() > 0 && amount != null) {
            pointsAmount = (int) (amount * rule.getRatio());
        } else {
            return HttpResult.failure("500", "积分规则配置错误");
        }

        if (pointsAmount <= 0) {
            return HttpResult.failure("500", "计算得到的积分数为0或负数");
        }

        // 获取或创建积分账户
        Points points = getOrCreatePoints(userid);

        // 计算过期时间
        LocalDateTime expireTime = null;
        if (rule.getValidDays() != null && rule.getValidDays() > 0) {
            expireTime = now.plusDays(rule.getValidDays());
        }

        // 创建积分明细
        PointsDetail detail = new PointsDetail();
        detail.setUserid(userid);
        detail.setPoints(pointsAmount);
        detail.setType(channel);
        detail.setExpireTime(expireTime);
        detail.setRelatedId(relatedId);
        detail.setDescription(description != null ? description : rule.getDescription());
        detail.setStatus("AVAILABLE");
        pointsDetailMapper.save(detail);

        // 更新积分账户
        points.setTotalPoints(points.getTotalPoints() + pointsAmount);
        updateAvailablePoints(userid);
        points = pointsMapper.findByUserid(userid).orElse(points);

        return HttpResult.success(points);
    }

    /**
     * 消费积分（优先使用快过期的积分）
     * @param userid 用户ID
     * @param pointsAmount 要消费的积分数量
     * @param channel 消费渠道（EXCHANGE、DEDUCTION等）
     * @param relatedId 关联业务ID
     * @param description 描述
     */
    @Transactional
    public HttpResult<Points> consumePoints(Long userid, Integer pointsAmount, String channel, Long relatedId, String description) {
        Points points = getOrCreatePoints(userid);
        
        // 更新可用积分
        updateAvailablePoints(userid);
        points = pointsMapper.findByUserid(userid).orElse(points);

        if (points.getAvailablePoints() < pointsAmount) {
            return HttpResult.failure("500", "可用积分不足");
        }

        // 查找可用的积分明细，按过期时间升序排列（优先使用快过期的）
        LocalDateTime now = LocalDateTime.now();
        List<PointsDetail> availableDetails = pointsDetailMapper.findAvailablePointsByUseridOrderByExpireTime(userid, now);
        
        int remainingAmount = pointsAmount;
        for (PointsDetail detail : availableDetails) {
            if (remainingAmount <= 0) {
                break;
            }
            if (detail.getStatus().equals("AVAILABLE") && detail.getPoints() > 0) {
                int detailPoints = detail.getPoints();
                if (detailPoints <= remainingAmount) {
                    // 该明细的积分全部使用
                    detail.setStatus("USED");
                    detail.setDescription(detail.getDescription() + " -> 已用于: " + description);
                    remainingAmount -= detailPoints;
                } else {
                    // 该明细的积分部分使用，需要拆分
                    // 创建新的已使用明细
                    PointsDetail usedDetail = new PointsDetail();
                    usedDetail.setUserid(userid);
                    usedDetail.setPoints(-remainingAmount);
                    usedDetail.setType(channel);
                    usedDetail.setRelatedId(relatedId);
                    usedDetail.setDescription(description);
                    usedDetail.setStatus("USED");
                    usedDetail.setExpireTime(detail.getExpireTime());
                    pointsDetailMapper.save(usedDetail);

                    // 更新原明细的积分数量
                    detail.setPoints(detailPoints - remainingAmount);
                    remainingAmount = 0;
                }
                pointsDetailMapper.save(detail);
            }
        }

        if (remainingAmount > 0) {
            return HttpResult.failure("500", "积分消费失败，可用积分不足");
        }

        // 创建消费明细记录
        PointsDetail consumeDetail = new PointsDetail();
        consumeDetail.setUserid(userid);
        consumeDetail.setPoints(-pointsAmount);
        consumeDetail.setType(channel);
        consumeDetail.setRelatedId(relatedId);
        consumeDetail.setDescription(description);
        consumeDetail.setStatus("USED");
        pointsDetailMapper.save(consumeDetail);

        // 更新积分账户
        points.setTotalPoints(points.getTotalPoints() - pointsAmount);
        updateAvailablePoints(userid);
        points = pointsMapper.findByUserid(userid).orElse(points);

        return HttpResult.success(points);
    }

    /**
     * 查询积分明细
     */
    public HttpResult<List<PointsDetail>> getPointsDetails(Long userid) {
        List<PointsDetail> details = pointsDetailMapper.findByUserid(userid);
        return HttpResult.success(details);
    }

    /**
     * 查询可用积分明细
     */
    public HttpResult<List<PointsDetail>> getAvailablePointsDetails(Long userid) {
        LocalDateTime now = LocalDateTime.now();
        List<PointsDetail> details = pointsDetailMapper.findAvailablePointsByUseridOrderByExpireTime(userid, now);
        return HttpResult.success(details);
    }

    /**
     * 处理过期积分（定时任务调用）
     */
    @Transactional
    public HttpResult<Integer> processExpiredPoints() {
        LocalDateTime now = LocalDateTime.now();
        List<PointsDetail> expiredDetails = pointsDetailMapper.findAllExpiredPoints(now);
        
        int expiredCount = 0;
        for (PointsDetail detail : expiredDetails) {
            if (detail.getStatus().equals("AVAILABLE") && detail.getPoints() > 0) {
                detail.setStatus("EXPIRED");
                detail.setDescription(detail.getDescription() + " -> 已过期");
                pointsDetailMapper.save(detail);
                expiredCount++;
                
                // 更新用户的可用积分
                updateAvailablePoints(detail.getUserid());
            }
        }
        
        return HttpResult.success(expiredCount);
    }

    /**
     * 创建积分规则
     */
    public HttpResult<PointsRule> addPointsRule(PointsRule rule) {
        if (rule.getRuleName() == null || rule.getRuleName().isEmpty()) {
            return HttpResult.failure("500", "规则名称不能为空");
        }
        if (rule.getChannel() == null || rule.getChannel().isEmpty()) {
            return HttpResult.failure("500", "渠道不能为空");
        }
        if (rule.getRuleType() == null || rule.getRuleType().isEmpty()) {
            return HttpResult.failure("500", "规则类型不能为空");
        }
        
        // 检查规则名称是否已存在
        Optional<PointsRule> existingRule = pointsRuleMapper.findByRuleName(rule.getRuleName());
        if (existingRule.isPresent()) {
            return HttpResult.failure("500", "规则名称已存在");
        }

        if (rule.getIsEnabled() == null) {
            rule.setIsEnabled(true);
        }
        
        return HttpResult.success(pointsRuleMapper.save(rule));
    }

    /**
     * 更新积分规则
     */
    public HttpResult<PointsRule> updatePointsRule(PointsRule rule) {
        if (rule.getId() == null) {
            return HttpResult.failure("500", "规则ID不能为空");
        }
        
        Optional<PointsRule> existingRuleOpt = pointsRuleMapper.findById(rule.getId());
        if (!existingRuleOpt.isPresent()) {
            return HttpResult.failure("500", "规则不存在");
        }

        PointsRule existingRule = existingRuleOpt.get();
        if (rule.getRuleName() != null) {
            existingRule.setRuleName(rule.getRuleName());
        }
        if (rule.getChannel() != null) {
            existingRule.setChannel(rule.getChannel());
        }
        if (rule.getRuleType() != null) {
            existingRule.setRuleType(rule.getRuleType());
        }
        if (rule.getRatio() != null) {
            existingRule.setRatio(rule.getRatio());
        }
        if (rule.getFixedPoints() != null) {
            existingRule.setFixedPoints(rule.getFixedPoints());
        }
        if (rule.getValidDays() != null) {
            existingRule.setValidDays(rule.getValidDays());
        }
        if (rule.getIsEnabled() != null) {
            existingRule.setIsEnabled(rule.getIsEnabled());
        }
        if (rule.getStartTime() != null) {
            existingRule.setStartTime(rule.getStartTime());
        }
        if (rule.getEndTime() != null) {
            existingRule.setEndTime(rule.getEndTime());
        }
        if (rule.getDescription() != null) {
            existingRule.setDescription(rule.getDescription());
        }

        return HttpResult.success(pointsRuleMapper.save(existingRule));
    }

    /**
     * 查询积分规则
     */
    public HttpResult<List<PointsRule>> getPointsRules(String channel, String ruleType) {
        List<PointsRule> rules;
        if (channel != null && ruleType != null) {
            rules = pointsRuleMapper.findByChannelAndRuleType(channel, ruleType);
        } else {
            rules = pointsRuleMapper.findAll();
        }
        return HttpResult.success(rules);
    }

    /**
     * 根据ID查询积分规则
     */
    public HttpResult<PointsRule> getPointsRuleById(Long id) {
        Optional<PointsRule> rule = pointsRuleMapper.findById(id);
        if (!rule.isPresent()) {
            return HttpResult.failure("500", "规则不存在");
        }
        return HttpResult.success(rule.get());
    }

    /**
     * 删除积分规则
     */
    public HttpResult<Void> deletePointsRule(Long id) {
        if (!pointsRuleMapper.existsById(id)) {
            return HttpResult.failure("500", "规则不存在");
        }
        pointsRuleMapper.deleteById(id);
        return HttpResult.success();
    }
}

