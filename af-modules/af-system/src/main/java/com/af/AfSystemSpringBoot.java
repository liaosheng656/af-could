package com.af;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


/**
 * 启动类
 * @Description: 
 * @author: liaohuiquan  
 * @date: 2022/03/09 20:26:37 
 *
 */
@MapperScan("com.af.mapper")
@SpringBootApplication

//开启SpringBoot的缓存
//@EnableCaching

//开启自定义过滤器
@ServletComponentScan 

//开启Spring定时任务
@EnableScheduling

@EnableWebMvc
//实现CommandLineRunner的作用: 服务启动执行，执行加载数据等操作
public class AfSystemSpringBoot //implements CommandLineRunner

{

	public static void main(String[] args) {
//		SpringApplication.run(RunSpringBoot.class, args);
        SpringApplicationBuilder builder = new SpringApplicationBuilder(AfSystemSpringBoot.class);
        builder.headless(false)
               // .web(WebApplicationType.NONE)
               // .bannerMode(Banner.Mode.OFF)
                .run(args);
	}

}
