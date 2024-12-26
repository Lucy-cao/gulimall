package com.atguigu.gulimall.order;

import com.alibaba.cloud.spring.boot.oss.autoconfigure.OssContextAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * RabbitMQ引入
 * 1、引入pom依赖spring-boot-starter-amqp
 * 2、给容器中注入组件
 *      AmqpAdmin、RabbitTemplate、RabbitMessagingTemplate、RabbitConnectionFactoryBeanConfigurer
 *      AmqpAdmin：新建交换器、队列、绑定关系
 *      RabbitTemplate：发送消息
 * 3、添加配置参数：配置参数都在RabbitProperties中，在application.yml中以spring.rabbitmq开头
 * 4、启用RabbitMQ服务 @EnableRabbit
 */
@MapperScan("com/atguigu/gulimall/order/dao")
@SpringBootApplication(exclude = {OssContextAutoConfiguration.class})
@EnableDiscoveryClient
@EnableRabbit
public class GulimallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallOrderApplication.class, args);
    }

}
