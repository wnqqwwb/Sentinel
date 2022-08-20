package com.alibaba.csp.sentinel.dashboard.rule.nacos;


import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.DegradeRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.AbstractNacosProvider;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.config.NacosConfigUtil;
import org.springframework.stereotype.Component;

/**
 * @author fanls
 */
@Component
public class DegradeRuleNacosProvider extends AbstractNacosProvider<DegradeRuleEntity> {
    @Override
    protected String getPostfix() {
        return NacosConfigUtil.DEGRADE_DATA_ID_POSTFIX;
    }
}
