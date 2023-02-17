package com.af.handle;


import java.io.IOException;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;

/**
 * 
 * @Description: 
 * @author: liaohuiquan  
 * @date: 2022/03/09 21:29:11 
 *
 */
@Component
public class ReceiveHandler {
 
	/**
	 * Topic：通配符，把消息交给符合routing pattern（路由模式） 的队列
	 * @param msg
	 */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "queue_email", durable = "true"),
            exchange = @Exchange(
                    value = "topic.exchange",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC
            ),
            key = {"topic.#.email.#","email.*"}))
    public void raceEmail(String msg, Message message, Channel channel) throws IOException {
        System.out.println(" [邮件服务] received : " + msg + "!");
        
        try {
            int x = 1/0;
            //消息的id
            long deliveryTag = message.getMessageProperties().getDeliveryTag();
            //告诉服务器收到这条消息 已经被我消费了 可以在队列删掉 这样以后就不会再发了 否则消息服务器以为这条消息没处理掉 后续还会在发
            channel.basicAck(deliveryTag,true);
            System.out.println("receiver success");
        } catch (IOException e) {

            //消息处理时，发生异常
            System.out.println("消息处理时，发生异常");
            e.printStackTrace();
            /**
             *
             *第三个request参数，
             * 为true，消息会返回MQ中，可能会存在一直一直来回发消息的情况，这个需要处理
             * 为false时，这条消息直接丢弃了，如果有设置死信队列，则这个消息会发送到死信队列中
             */
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), true,true);
            System.out.println("消息返回MQ中");
        }
    }
 
    //监听短信队列
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "queue_sms", durable = "true"),
            exchange = @Exchange(
                    value = "topic.exchange",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC
            ),
            key = {"topic.#.sms.#"}))
    public void rece_sms(String msg){
        System.out.println(" [短信服务] received : " + msg + "!");
    }
}