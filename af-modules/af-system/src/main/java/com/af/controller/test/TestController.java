package com.af.controller.test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.af.common.vo.Result;
import com.alibaba.fastjson.JSONObject;

/**
 * 测试
 * @Description: 
 * @author: liaohuiquan  
 * @date: 2022/04/14 11:29:42 
 *
 */
@RequestMapping("test")
@RestController
public class TestController {

	/**
	 * 测试
	 * @return
	 */
	@RequestMapping("test01")
	public Result<?> test01(){
		Result<String> ok = Result.ok("123");
		
		return ok;
	}
	
	   public static void main(String[] args) throws UnsupportedEncodingException {

	        //设置请求参数
	        Map<String, Object> reqMap = new HashMap<>();

	        reqMap.put("nonce", "28355675342418");
	        reqMap.put("platformId", 63);
	        reqMap.put("realName", "韩代芙");
	        reqMap.put("phoneNo", "13726219935");
	        reqMap.put("idType", "0");
	        reqMap.put("idNo", "654022199104141048");

//	        req={"nonce":"1234567","phoneNo":"13333333334","type":"1","commentGranted":1}
//	        reqMap.put("phoneNo", "13333333334");
//	        reqMap.put("type", "1");
//	        reqMap.put("commentGranted", 1);

	        //请求参数
	        String req = JSONObject.toJSONString(reqMap);
	        //参数摘要
	        String sign = MD5Util.getMD5("171" + "CFBA8DE1B994473783240F56378F7DC5" + URLEncoder.encode(req, "UTF-8"));

	        System.out.println(sign);
	        // 认证json对象字符串
	        String authJsonString = "{\"managerId\": \"" + 171 + "\",\"sign\": \"" + sign + "\"}";
	        String authValue = "Manager " + Base64.getEncoder().encodeToString(authJsonString.getBytes("UTF-8"));

	        System.out.println(authValue);
	    }
	
	
}
