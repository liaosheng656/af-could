package com.af.controller.message;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.af.config.RabbitmqConfig;

/**
 * 消息发送
 * @Description: 
 * @author: liaohuiquan  
 * @date: 2022/03/09 20:40:07 
 *
 */
@RequestMapping("messageSent")
@RestController
public class MessageSentController {

	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	/**
	 * 消息发送测试
	 * @return
	 */
	public String sendTest01() {
		
		for (int x = 0; x < 10; x++) {

			//监听当发送的消息无法到达交换机时或成功到达交换机时，发生回调
			rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback(){

				/**
				 *
				 * @param correlationData 相关配置信息
				 * @param ack true 交换机接收消息成功，false 接收消息失败
				 * @param cause 失败原因
				 * @date 2022/10/14
				 *
				 */
				@Override
				public void confirm(CorrelationData correlationData, boolean ack, String cause) {

					//消息成功到达交换机
					if(ack){
						System.out.println("接收消息成功！");
					}else {
						System.out.println("接收消息失败！");
						//消息接收失败处理
					}
				}
			});


			//这里消息默认是持久化的
			rabbitTemplate.convertAndSend(RabbitmqConfig.EXCHANGE_NAME ,"topic.sms.email", "你好啊 "+x);

			//设置交换机将消息投递给队列时，true 失败时将消息返回给生产者，这个和下面的结合使用
			rabbitTemplate.setMandatory(true);

			rabbitTemplate.setReturnsCallback(new RabbitTemplate.ReturnsCallback(){

				/**
				 *
				 * ReturnedMessage 对象包含以下属性
				 * @param message 消息对象
				 * @param replyCode 错误码
				 * @param replyText 消息对象
				 * @param exchange 交换机
				 * @param routingKey 路由规则
				 * @date 2022/10/14
				 *
				 */
				@Override
				public void returnedMessage(ReturnedMessage returned) {
					System.out.println("交换机向消息队列传递的信息："+returned.toString());
				}
			});
		}
		return "RabbitMQ发送消息成功！";
	}
	
	
}
