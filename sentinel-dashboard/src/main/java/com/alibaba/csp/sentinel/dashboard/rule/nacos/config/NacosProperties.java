package com.alibaba.csp.sentinel.dashboard.rule.nacos.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author fanls
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "sentinel.dashboard.rule.nacos")
public class NacosProperties {

    private String serverAddr;

    private String namespace;
}
