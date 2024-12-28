package com.atguigu.gulimall.order.config;

import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class MyRabbitInit {
	@Autowired
	RabbitTemplate rabbitTemplate;
	/**
	 * 定制RabbitTemplate，设置消息确认机制
	 */
	@PostConstruct //设置MyRabbitConfig对象创建完成之后就调用此方法
	public void initRabbitTemplate() {
		rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
			/**
			 * 消息发送到消息代理之后，就触发这个回调函数
			 * @param correlationData 当前消息的唯一关联数据（唯一id）
			 * @param ack 消息代理Broker是否成功收到消息
			 * @param cause 失败的原因
			 */
			@Override
			public void confirm(CorrelationData correlationData, boolean ack, String cause) {
				System.out.println("ConfirmCallback: correlationData=" + correlationData + "=> ack=" + ack + "=> cause=" + cause);
			}
		});

		rabbitTemplate.setReturnsCallback(new RabbitTemplate.ReturnsCallback() {
			/**
			 * 如果消息没有投递到指定的队列，触发此回调函数
			 * @param returned
			 */
			@Override
			public void returnedMessage(ReturnedMessage returned) {
				System.out.println("returned = " + returned);
			}
		});
	}
}
