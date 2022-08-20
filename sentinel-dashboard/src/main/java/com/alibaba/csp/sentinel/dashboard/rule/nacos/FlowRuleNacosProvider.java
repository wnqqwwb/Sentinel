package com.alibaba.csp.sentinel.dashboard.rule.nacos;


import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.FlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.AbstractNacosProvider;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.config.NacosConfigUtil;
import org.springframework.stereotype.Component;

/**
 * @author fanls
 */
@Component
public class FlowRuleNacosProvider extends AbstractNacosProvider<FlowRuleEntity> {
    @Override
    protected String getPostfix() {
        return NacosConfigUtil.FLOW_DATA_ID_POSTFIX;
    }
}
