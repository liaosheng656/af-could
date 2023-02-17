package com.af.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.minio.MinioClient;

/**
 * MinIoClient配置
 * @Description: 
 * @author: liaohuiquan  
 * @date: 2022/03/31 11:55:37 
 *
 */
@Configuration
public class MinIoClientConfig {

	/**
	 * 节点
	 */
    @Value("${minio.endpoint}")
    private String endpoint;
    
	/**
	 * 账号
	 */
    @Value("${minio.accessKey}")
    private String accessKey;
    
	/**
	 * 密码
	 */
    @Value("${minio.secretKey}")
    private String secretKey;

    /**
     * 注入minio 客户端
     * @return
     */
    @Bean
    public MinioClient minioClient(){

    return MinioClient.builder()
                        .endpoint(endpoint)
                        .credentials(accessKey, secretKey)
                        .build();
    }
}
