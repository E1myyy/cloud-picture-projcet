package com.eimy.yunpicturebackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("com.eimy.yunpicturebackend.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
public class YunpictureBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(YunpictureBackendApplication.class, args);
    }


}
