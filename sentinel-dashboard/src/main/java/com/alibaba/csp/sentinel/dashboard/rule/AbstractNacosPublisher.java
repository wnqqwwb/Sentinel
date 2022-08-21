package com.alibaba.csp.sentinel.dashboard.rule;

import com.alibaba.csp.sentinel.dashboard.rule.nacos.config.NacosConfigUtil;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.util.AssertUtil;
import com.alibaba.nacos.api.config.ConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author fanls
 */
@Slf4j
public abstract class AbstractNacosPublisher<T> implements DynamicRulePublisher<List<T>> {

    @Autowired
    private ConfigService configService;

    @Autowired
    private Converter<List<T>, String> converter;

    @Override
    public void publish(String appName, List<T> rules) throws Exception {
        AssertUtil.notEmpty(appName, "app name cannot be empty");
        if (null == rules) {
            return;
        }
        // 将sentinel规则推送到nacos
        boolean result = configService.publishConfig(appName + getPostfix(), NacosConfigUtil.GROUP_ID, converter.convert(rules));

        if (!result) {
            log.error("{} rules publish failed", appName);
            throw new RuntimeException("rules publish failed");
        }
        // 成功后阻塞一会，由于成功后去获取列表，这个时候还取不到
        TimeUnit.MILLISECONDS.sleep(50);
    }

    /**
     * 获取dataId后缀
     *
     * @return data id postfix
     */
    protected abstract String getPostfix();
}
