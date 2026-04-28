package com.cms;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.cms.mapper")
public class CmsApplication {
    public static void main(String[] args) {
        SpringApplication.run(CmsApplication.class, args);
        System.out.println("========================================");
        System.out.println("    CMS系统启动成功！");
        System.out.println("    访问地址: http://localhost:8080");
        System.out.println("========================================");
    }
}
