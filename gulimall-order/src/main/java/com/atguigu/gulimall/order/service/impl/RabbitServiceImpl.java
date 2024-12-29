package com.atguigu.gulimall.order.service.impl;

import com.atguigu.gulimall.order.entity.OrderReturnReasonEntity;
import com.atguigu.gulimall.order.entity.OrderSettingEntity;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RabbitListener(queues = {"hello-java-queue"})
public class RabbitServiceImpl {
	//接收消息

	/**
	 * queues: 声明需要监听的所有队列
	 * org.springframework.amqp.core.Message
	 * <p>
	 * Message message：原生消息详细信息，头+体
	 * Object entity：消息的类型，可以直接指定实体类接收
	 * Channel channel：信道
	 */
	@RabbitHandler
	public void receiveMessage(Message message, OrderReturnReasonEntity entity, Channel channel) throws IOException {
		System.out.println("获取消息 entity = " + entity);
		//channel内按顺序自增的一个标识
		long deliveryTag = message.getMessageProperties().getDeliveryTag();
		System.out.println("deliveryTag = " + deliveryTag);
		if (deliveryTag % 2 == 0) {
			//进行手动ack，非批量模式。确认签收获取
			channel.basicAck(deliveryTag, false);
			System.out.println("货物已被签收 deliveryTag = " + deliveryTag);
		} else {
			//手动拒签
			//basicNack(long deliveryTag, boolean multiple, boolean requeue)
			//requeue是否重新入队，如果设为true则重新入队，deliveryTag会变化为最新的顺序；如果设为false，直接丢弃
			channel.basicNack(deliveryTag, false, true);
			System.out.println("货物未被签收 deliveryTag = " + deliveryTag);

			//basicReject不支持批量签收，其他同basicNack
			//basicReject(long deliveryTag, boolean requeue)
//			channel.basicReject(deliveryTag, false);
		}

	}

	@RabbitHandler
	public void receiveMessage(Message message, OrderSettingEntity entity, Channel channel) throws IOException {
		System.out.println("获取消息 OrderSettingEntity = " + entity);
		channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
	}
}
