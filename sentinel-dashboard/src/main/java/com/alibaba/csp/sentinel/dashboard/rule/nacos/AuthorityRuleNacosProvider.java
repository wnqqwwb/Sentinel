package com.alibaba.csp.sentinel.dashboard.rule.nacos;


import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.AuthorityRuleCorrectEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.AuthorityRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.AbstractNacosProvider;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.config.NacosConfigUtil;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRule;
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
public class AuthorityRuleNacosProvider extends AbstractNacosProvider<AuthorityRuleCorrectEntity> {

    /**
     * 权限控制与客户端解析的不一致，所以这里要进行转换
     *
     * @param appName 应用名称
     * @return List<AuthorityRuleEntity>
     * @throws Exception e
     */
    public List<AuthorityRuleEntity> getAvailableRules(String appName) throws Exception {
        // 获取规则列表
        List<AuthorityRuleCorrectEntity> rules = super.getRules(appName);
        if (CollectionUtils.isEmpty(rules)) {
            return Collections.emptyList();
        }
        // 转换后的权限控制对象，sentinel控制台能够解析的格式
        return rules.stream().map(rule -> {
            AuthorityRule authorityRule = new AuthorityRule();
            BeanUtils.copyProperties(rule, authorityRule);
            AuthorityRuleEntity entity = AuthorityRuleEntity.fromAuthorityRule(rule.getApp(), rule.getIp(), rule.getPort(), authorityRule);
            entity.setId(rule.getId());
            entity.setGmtCreate(rule.getGmtCreate());
            return entity;
        }).collect(Collectors.toList());
    }

    @Override
    protected String getPostfix() {
        return NacosConfigUtil.AUTHORITY_DATA_ID_POSTFIX;
    }
}
