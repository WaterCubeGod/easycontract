package com.easycontract;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@SpringBootApplication
@MapperScan(basePackages = {"com.easycontract.mapper"})
@EnableElasticsearchRepositories(basePackages = "com.easycontract.repository")
public class EasycontractApplication {

    public static void main(String[] args) {
        SpringApplication.run(EasycontractApplication.class, args);
    }

}
