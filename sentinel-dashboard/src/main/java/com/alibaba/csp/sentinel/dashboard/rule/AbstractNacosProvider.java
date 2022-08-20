package com.alibaba.csp.sentinel.dashboard.rule;

import com.alibaba.csp.sentinel.dashboard.rule.nacos.config.NacosConfigUtil;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.nacos.api.config.ConfigService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @author fanls
 */
@Slf4j
public abstract class AbstractNacosProvider<T> implements DynamicRuleProvider<List<T>> {

    private static final long TIMEOUT_IN_MILLS = 3000;

    @Resource
    private ConfigService configService;

    @Resource
    private Converter<String, List<T>> converter;

    @Override
    public List<T> getRules(String appName) throws Exception {
        // 从nacos中获取规则配置
        String rules = configService.getConfig(appName + getPostfix(), NacosConfigUtil.GROUP_ID, TIMEOUT_IN_MILLS);
        if (StringUtil.isBlank(rules)) {
            return Collections.emptyList();
        }
        return converter.convert(rules);
    }

    /**
     * 获取dataId后缀
     *
     * @return data id postfix
     */
    protected abstract String getPostfix();
}
