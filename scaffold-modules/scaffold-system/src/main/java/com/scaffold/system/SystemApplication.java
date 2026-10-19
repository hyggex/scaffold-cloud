package com.scaffold.system;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 系统服务启动类。
 */
@EnableDubbo
@MapperScan("com.scaffold.system.mapper")
@SpringBootApplication(scanBasePackages = "com.scaffold")
public class SystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(SystemApplication.class, args);
        System.out.println("(￣▽￣)  scaffold-system 启动成功");
    }
}
