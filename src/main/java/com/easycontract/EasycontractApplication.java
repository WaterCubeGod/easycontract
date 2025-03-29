package com.easycontract;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = {"com.easycontract.mapper"})
public class EasycontractApplication {

    public static void main(String[] args) {
        SpringApplication.run(EasycontractApplication.class, args);
    }

}
