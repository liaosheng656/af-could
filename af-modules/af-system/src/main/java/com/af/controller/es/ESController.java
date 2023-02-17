package com.af.controller.es;

import java.io.IOException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.lucene.search.TotalHits;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.Version;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.ElasticsearchClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.*;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Bucket;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.SumAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.SuggestionBuilder;
import org.elasticsearch.search.suggest.term.TermSuggestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.af.common.moudules.entity.SfcMedia;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;

import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.search.suggest.term.TermSuggestionBuilder;

/**
 * ES
 * @Description: 
 * @author: liaohuiquan  
 * @date: 2022/04/03 15:18:24 
 *
 */
@Slf4j
@RequestMapping("es")
@RestController
public class ESController {
	
	private static final String JOB_IDX = "hsidfhsdih";

	@Autowired
	private RestClient restClient;
	
//	@Autowired
//	private ElasticsearchClient elasticsearchClient;
	
	@Autowired
	private RestHighLevelClient restHighLevelClient;
	
	@Autowired
	private BulkProcessor bulkProcessor;
	
	/**
	 * 删除文档
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("delete")
    public Object delete() throws IOException {
        DeleteByQueryRequest deleteByQueryRequest=new DeleteByQueryRequest();
        deleteByQueryRequest.indices("posts");
        deleteByQueryRequest.setQuery(QueryBuilders.wildcardQuery("fileName.keyword", "*文件名称112*"));
        //分词式删除
        BulkByScrollResponse response = restHighLevelClient.deleteByQuery(deleteByQueryRequest,RequestOptions.DEFAULT);
        
        
        return "";
    }
    
	/**
	 * 先查询出来再更新
	 * @throws IOException
	 */
	@RequestMapping("queryToUpdate")
    public Object queryToUpdate() throws IOException {

        // 条件搜索
        SearchRequest searchRequest = new SearchRequest("posts");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        
		//多条件查询，termQuery精准匹配(一个个字分开，有可能有bug)，matchAllQuery查询所有内容，matchQuery可以模糊查询。
       QueryBuilder queryBuilder = QueryBuilders.boolQuery()
    		   //模糊查询
               .must(QueryBuilders.wildcardQuery("fileName.keyword", "*文件名称111*"));
       
       sourceBuilder.query(queryBuilder);
		
        // 执行搜索
        searchRequest.source(sourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
  
        SearchHits hits = response.getHits();
        
        SearchHit[] searchHits = hits.getHits();
        
	    for(SearchHit hit:searchHits){
	        // 文档的主键
	        String id = hit.getId();
//	        // 源文档内容，文档返回的是map
//	        Map<String, Object> sourceAsMap = hit.getSourceAsMap();
//	        
//	        System.out.println(JSON.toJSONString(sourceAsMap));
	    }
        
        UpdateRequest updateRequest = new UpdateRequest("posts",searchHits[0].getId());
        
        Map<String, Object> kvs = new HashMap<>();
        kvs.put("fileName", "文件名称111xxx");
        updateRequest.doc(kvs);
        updateRequest.timeout(TimeValue.timeValueSeconds(1));
        updateRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);
        //数据为存储而不是更新
        UpdateResponse update = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
        
        return "";
    
    }

	
    /**
     * 更新（脚本更新，暂时未调通）
     * @return
     * @throws IOException
     */
    public Object testSingleUpdateQuery() throws IOException {

        UpdateByQueryRequest updateByQueryRequest = new UpdateByQueryRequest();
        //需要更新的索引
        updateByQueryRequest.indices("posts");

        updateByQueryRequest.setQuery(QueryBuilders.wildcardQuery("fileName.keyword", "文件名称11*"));

        updateByQueryRequest.setScript(new Script(ScriptType.INLINE,
                "painless",
                "ctx._source.tag='电脑'", Collections.emptyMap()));
        //数据为存储而不是更新
        BulkByScrollResponse response = restHighLevelClient.updateByQuery(updateByQueryRequest, RequestOptions.DEFAULT);
        
        return "";
    }

	
	/**
	 * 求和
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("sum")
	public Object sum() throws IOException {
		
        // 条件搜索
        SearchRequest searchRequest = new SearchRequest("posts");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        
		SumAggregationBuilder builder = AggregationBuilders
				.sum("sum_id")
				.field("id");
		
		//多条件查询，termQuery精准匹配(一个个字分开，有可能有bug)，matchAllQuery查询所有内容，matchQuery可以模糊查询。
       QueryBuilder queryBuilder = QueryBuilders.boolQuery()
    		   //模糊查询,?匹配单个字符，*匹配多个字符
               .must(QueryBuilders.wildcardQuery("fileName.keyword", "文件名称11*"));
       //多个字段匹配某一个值		  
       //multiMatchQuery

		// 3.使用QueryBuilders.multiMatchQuery构建一个查询条件（搜索title、jd），并配置到SearchSourceBuilder
//		MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(keywords, "title", "jd");
//
//		// 将查询条件设置到查询请求构建器中
//		sourceBuilder.query(multiMatchQueryBuilder);


       sourceBuilder.query(queryBuilder);
		sourceBuilder.aggregation(builder);
		
        // 执行搜索
        searchRequest.source(sourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        
        SearchHits hits = response.getHits();
        
        SearchHit[] hits2 = hits.getHits();
        
		return JSON.toJSONString(hits2);
	}
	
	
	/**
	 * 查询字段平均值
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("avg")
	public Object avg() throws IOException {
		
        // 条件搜索
        SearchRequest searchRequest = new SearchRequest("posts");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        
		AvgAggregationBuilder builder = AggregationBuilders
				.avg("avg_id")
				.field("id");
		
		//多条件查询，termQuery精准匹配(一个个字分开，有可能有bug)，matchAllQuery查询所有内容，matchQuery可以模糊查询。
       QueryBuilder queryBuilder = QueryBuilders.boolQuery()
    		   //模糊查询
               .must(QueryBuilders.wildcardQuery("fileName.keyword", "*文件名称11*"));
       
       sourceBuilder.query(queryBuilder);
		sourceBuilder.aggregation(builder);
		
        // 执行搜索
        searchRequest.source(sourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        
		return "";
	}
	
	
	/**
	 * 错词纠正和自动补全(类似百度搜索就查询出一些相关的字条)
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("aggregationTest")
	public Object aggregationTest() throws IOException {
        // 条件搜索
        SearchRequest searchRequest = new SearchRequest("posts");
        
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		// 为字段user和文本kmichy创建 TermSuggestionBuilder 
		SuggestionBuilder<?> termSuggestionBuilder = SuggestBuilders
				.termSuggestion("fileName.keyword")
				.text("文件名称1")
//				.prefixLength(2)
				//错词纠正和自动补全
				.suggestMode(TermSuggestionBuilder.SuggestMode.POPULAR)
				.size(10);
		
		SuggestBuilder suggestBuilder = new SuggestBuilder();
		// 添加TermSuggestionBuilder到suggestBuilder中，并命名为suggest_user
		suggestBuilder.addSuggestion("suggest_user", termSuggestionBuilder); 
		sourceBuilder.suggest(suggestBuilder);
		
        // 执行搜索
        searchRequest.source(sourceBuilder);
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        // 解析结果
     // Use the Suggest class to access suggestions
        Suggest suggest = response.getSuggest(); 
        // Suggestions can be retrieved by name. You need to assign them to the correct type of Suggestion class (here TermSuggestion), otherwise a ClassCastException is thrown
        TermSuggestion termSuggestion = suggest.getSuggestion("suggest_user"); 
        // Iterate over the suggestion entries
        for (TermSuggestion.Entry entry : termSuggestion.getEntries()) { 
        	
        	System.out.println(JSON.toJSONString(entry));
            // Iterate over the options in one entry
            for (TermSuggestion.Entry.Option option : entry) { 
                String suggestText = option.getText().string();
            }
        }
 

        return JSON.toJSONString(response);
	}
	
	
    /**
     * 获取数据实现高亮功能
     * @param keyword 值
     * @param pageNo
     * @param pageSize
     * @return
     * @throws IOException
     */
	@RequestMapping("pageHighlight")
    public List<Map<String, Object>> searchPageHighlightBuilder(String keyword, int pageNo, int pageSize)
        throws IOException {
        if (pageNo <= 0) {
            pageNo = 0;
        }
        
        if(pageSize <= 0) {
        	pageSize = 10;
        }

        keyword = URLDecoder.decode(keyword, "UTF-8");

        // 条件搜索
        SearchRequest searchRequest = new SearchRequest("posts");

		//123为这个聚合查询的名称
//		TermsAggregationBuilder terms = AggregationBuilders.terms("123");
        
        // 精准匹配
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("fileName.keyword", keyword);
        
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		//多条件查询，termQuery精准匹配(一个个字分开，有可能有bug)，matchAllQuery查询所有内容，matchQuery可以模糊查询。
       QueryBuilder queryBuilder = QueryBuilders.boolQuery()
    		   //and，Operator默认为or，改成and才能查询一个结果
               .must(QueryBuilders.matchQuery("fileName", "文件名称1")
            		   .operator(Operator.AND));
       			 //not
//               .mustNot(QueryBuilders.termQuery("message", "nihao"))
       			 //or
//               .should(QueryBuilders.termQuery("gender", "male"));
       
       	//根据id排序
       	sourceBuilder.sort(new FieldSortBuilder("id").order(SortOrder.DESC)); 
       	
//       	sourceBuilder.query(queryBuilder);       	
		sourceBuilder.query(termQueryBuilder);
		// 设置查询的起始位置，默认是0
		sourceBuilder.from(pageNo); 
		// 设置查询结果的页大小，默认是10
		sourceBuilder.size(pageSize); 
		//默认情况下，搜索请求会返回文档_source的内容，但与Rest API中的内容一样，您可以覆盖此行为。例如，您可以完全关闭_source检索
//		sourceBuilder.fetchSource(false);
		//设置查询请求的超时时间：如下表示60秒没得到返回结果时就认为请求已超时
		sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
		
        // 高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("fileName");
		// 多个高亮显示
        highlightBuilder.requireFieldMatch(true);
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
//		highlightBuilder.field("title");
//		highlightBuilder.field("jd");
//		highlightBuilder.preTags("<font color='red'>");
//		highlightBuilder.postTags("</font>");

        sourceBuilder.highlighter(highlightBuilder);

        // 执行搜索
        searchRequest.source(sourceBuilder);
        SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        // 解析结果
        List<Map<String, Object>> list = new ArrayList<>();
        for (SearchHit documentFields : search.getHits().getHits()) {

            // 解析高亮的字段
            Map<String, HighlightField> highlightFields = documentFields.getHighlightFields();
            HighlightField title = highlightFields.get("fileName");
            Map<String, Object> sourceAsMap = documentFields.getSourceAsMap();
            if (title != null) {
                Text[] fragments = title.fragments();
                String n_title = "";
                for (Text text : fragments) {
                    n_title += text;
                }
                sourceAsMap.put("fileName", n_title);
            }
            list.add(sourceAsMap);
        }
        return list;

    }

	/**
	 * 查询数据
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("test03")
	public String test03() throws IOException{

		boolean checkIndex = checkIndex("posts");
//		System.out.println(checkIndex);
		if(!checkIndex) {
			boolean createIndex = createIndex("posts");
			System.out.println(createIndex);
			
		}
		//123为这个聚合查询的名称
//		TermsAggregationBuilder terms = AggregationBuilders.terms("123");
		
		SearchRequest request = new SearchRequest("posts");
		// 设置路由
//		request.routing("routing"); 
		// IndicesOptions 设置如何解析未知的索引及通配符表达式如何扩展
//		request.indicesOptions(IndicesOptions.lenientExpandOpen()); 
		// 设置偏好参数，如设置搜索本地分片的偏好，默认是在分片中随机检索
//		request.preference("_local");
		
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		//多条件查询，termQuery精准匹配(一个个字分开，有可能有bug)，matchAllQuery查询所有内容，matchQuery可以模糊查询。
       QueryBuilder queryBuilder = QueryBuilders.boolQuery()
    		   //and，Operator默认为or，改成and才能查询一个结果
               .must(QueryBuilders.matchQuery("fileName", "文件名称1")
            		   .operator(Operator.AND));
       			 //not
//               .mustNot(QueryBuilders.termQuery("message", "nihao"))
       			 //or
//               .should(QueryBuilders.termQuery("gender", "male"));
       
		sourceBuilder.query(queryBuilder);
		// 设置查询的起始位置，默认是0
		sourceBuilder.from(0); 
		// 设置查询结果的页大小，默认是10
		sourceBuilder.size(5); 
		//默认情况下，搜索请求会返回文档_source的内容，但与Rest API中的内容一样，您可以覆盖此行为。例如，您可以完全关闭_source检索
//		sourceBuilder.fetchSource(false);
		//设置查询请求的超时时间：如下表示60秒没得到返回结果时就认为请求已超时
		sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
		
		request.source(sourceBuilder);
		
		//混合查询
//        DisMaxQueryBuilder tieBreaker = QueryBuilders.disMaxQuery()
//        		.add(QueryBuilders.termQuery("fileName", "文件名称1"))
//                .add(QueryBuilders.termQuery("fileName", "文件名称1"))
//                //权重
//                .boost(1.2f).tieBreaker(0.7f);
//
//        sourceBuilder.query(tieBreaker);
		
//		QueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("fileName", "文件名称")
//				// 开启模糊查询
//                .fuzziness(Fuzziness.AUTO)
//                // 设置查询前缀长度
//                .prefixLength(3)
//                // 设置模糊查询最大扩展
//                .maxExpansions(10);
		
//		sourceBuilder.query(matchQueryBuilder);
		
		SearchResponse searchResponse = restHighLevelClient.search(request, RequestOptions.DEFAULT);
		
	    // 搜索结果
	    SearchHits hits = searchResponse.getHits();
	    // 匹配到的总记录数
	    long totalHits = hits.getTotalHits().value;
	    
	    log.info("匹配到的总记录数："+totalHits);
		
	    // 得到匹配度高的文档
	    SearchHit[] searchHits = hits.getHits();
	    // 日期格式化对象
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    for(SearchHit hit:searchHits){
	        // 文档的主键
	        String id = hit.getId();
	        // 源文档内容，文档返回的是map
	        Map<String, Object> sourceAsMap = hit.getSourceAsMap();
	        
	        System.out.println(JSON.toJSONString(sourceAsMap));
	    }
		
		return "OK";
	}


	/**
	 * 测试添加数据
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("test02")
	public String test02() throws IOException{

		boolean checkIndex = checkIndex("posts");
		System.out.println(checkIndex);
		if(!checkIndex) {
			boolean createIndex = createIndex("posts");
			System.out.println(createIndex);

		}
		BulkRequest request = new BulkRequest();

		for (int x = 150; x < 349; x++) {
			SfcMedia sfcMedia = createSfcMedia(x);
			IndexRequest indexRequest = new IndexRequest("posts")
					.source(JSON.toJSONString(sfcMedia),XContentType.JSON);
			request.add(indexRequest);
			bulkProcessor.add(indexRequest);
		}
//		BulkResponse response = restHighLevelClient.bulkAsync(request, RequestOptions.DEFAULT,listener);

//		String jsonString = JSONObject.toJSONString(response);
//		RestStatus status = response.status();
//		System.out.println(status);

		return "OK";
	}


	/**
	 * 测试添加数据
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("test01")
	public String test01() throws IOException{

		boolean checkIndex = checkIndex("posts");
		System.out.println(checkIndex);
		if(!checkIndex) {
			boolean createIndex = createIndex("posts");
			System.out.println(createIndex);

		}
		BulkRequest request = new BulkRequest();

//		request.add(new DeleteRequest("posts"));
//		request.add(new UpdateRequest("posts", "2")
//		        .doc(XContentType.JSON,"other", "test"));

		Map<String, Object> jsonMap = new HashMap<>();
		jsonMap.put("1user", "kimchy");
		jsonMap.put("1postDate", new Date());
		jsonMap.put("1message", "trying out Elasticsearch");
		IndexRequest indexRequest = new IndexRequest("posts")
				.source(JSON.toJSONString(jsonMap),XContentType.JSON);

		request.add(indexRequest);
		BulkResponse response = restHighLevelClient.bulk(request, RequestOptions.DEFAULT);

		String jsonString = JSONObject.toJSONString(response);
		RestStatus status = response.status();
		System.out.println(status);

		return jsonString;
	}
	

    /**
     * 判断索引是否存在
     */
    private boolean checkIndex (String index) {
        try {
        	GetIndexRequest getIndexRequest = new GetIndexRequest();
        	getIndexRequest.indices(index);
            return restHighLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Boolean.FALSE ;
    }

    /**
     * 创建索引
     */
    public boolean createIndex (String indexName){
        try {
            if(!checkIndex(indexName)){
                CreateIndexRequest request = new CreateIndexRequest(indexName);
                this.restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
                return Boolean.TRUE ;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Boolean.FALSE;
    }

    /**
     * 删除索引
     */
    public boolean deleteIndex(String indexName) {
        try {
            if(checkIndex(indexName)){
                DeleteIndexRequest request = new DeleteIndexRequest(indexName);
                AcknowledgedResponse response = restHighLevelClient.indices().delete(request, RequestOptions.DEFAULT);
                return response.isAcknowledged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Boolean.FALSE;
    }

    /**
     * 创建实体信息
     * @param x
     * @return
     */
    private SfcMedia createSfcMedia(int x) {
    	
    	SfcMedia sfcMedia = new SfcMedia();
    	
    	sfcMedia.setId(x);
    	sfcMedia.setTitle("标题"+x);
    	sfcMedia.setTypeId(x);
    	sfcMedia.setGroupId(x);
    	sfcMedia.setStoreType(x);
    	sfcMedia.setStoreType((x*3)+x);
    	sfcMedia.setFileName("文件名称"+x);
    	sfcMedia.setOriginalFile("原文件名称"+x);
    	sfcMedia.setPreviewUrl("预览文件url"+x);
    	sfcMedia.setProgramId(x);
    	sfcMedia.setVideoId(x);
    	sfcMedia.setDescription("媒体描述"+x);
    	sfcMedia.setCateId(x);
    	sfcMedia.setCateIdLeaf("二级分类ID"+x);
    	sfcMedia.setTags("标签信息"+x);
    	sfcMedia.setCoverUrl("缩略图URL"+x);
    	sfcMedia.setVideoCodec("视频编码"+x);
    	sfcMedia.setAudioCodec("音频编码"+x);
    	sfcMedia.setDuration(x);
    	sfcMedia.setFormat("格式"+x);
    	sfcMedia.setSize(Long.valueOf(x));
    	sfcMedia.setVideoBitrate(1080+x);
    	sfcMedia.setAudioBitrate(50+x);
    	sfcMedia.setFps(600+x);
    	sfcMedia.setWidth(142+x);
    	sfcMedia.setHeight(231+x);
    	sfcMedia.setUserId("上传人"+x);
    	sfcMedia.setVideoText("视频字幕文本内容");
    	sfcMedia.setMediaStatus(1+x);
    	sfcMedia.setStatus(1);
    	sfcMedia.setUploadStatus(x);
    	sfcMedia.setDecodeStatus(x);
    	sfcMedia.setFileMd5(UUID.randomUUID().toString());
    	sfcMedia.setDeptId("单位id"+x);
    	sfcMedia.setStorageId(x);
    	sfcMedia.setCreateTime(new Date());
    	sfcMedia.setUpdateTime(new Date());
    	
		return sfcMedia;
    }

	/***
	 * > * 第一次查询，不带scroll_id，所以要设置scroll超时时间
	 * > * 超时时间不要设置太短，否则会出现异常
	 * > * 第二次查询，SearchSrollRequest
	 *
	 * 使用scroll分页方式查询
	 * @param keywords
	 * @param scrollId
	 * @param pageSize
	 * @return
	 * @throws IOException
	 */
	public Map<String, Object> searchByScrollPage(String keywords, String scrollId, int pageSize) throws IOException {
		SearchResponse searchResponse = null;

		if(scrollId == null) {
			// 1.构建SearchRequest检索请求
			// 专门用来进行全文检索、关键字检索的API
			SearchRequest searchRequest = new SearchRequest(JOB_IDX);

			// 2.创建一个SearchSourceBuilder专门用于构建查询条件
			SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

			// 3.使用QueryBuilders.multiMatchQuery构建一个查询条件（搜索title、jd），并配置到SearchSourceBuilder
			MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(keywords, "title", "jd");

			// 将查询条件设置到查询请求构建器中
			searchSourceBuilder.query(multiMatchQueryBuilder);

			// 每页显示多少条
			searchSourceBuilder.size(pageSize);

			// 4.调用SearchRequest.source将查询条件设置到检索请求
			searchRequest.source(searchSourceBuilder);

			//--------------------------
			// 设置scroll查询
			//--------------------------
			searchRequest.scroll(TimeValue.timeValueMinutes(5));

			// 5.执行RestHighLevelClient.search发起请求
			searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

		}
		// 第二次查询的时候，直接通过scroll id查询数据
		else {
			SearchScrollRequest searchScrollRequest = new SearchScrollRequest(scrollId);
			searchScrollRequest.scroll(TimeValue.timeValueMinutes(5));

			// 使用RestHighLevelClient发送scroll请求
			searchResponse = restHighLevelClient.scroll(searchScrollRequest, RequestOptions.DEFAULT);
		}

		//--------------------------
		// 迭代ES响应的数据
		//--------------------------

		SearchHit[] hitArray = searchResponse.getHits().getHits();

		// 6.遍历结果
		ArrayList<SfcMedia> jobDetailArrayList = new ArrayList<>();

		for (SearchHit documentFields : hitArray) {
			// 1)获取命中的结果
			String json = documentFields.getSourceAsString();

			// 2)将JSON字符串转换为对象
			SfcMedia jobDetail = JSONObject.parseObject(json, SfcMedia.class);

			// 3)使用SearchHit.getId设置文档ID
//			jobDetail.setId(Long.parseLong(documentFields.getId()));

			jobDetailArrayList.add(jobDetail);
		}

		// 8.	将结果封装到Map结构中（带有分页信息）
		// a)	total -> 使用SearchHits.getTotalHits().value获取到所有的记录数
		// b)	content -> 当前分页中的数据
		long totalNum = searchResponse.getHits().getTotalHits().value;
		HashMap hashMap = new HashMap();
		hashMap.put("scroll_id", searchResponse.getScrollId());
		hashMap.put("content", jobDetailArrayList);

		return hashMap;
	}
}
