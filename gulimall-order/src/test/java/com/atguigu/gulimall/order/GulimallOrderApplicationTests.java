package com.atguigu.gulimall.order;

import com.atguigu.gulimall.order.entity.OrderEntity;
import com.atguigu.gulimall.order.entity.OrderReturnReasonEntity;
import com.atguigu.gulimall.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@Slf4j
@SpringBootTest
class GulimallOrderApplicationTests {
	@Autowired
	OrderService orderService;
	@Autowired
	AmqpAdmin amqpAdmin;
	@Autowired
	RabbitTemplate rabbitTemplate;

	@Test
	void contextLoads() {
		OrderEntity order = new OrderEntity();
		order.setMemberId(23L);
		orderService.save(order);
	}

	/**
	 * 测试RabbitMQ
	 */
	@Test
	public void createExchange() {
		//创建交换器
		//DirectExchange(String name, boolean durable, boolean autoDelete, Map<String, Object> arguments)
		DirectExchange directExchange = new DirectExchange("hello-java-exchange", true, false);
		amqpAdmin.declareExchange(directExchange);
		System.out.println("directExchange = " + directExchange);
	}

	@Test
	public void createQueue() {
		//创建队列
		//Queue(String name, boolean durable, boolean exclusive, boolean autoDelete,
		//			@Nullable Map<String, Object> arguments)
		Queue queue = new Queue("hello-java-queue", true, false, false);
		amqpAdmin.declareQueue(queue);
		System.out.println("queue = " + queue);
	}

	@Test
	public void createBinding() {
		//创建交换器和队列的绑定关系
		//Binding(
		// String destination, 目的地（队列名或交换器名）
		// DestinationType destinationType,目的地类型（队列或交换器）
		// String exchange, 交换器
		// String routingKey, 路由键
		//	@Nullable Map<String, Object> arguments) 其他参数
		Binding binding = new Binding("hello-java-queue", Binding.DestinationType.QUEUE,
				"hello-java-exchange", "hello-java-queue", null);
		amqpAdmin.declareBinding(binding);
		System.out.println("binding = " + binding);
	}

	@Test
	public void sendMessageTest() {
		//send(Message message) 直接发送消息
		//convertAndSend(String exchange, String routingKey, Object object) 转化为字节流并发送消息
		//1、发送字符串
//		String msg = "hello world";
//		rabbitTemplate.convertAndSend("hello-java-exchange", "hello-java-queue", msg);
//		log.info("消息发送成功：{}", msg);

		//2、发送实体对象
		OrderReturnReasonEntity reasonEntity = new OrderReturnReasonEntity();
		reasonEntity.setId(1L);
		reasonEntity.setName("不喜欢的样式");
		reasonEntity.setCreateTime(new Date());
		reasonEntity.setStatus(1);
		reasonEntity.setSort(1);
		rabbitTemplate.convertAndSend("hello-java-exchange", "hello-java-queue", reasonEntity);
		log.info("消息发送成功：{}", reasonEntity);
	}
}
