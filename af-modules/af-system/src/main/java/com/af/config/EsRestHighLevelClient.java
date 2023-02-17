package com.af.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//import co.elastic.clients.elasticsearch.ElasticsearchClient;
//import co.elastic.clients.json.jackson.JacksonJsonpMapper;
//import co.elastic.clients.transport.ElasticsearchTransport;
//import co.elastic.clients.transport.rest_client.RestClientTransport;
import io.minio.MinioClient;

/**
 * ES客户端配置
 * @Description: 
 * @author: liaohuiquan  
 * @date: 2022/04/02 17:53:35 
 *
 */
@Configuration
public class EsRestHighLevelClient {

	@Value("${elasticsearch.address}")
	private String hostname;
	
	@Value("${elasticsearch.port}")
	private Integer port;
	
    /**
     * 注入ES 客户端
     * @return
     */
//    @Bean
//    public ElasticsearchClient elasticsearchClient(){
//
//		// Create the low-level client
//		RestClient restClient = RestClient.builder(
//		    new HttpHost(hostname, port)).build();
//	
//		// Create the transport with a Jackson mapper
//		ElasticsearchTransport transport = new RestClientTransport(
//		    restClient, new JacksonJsonpMapper());
//	
//		// And create the API client
//		ElasticsearchClient client = new ElasticsearchClient(transport);
//	
//	    return client;
//    }
    
    /**
     * 注入ES 客户端2
     * @return
     */
    @Bean
    public RestClient restClient(){

		// Create the low-level client
		RestClient restClient = RestClient.builder(
		    new HttpHost(hostname, port)).build();

	    return restClient;
    }
    
    /**
     * 注入ES 客户端3
     * @return
     */
//    @Bean
//    public ElasticsearchClient elasticsearchClient(){
//
//		// Create the low-level client
//		RestClientBuilder restClient = RestClient.builder(
//		    new HttpHost(hostname, port));
//
//		// 创建 HLRC 
//		RestHighLevelClient hlrc =  new  RestHighLevelClient (restClient); 
//		
//		// Create the new Java Client with the same low level client
//		ElasticsearchTransport transport = new RestClientTransport(
//				hlrc.getLowLevelClient(),
//		    new JacksonJsonpMapper()
//		);
//
//		ElasticsearchClient esClient = new ElasticsearchClient(transport);
//		
//	    return esClient;
//    }
    
}
