package com.scaffold.common.core.utils;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.autoconfigure.thread.Threading;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Spring 工具类。
 */
@Component
public final class SpringUtils extends SpringUtil {

    /**
     * 判断 BeanFactory 中是否存在指定名称的 bean。
     */
    public static boolean containsBean(String name) {
        return getBeanFactory().containsBean(name);
    }

    /**
     * 判断给定类型是否已注册 bean（singleton 或 prototype）。
     */
    public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return getBeanFactory().isSingleton(name);
    }

    /**
     * 获取 bean 的 Class 类型。
     */
    public static Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return getBeanFactory().getType(name);
    }

    /**
     * 获取 bean 的别名列表。
     */
    public static String[] getAliases(String name) throws NoSuchBeanDefinitionException {
        return getBeanFactory().getAliases(name);
    }

    /**
     * 获取 Spring 上下文。
     */
    public static ApplicationContext context() {
        return getApplicationContext();
    }

    /**
     * 判断是否启用了虚拟线程。
     */
    public static boolean isVirtual() {
        return Threading.VIRTUAL.isActive(getBean(Environment.class));
    }
}
