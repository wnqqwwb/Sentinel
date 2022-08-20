package com.alibaba.csp.sentinel.dashboard.rule.nacos;


import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.SystemRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.AbstractNacosProvider;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.config.NacosConfigUtil;
import org.springframework.stereotype.Component;

/**
 * @author fanls
 */
@Component
public class SystemRuleNacosProvider extends AbstractNacosProvider<SystemRuleEntity> {
    @Override
    protected String getPostfix() {
        return NacosConfigUtil.SYSTEM_DATA_ID_POSTFIX;
    }
}
