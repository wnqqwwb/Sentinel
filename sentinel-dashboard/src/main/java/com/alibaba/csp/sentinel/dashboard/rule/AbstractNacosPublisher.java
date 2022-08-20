package com.alibaba.csp.sentinel.dashboard.rule;

import com.alibaba.csp.sentinel.dashboard.rule.nacos.config.NacosConfigUtil;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.util.AssertUtil;
import com.alibaba.nacos.api.config.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author fanls
 */
@Slf4j
public abstract class AbstractNacosPublisher<T> implements DynamicRulePublisher<List<T>> {

    @Resource
    private ConfigService configService;

    @Resource
    private Converter<List<T>, String> converter;

    @Override
    public void publish(String appName, List<T> rules) throws Exception {
        AssertUtil.notEmpty(appName, "app name cannot be empty");
        if (CollectionUtils.isEmpty(rules)) {
            return;
        }
        // 将sentinel规则推送到nacos
        boolean result = configService.publishConfig(appName + getPostfix(), NacosConfigUtil.GROUP_ID, converter.convert(rules));

        if (!result) {
            log.error("{} rules publish failed", appName);
        }
    }

    /**
     * 获取dataId后缀
     *
     * @return data id postfix
     */
    protected abstract String getPostfix();
}
