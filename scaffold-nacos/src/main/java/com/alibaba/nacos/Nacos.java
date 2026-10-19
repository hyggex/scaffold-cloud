/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.alibaba.nacos;

import com.alibaba.nacos.sys.filter.NacosTypeExcludeFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationExcludeFilter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.TypeExcludeFilter;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;

/**
 * Nacos 注册与配置中心启动类。
 *
 * <p>使用 @SpringBootApplication + @ComponentScan 配合自定义 TypeFilter，
 * 控制各模块启用状态，单机模式启动。</p>
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.alibaba.nacos", excludeFilters = {
    @Filter(type = FilterType.CUSTOM, classes = {NacosTypeExcludeFilter.class}),
    @Filter(type = FilterType.CUSTOM, classes = {TypeExcludeFilter.class}),
    @Filter(type = FilterType.CUSTOM, classes = {AutoConfigurationExcludeFilter.class})})
@ServletComponentScan
public class Nacos {

    public static void main(String[] args) {
        // 单机模式启动（false 为集群模式，需配合 conf/cluster.conf 使用）
        System.setProperty("nacos.standalone", "true");
        System.setProperty("server.tomcat.accesslog.enabled", "false");
        SpringApplication.run(Nacos.class, args);
        System.out.println("(♥◠‿◠)ﾉﾟ  注册中心启动成功   ლ(´ڡ`ლ)ﾟ  ");
    }
}
