package com.alibaba.csp.sentinel.dashboard.rule.nacos;


import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.ParamFlowRuleCorrectEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.ParamFlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.AbstractNacosProvider;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.config.NacosConfigUtil;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fanls
 */
@Component
public class ParamFlowRuleNacosProvider extends AbstractNacosProvider<ParamFlowRuleCorrectEntity> {

    /**
     * 参数限流与客户端解析的不一致，所以这里要进行转换
     *
     * @param appName 应用名称
     * @return List<ParamFlowRuleEntity>
     * @throws Exception e
     */
    public List<ParamFlowRuleEntity> getAvailableRules(String appName) throws Exception {
        // 获取规则列表
        List<ParamFlowRuleCorrectEntity> rules = super.getRules(appName);
        if (CollectionUtils.isEmpty(rules)) {
            return Collections.emptyList();
        }
        // 转换后的参数限流对象，sentinel控制台能够解析的格式
        return rules.stream().map(rule -> {
            ParamFlowRule paramFlowRule = new ParamFlowRule();
            BeanUtils.copyProperties(rule, paramFlowRule);
            ParamFlowRuleEntity entity = ParamFlowRuleEntity.fromParamFlowRule(rule.getApp(), rule.getIp(), rule.getPort(), paramFlowRule);
            entity.setId(rule.getId());
            entity.setGmtCreate(rule.getGmtCreate());
            return entity;
        }).collect(Collectors.toList());
    }

    @Override
    protected String getPostfix() {
        return NacosConfigUtil.PARAM_FLOW_DATA_ID_POSTFIX;
    }
}
