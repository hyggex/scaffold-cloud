package com.scaffold.resource;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * 文件上传模块启动类
 *
 * @author 8800
 */
@EnableConfigurationProperties
@MapperScan("com.scaffold.resource.mapper")
@SpringBootApplication(scanBasePackages = "com.scaffold")
public class ResourceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ResourceApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾟ  文件服务启动成功   ლ(´ڡ`ლ)ﾟ  ");
    }
}
