package com.alibaba.csp.sentinel.dashboard.rule.nacos;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.ParamFlowRuleCorrectEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.ParamFlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.AbstractNacosPublisher;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.config.NacosConfigUtil;
import com.alibaba.csp.sentinel.util.AssertUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fanls
 */
@Component
public class ParamFlowRuleNacosPublisher extends AbstractNacosPublisher<ParamFlowRuleCorrectEntity> {

    /**
     * 参数限流与客户端解析的不一致，所以这里要进行转换
     *
     * @param appName 应用名称
     * @param rules   规则集合
     * @throws Exception e
     */
    public void publishAvailable(String appName, List<ParamFlowRuleEntity> rules) throws Exception {
        AssertUtil.notEmpty(appName, "app name cannot be empty");
        if (rules == null) {
            return;
        }
        // 转换成客户端能够解析的
        List<ParamFlowRuleCorrectEntity> list = rules.stream().map(rule -> {
            ParamFlowRuleCorrectEntity entity = new ParamFlowRuleCorrectEntity();
            BeanUtils.copyProperties(rule, entity);
            return entity;
        }).collect(Collectors.toList());
        // 将转换后的规则推送到nacos
        super.publish(appName, list);
    }

    @Override
    protected String getPostfix() {
        return NacosConfigUtil.FLOW_DATA_ID_POSTFIX;
    }
}
