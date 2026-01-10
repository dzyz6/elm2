package cn.edu.tju.elm.controller;


import cn.edu.tju.core.model.HttpResult;
import cn.edu.tju.elm.model.Points;
import cn.edu.tju.elm.model.PointsDetail;
import cn.edu.tju.elm.model.PointsRule;
import cn.edu.tju.elm.service.PointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/points")
public class PointsController {
    @Autowired
    PointsService pointsService;

    /**
     * 获取积分账户
     */
    @GetMapping("/getPoints")
    public HttpResult<Points> getPoints(@RequestParam Long userid) throws Exception {
        return pointsService.getPoints(userid);
    }

    /**
     * 添加积分（根据规则）
     * @param userid 用户ID
     * @param channel 渠道（ORDER、COMMENT、ACTIVITY等）
     * @param amount 金额或数量（用于计算积分）
     * @param relatedId 关联业务ID（可选）
     * @param description 描述（可选）
     */
    @PostMapping("/addPoints")
    public HttpResult<Points> addPoints(@RequestParam Long userid,
                                        @RequestParam String channel,
                                        @RequestParam(required = false) Double amount,
                                        @RequestParam(required = false) Long relatedId,
                                        @RequestParam(required = false) String description) throws Exception {
        return pointsService.addPoints(userid, channel, amount, relatedId, description);
    }

    /**
     * 消费积分（优先使用快过期的积分）
     * @param userid 用户ID
     * @param pointsAmount 要消费的积分数量
     * @param channel 消费渠道（EXCHANGE、DEDUCTION等）
     * @param relatedId 关联业务ID（可选）
     * @param description 描述（可选）
     */
    @PostMapping("/consumePoints")
    public HttpResult<Points> consumePoints(@RequestParam Long userid,
                                            @RequestParam Integer pointsAmount,
                                            @RequestParam String channel,
                                            @RequestParam(required = false) Long relatedId,
                                            @RequestParam(required = false) String description) throws Exception {
        return pointsService.consumePoints(userid, pointsAmount, channel, relatedId, description);
    }

    /**
     * 查询积分明细
     */
    @GetMapping("/getPointsDetails")
    public HttpResult<List<PointsDetail>> getPointsDetails(@RequestParam Long userid) throws Exception {
        return pointsService.getPointsDetails(userid);
    }

    /**
     * 查询可用积分明细
     */
    @GetMapping("/getAvailablePointsDetails")
    public HttpResult<List<PointsDetail>> getAvailablePointsDetails(@RequestParam Long userid) throws Exception {
        return pointsService.getAvailablePointsDetails(userid);
    }

    /**
     * 处理过期积分（定时任务调用）
     */
    @PostMapping("/processExpiredPoints")
    public HttpResult<Integer> processExpiredPoints() throws Exception {
        return pointsService.processExpiredPoints();
    }

    /**
     * 创建积分规则
     */
    @PostMapping("/addPointsRule")
    public HttpResult<PointsRule> addPointsRule(@RequestBody PointsRule rule) throws Exception {
        return pointsService.addPointsRule(rule);
    }

    /**
     * 更新积分规则
     */
    @PostMapping("/updatePointsRule")
    public HttpResult<PointsRule> updatePointsRule(@RequestBody PointsRule rule) throws Exception {
        return pointsService.updatePointsRule(rule);
    }

    /**
     * 查询积分规则
     * @param channel 渠道（可选）
     * @param ruleType 规则类型（可选，EARN或CONSUME）
     */
    @GetMapping("/getPointsRules")
    public HttpResult<List<PointsRule>> getPointsRules(@RequestParam(required = false) String channel,
                                                       @RequestParam(required = false) String ruleType) throws Exception {
        return pointsService.getPointsRules(channel, ruleType);
    }

    /**
     * 根据ID查询积分规则
     */
    @GetMapping("/getPointsRuleById")
    public HttpResult<PointsRule> getPointsRuleById(@RequestParam Long id) throws Exception {
        return pointsService.getPointsRuleById(id);
    }

    /**
     * 删除积分规则
     */
    @PostMapping("/deletePointsRule")
    public HttpResult<Void> deletePointsRule(@RequestParam Long id) throws Exception {
        return pointsService.deletePointsRule(id);
    }
}

