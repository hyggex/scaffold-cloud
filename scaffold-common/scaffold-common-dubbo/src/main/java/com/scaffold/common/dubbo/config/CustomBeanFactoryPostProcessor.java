package com.scaffold.common.dubbo.config;

import com.scaffold.common.core.utils.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.cloud.commons.util.InetUtilsProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;

import java.net.Inet6Address;
import java.net.InetAddress;

/**
 * Dubbo 自定义 IP 注入（避免 IP 不正确问题）。
 *
 * <p>Dubbo 默认使用 JVM 启动网卡 IP 注册到 Nacos，但在容器环境或有多网卡时，
 * 可能获取到错误的 IP（如 Docker 网桥 IP）。本类通过 Spring Cloud 的 InetUtils
 * 找到正确的网卡 IP，并设置为系统属性 dubbo.ip.to.registry，让 Dubbo 使用正确的 IP。</p>
 */
public class CustomBeanFactoryPostProcessor implements BeanFactoryPostProcessor, Ordered, EnvironmentAware {

    private static final String DUBBO_IP_TO_REGISTRY = "dubbo.ip.to.registry";

    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        String property = System.getProperty(DUBBO_IP_TO_REGISTRY);
        if (StringUtils.isNotBlank(property)) {
            return;
        }
        InetUtilsProperties properties = Binder.get(environment)
                .bind(InetUtilsProperties.PREFIX, InetUtilsProperties.class)
                .orElseGet(InetUtilsProperties::new);

        try (InetUtils inetUtils = new InetUtils(properties)) {
            String ip = "127.0.0.1";
            InetAddress address = inetUtils.findFirstNonLoopbackAddress();
            if (address != null) {
                if (address instanceof Inet6Address) {
                    String ipv6AddressString = address.getHostAddress();
                    if (ipv6AddressString.contains("%")) {
                        ipv6AddressString = ipv6AddressString.substring(0, ipv6AddressString.indexOf("%"));
                    }
                    ip = ipv6AddressString;
                } else {
                    ip = inetUtils.findFirstNonLoopbackHostInfo().getIpAddress();
                }
            }
            System.setProperty(DUBBO_IP_TO_REGISTRY, ip);
        }
    }
}
