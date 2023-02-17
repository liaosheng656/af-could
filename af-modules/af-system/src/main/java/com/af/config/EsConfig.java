package com.af.config;



import org.apache.http.HttpHost;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.core.TimeValue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * ElasticSearch 配置
 * @Description: 
 * @author: liaohuiquan  
 * @date: 2022/04/03 21:19:58 
 *
 */
@Slf4j
@Configuration
public class EsConfig {

      /**
       * 协议
       */
      @Value("${elasticsearch.schema:http}")
      private String schema;
      
      /**
       * 集群地址，如果有多个用“,”隔开
       */
      @Value("${elasticsearch.address}")
      private String address;
      
      /**
       * 连接超时时间
       */
      @Value("${elasticsearch.connectTimeout}")
      private int connectTimeout;
      
      /**
       * Socket 连接超时时间
       */
      @Value("${elasticsearch.socketTimeout}")
      private int socketTimeout;
      
      /**
       * 获取连接的超时时间
       */
      @Value("${elasticsearch.connectionRequestTimeout}")
      private int connectionRequestTimeout;
      
      /**
       * 最大连接数
       */
      @Value("${elasticsearch.maxConnectNum}")
      private int maxConnectNum;
      
      /**
       * 最大路由连接数
       */
      @Value("${elasticsearch.maxConnectPerRoute}")
      private int maxConnectPerRoute;
      
      @Value("${elasticsearch.userName}")
      private String userName;
      
      @Value("${elasticsearch.password}")
      private String password;
   
      private RestHighLevelClient restHighLevelClient;
   
   //通用设置项
   public static final RequestOptions COMMON_OPTIONS;
   static {
      RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
      // builder.addHeader("Authorization", "Bearer " + TOKEN);
      // builder.setHttpAsyncResponseConsumerFactory(
      //        new HttpAsyncResponseConsumerFactory
      //                  .HeapBufferedResponseConsumerFactory(30 * 1024 * 1024 * 1024));
      COMMON_OPTIONS = builder.build();
   }
      
      // @Bean(name = "restHighLevelClient")
      @Bean
      public RestHighLevelClient restHighLevelClient() {
         // 拆分地址
         List<HttpHost> hostLists = new ArrayList<>();
         String[] hostList = address.split(",");
         for (String addr : hostList) {
            String host = addr.split(":")[0];
            String port = addr.split(":")[1];
            hostLists.add(new HttpHost(host, Integer.parseInt(port), schema));
         }
         
         final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
         credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(userName, password));
         
         // 转换成 HttpHost 数组
         HttpHost[] httpHost = hostLists.toArray(new HttpHost[]{});
         // 构建连接对象
         RestClientBuilder builder = RestClient.builder(httpHost);
         // 异步连接延时配置
         builder.setRequestConfigCallback(requestConfigBuilder -> {
            requestConfigBuilder.setConnectTimeout(connectTimeout);
            requestConfigBuilder.setSocketTimeout(socketTimeout);
            requestConfigBuilder.setConnectionRequestTimeout(connectionRequestTimeout);
            return requestConfigBuilder;
         });
         // 异步连接数配置
         builder.setHttpClientConfigCallback(httpClientBuilder -> {
            httpClientBuilder.setMaxConnTotal(maxConnectNum);
            httpClientBuilder.setMaxConnPerRoute(maxConnectPerRoute);
            return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
         });
         
         restHighLevelClient = new RestHighLevelClient(builder);
         return restHighLevelClient;
      }
      
      
      @Bean
      public BulkProcessor bulkProcessor() {

          BulkProcessor.Listener listener = new BulkProcessor.Listener() {
              @Override
              public void beforeBulk(long executionId, BulkRequest request) {
                  log.info("1. 【beforeBulk】批次[{}] 携带 {} 请求数量", executionId, request.numberOfActions());
              }

              @Override
              public void afterBulk(long executionId, BulkRequest request,
                                    BulkResponse response) {
                  if (!response.hasFailures()) {
                      log.info("2. 【afterBulk-成功】批量 [{}] 完成在 {} ms", executionId, response.getTook().getMillis());
                  } else {
                      BulkItemResponse[] items = response.getItems();
                      for (BulkItemResponse item : items) {
                          if (item.isFailed()) {
                              log.info("2. 【afterBulk-失败】批量 [{}] 出现异常的原因 : {}", executionId, item.getFailureMessage());
                              break;
                          }
                      }
                  }
              }

              @Override
              public void afterBulk(long executionId, BulkRequest request,
                                    Throwable failure) {

                  List<DocWriteRequest<?>> requests = request.requests();
                  List<String> esIds = requests.stream().map(DocWriteRequest::id).collect(Collectors.toList());
                  log.error("3. 【afterBulk-failure失败】es执行bluk失败,失败的esId为：{}", esIds, failure);
              }
          };

         //可以配置多种刷新策略，将数据由内存刷新到es中
		BulkProcessor.Builder builder = BulkProcessor.builder(((bulkRequest, bulkResponseActionListener) -> {
              restHighLevelClient.bulkAsync(bulkRequest, RequestOptions.DEFAULT, bulkResponseActionListener);
              //af-bulkProcessor该处理器的名称，例如用于识别调度程序线程
          }), listener,"af-bulkProcessor");
          //到达10000条时刷新，默认1000
          builder.setBulkActions(300);
          //内存到达8M时刷新，默认5M
          builder.setBulkSize(new ByteSizeValue(1L, ByteSizeUnit.MB));
          //设置的刷新间隔10s
          builder.setFlushInterval(TimeValue.timeValueSeconds(10));
          //设置允许执行的并发请求数。
          builder.setConcurrentRequests(8);
          //设置重试策略
          builder.setBackoffPolicy(BackoffPolicy.constantBackoff(TimeValue.timeValueSeconds(1), 3));
          
          BulkProcessor build = builder.build();
          
          return build;
      }      
}
