package com.alibaba.csp.sentinel.dashboard.rule.nacos;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.DegradeRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.AbstractNacosPublisher;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.config.NacosConfigUtil;
import org.springframework.stereotype.Component;

/**
 * @author fanls
 */
@Component
public class DegradeRuleNacosPublisher extends AbstractNacosPublisher<DegradeRuleEntity> {
    @Override
    protected String getPostfix() {
        return NacosConfigUtil.DEGRADE_DATA_ID_POSTFIX;
    }
}
