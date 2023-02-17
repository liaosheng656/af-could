package com.af.controller.os;

import cn.hutool.core.util.RuntimeUtil;

/**
 * @Description: 
 * @author: liaohuiquan  
 * @date: 2022/08/18 23:29:44 
 * 
 */
public class OsTestController {

	/**
	 * 用命令操作Linux系统
	 * @param time
	 * @param module
	 * @return
	 */
    public Process deploy(String time, String module) {
        return RuntimeUtil.exec("sh /home/run/www/deploy/deploy.sh " + time + " " + module);

    }
}
