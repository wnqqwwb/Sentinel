package com.alibaba.csp.sentinel.dashboard.datasource.entity.rule;

import com.alibaba.csp.sentinel.slots.block.Rule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowClusterConfig;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

/**
 * @author fanls
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParamFlowRuleCorrectEntity implements RuleEntity {
    private Long id;
    private String app;
    private String ip;
    private Integer port;
    private String limitApp;
    private String resource;
    private Date gmtCreate;

    private int grade = 1;
    private Integer paramIdx;
    private double count;
    private int controlBehavior = 0;
    private int maxQueueingTimeMs = 0;
    private int burstCount = 0;
    private long durationInSec = 1L;
    private List<ParamFlowItem> paramFlowItemList = new ArrayList<>();
    private Map<Object, Integer> hotItems = new HashMap<>();
    private boolean clusterMode = false;
    private ParamFlowClusterConfig clusterConfig;

    @Override
    public Rule toRule() {
        return null;
    }
}
