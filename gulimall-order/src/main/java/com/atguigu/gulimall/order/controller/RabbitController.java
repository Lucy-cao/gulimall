package com.atguigu.gulimall.order.controller;

import com.atguigu.gulimall.order.entity.OrderReturnReasonEntity;
import com.atguigu.gulimall.order.entity.OrderSettingEntity;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.UUID;

@RestController
public class RabbitController {
	@Autowired
	RabbitTemplate rabbitTemplate;

	@GetMapping("/sendMq")
	public String sendMq(@RequestParam(value = "num", defaultValue = "10") Integer num) {
		//发送消息
		for (int i = 0; i < num; i++) {
			//根据单双数放入不一样的数据消息
			CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
			if (i % 2 != 0) {
				OrderReturnReasonEntity reasonEntity = new OrderReturnReasonEntity();
				reasonEntity.setId((long) i);
				reasonEntity.setName("第" + i + "条消息");
				reasonEntity.setCreateTime(new Date());
				reasonEntity.setStatus(1);
				reasonEntity.setSort(i);
				rabbitTemplate.convertAndSend("hello-java-exchange", "hello-java-queue", reasonEntity, correlationData);
			} else {
				OrderSettingEntity entity = new OrderSettingEntity();
				entity.setId((long) i);
				entity.setMemberLevel(i);
				rabbitTemplate.convertAndSend("hello-java-exchange", "hello-java-queue", entity, correlationData);
			}
		}
		return "ok";
	}
}
