package cn.itcast.wanxinp2p.account;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

// exclude = {MongoAutoConfiguration.class} : 在启动类上通过禁用指定的自动化配置来避免加载不必要的自动化配置
@SpringBootApplication(exclude = {MongoAutoConfiguration.class},scanBasePackages = {"org.dromara.hmily","cn.itcast.wanxinp2p.account"})
@EnableDiscoveryClient
@MapperScan("cn.itcast.wanxinp2p.account.mapper") //设置mapper接口的扫描包
public class AccountApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountApplication.class, args);
    }

}
