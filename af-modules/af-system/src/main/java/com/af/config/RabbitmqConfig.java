package com.af.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * RabbitConfig配置类，这里可以设置交换机和队列持久化
 * @Description: 
 * @author: liaohuiquan  
 * @date: 2022/03/09 17:49:14 
 *
 */
@Configuration
public class RabbitmqConfig {
	
	/**
	 * email队列
	 */
    public static final String QUEUE_EMAIL = "queue_email";
    
    /**
     * sms队列
     */
    public static final String QUEUE_SMS = "queue_sms";
    
    /**
     * topics类型交换机
     */
    public static final String EXCHANGE_NAME="topic.exchange";
    
    /**
     * 路由过滤器，类似正则
     */
    public static final String ROUTINGKEY_EMAIL="topic.#.email.#";
    
    /**
     * 路由过滤器，类似正则
     */
    public static final String ROUTINGKEY_SMS="topic.#.sms.#";
 
    //声明交换机
    @Bean(EXCHANGE_NAME)
    public Exchange exchange(){
        //durable(true) 持久化，mq重启之后交换机还在
        return ExchangeBuilder.topicExchange(EXCHANGE_NAME).durable(true).build();
    }
 
    /**
     *   声明email队列----TTL过期时间
     *   new Queue(QUEUE_EMAIL,true,false,false)
     *   durable="true" 持久化 rabbitmq重启的时候不需要创建新的队列
     *   auto-delete 表示消息队列没有在使用时将被自动删除 默认是false
     *   exclusive  表示该消息队列是否只在当前connection生效,默认是false
     *
     */
    @Bean(QUEUE_EMAIL)
    public Queue emailQueue(){
        Map<String, Object> arguments = new HashMap<>(1);
        //设置队列消息TTL过期时间（单位：ms毫秒），当消息过期后，还没有被消费，该消息也会被删除掉
        arguments.put("x-message-ttl",10000);
        return new Queue(QUEUE_EMAIL,true,false,false,arguments);
//        return new Queue(QUEUE_EMAIL);
    }
    
    /**
     * 声明sms队列
     * @return
     */
    @Bean(QUEUE_SMS)
    public Queue smsQueue(){
        return new Queue(QUEUE_SMS);
    }
 
    /**
     * ROUTINGKEY_EMAIL队列绑定交换机，指定routingKey
     * @param queue
     * @param exchange
     * @return
     */
    @Bean
    public Binding bindingEmail(@Qualifier(QUEUE_EMAIL) Queue queue,
                                @Qualifier(EXCHANGE_NAME) Exchange exchange)
    {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTINGKEY_EMAIL).noargs();
    }
    
    /**
     * ROUTINGKEY_SMS队列绑定交换机，指定routingKey
     * @param queue
     * @param exchange
     * @return
     */
    @Bean
    public Binding bindingSMS(@Qualifier(QUEUE_SMS) Queue queue,
                              @Qualifier(EXCHANGE_NAME) Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(ROUTINGKEY_SMS).noargs();
    }

    /**
     *  设置手动ack签收消息
     * @author lhq
     * @date 2022/10/14
     *
     */
    @Bean
    @ConditionalOnClass
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactoryManual(CachingConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        //设置手动ack签收消息
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        //设置消费消息，每次1条
        factory.setPrefetchCount(1);
        return factory;
    }



    /**
     * A交换机
     */
    public static final String EXCHANGE_A = "exchange_a";

    /**
     * A队列
     */
    public static final String QUEUE_A = "queue_a";

    /**
     * 交换机往路由规则，类似正则
     */
    public static final String ROUTINGKEY_A="topic.#.sms.#";

    /**
     * 死信B交换机
     */
    public static final String EXCHANGE_DLX_B = "exchange_dlx_b";

    /**
     * B队列
     */
    public static final String QUEUE_B = "queue_b";

    /**
     * 路由规则，类似正则
     */
    public static final String ROUTINGKEY_B ="topic.#.sms.#";

    /**
     *延时队列
     * 1、一个正常的交换机+正常的队列A，并设置队列消息的过期时间或队列可以装的消息条数
     * 2、一个死信交换机（dlx）+正常的队列B
     *      消息成为死信的条件：
     *          1、队列A消息过期
     *          2、队列A的消息的长度超出最大的长度
     *          3、监听队列A的消费端拒接签收消息，并且不设置返回原先队列A
     * 3、设置队列A绑定死信，队列A消息过期了，直接发到死信队列dlx中，交换机dlx把消息发到队列B中，对应消费端监听队列B即可
     *
     */

    /**
     * 声明正常的交换机B---用于测试延时队列
     *
     */
    @Bean(EXCHANGE_A)
    public Exchange exchangeA(){
        //durable(true) 持久化，mq重启之后交换机还在
        return ExchangeBuilder.topicExchange(EXCHANGE_A).durable(true).build();
    }

    /**
     *   声明正常的队列A----TTL过期时间
     *   new Queue(QUEUE_EMAIL,true,false,false)
     *   durable="true" 持久化 rabbitmq重启的时候不需要创建新的队列
     *   auto-delete 表示消息队列没有在使用时将被自动删除 默认是false
     *   exclusive  表示该消息队列是否只在当前connection生效,默认是false
     *
     */
    @Bean(QUEUE_A)
    public Queue emailQueueA(){
        Map<String, Object> arguments = new HashMap<>(1);
        //设置队列消息TTL过期时间（单位：ms毫秒），这里设置为10秒，当消息过期后，还没有被消费，该消息也会被删除掉
        arguments.put("x-message-ttl",10000);
        // x-dead-letter-exchange 声明了队列里的死信转发到的DLX名称，转发到对应死信队列中
        arguments.put("x-dead-letter-exchange", EXCHANGE_A);

        return new Queue(QUEUE_A,true,false,false,arguments);
    }


    /**
     *   声明正常的队列A----TTL过期时间
     *   new Queue(QUEUE_EMAIL,true,false,false)
     *   durable="true" 持久化 rabbitmq重启的时候不需要创建新的队列
     *   auto-delete 表示消息队列没有在使用时将被自动删除 默认是false
     *   exclusive  表示该消息队列是否只在当前connection生效,默认是false
     *
     */
    @Bean(QUEUE_B)
    public Queue emailQueueB(){
        Map<String, Object> arguments = new HashMap<>(1);
        //设置队列消息TTL过期时间（单位：ms毫秒），这里设置为10秒，当消息过期后，还没有被消费，该消息也会被删除掉
        arguments.put("x-message-ttl",10000);
        // x-dead-letter-exchange 声明了队列里的死信转发到的DLX名称，转发到对应死信队列中
        arguments.put("x-dead-letter-exchange", EXCHANGE_DLX_B);

        return new Queue(QUEUE_A,true,false,false,arguments);
    }

    /**
     * 队列A绑定交换机B，指定routingKey
     * @param queue
     * @param exchange
     * @return
     */
    @Bean
    public Binding bindingExchangeA(@Qualifier(QUEUE_A) Queue queue,
                              @Qualifier(EXCHANGE_A) Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(ROUTINGKEY_A).noargs();
    }


    /**
     * 声明死信交换机
     *
     */
    @Bean(EXCHANGE_DLX_B)
    public Exchange exchangeB(){
        //durable(true) 持久化，mq重启之后交换机还在
        return ExchangeBuilder.topicExchange(EXCHANGE_DLX_B).durable(true).build();
    }

    /**
     * 队列B死信交换机dlx_B，指定routingKey
     * @param queue
     * @param exchange
     * @return
     */
    @Bean
    public Binding bindingExchangeB(@Qualifier(QUEUE_B) Queue queue,
                                    @Qualifier(EXCHANGE_DLX_B) Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(ROUTINGKEY_B).noargs();
    }
}