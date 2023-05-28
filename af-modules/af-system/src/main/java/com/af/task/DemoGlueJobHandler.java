package com.af.task;

import com.af.common.moudules.entity.AmapArea;
import com.af.mapper.AmapAreaMapper;
import com.af.service.IAmapAreaService;
import com.alibaba.fastjson.JSONObject;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.IJobHandler;
import org.springframework.beans.factory.annotation.Autowired;

public class DemoGlueJobHandler extends IJobHandler {

	@Autowired
  	private AmapAreaMapper amapAreaMapper;

	@Override
	public void execute() throws Exception {
		XxlJobHelper.log("获取amapAreaService数据");
		AmapArea amapArea = amapAreaMapper.selectById("440118000000");

		XxlJobHelper.log("获取成功：{}", JSONObject.toJSONString(amapArea));

	}

}