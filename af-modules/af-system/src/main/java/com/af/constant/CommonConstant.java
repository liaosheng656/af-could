package com.af.constant;
import java.util.HashMap;
import java.util.Map;

public interface CommonConstant {

	public final static String X_ACCESS_TOKEN = "X-Access-Token";
	
	/**
	 * 用户身份
	 */
	public final static String ZC_USER_IDENTITY = "Zc-User-Identity";
	
	/**
	 * 正常状态
	 */
	public static final Integer STATUS_NORMAL = 0;

	/**
	 * 禁用状态
	 */
	public static final Integer STATUS_DISABLE = -1;

	/**
	 * 删除标志
	 */
	public static final Integer DEL_FLAG_1 = 1;

	/**
	 * 未删除
	 */
	public static final Integer DEL_FLAG_0 = 0;

	
	
	/** {@code 500 Server Error} (HTTP/1.0 - RFC 1945) */
    public static final Integer SC_INTERNAL_SERVER_ERROR_500 = 500;
    /** {@code 200 OK} (HTTP/1.0 - RFC 1945) */
    public static final Integer SC_OK_200 = 200;
    
    /**登录过期，token无效*/
//    public static final Integer SC_TOKEN_502 = 502;
    
    /**访问权限认证未通过 510*/
    public static final Integer SC_JEECG_NO_AUTHZ=510;

    /** 登录用户Shiro权限缓存KEY前缀 */
    public static String PREFIX_USER_SHIRO_CACHE  = "zc_shiro:cache:org.jeecg.modules.shiro.authc.ShiroRealm.authorizationCache:";
    /** 登录用户Token令牌缓存KEY前缀 */
    public static final String PREFIX_USER_TOKEN  = "prefix_zc_user_token_";
    /** Token缓存时间：3600秒即一小时 */
    public static final int  TOKEN_EXPIRE_TIME  = 3600;
    

    /**
     *  0：一级菜单
     */
    public static final Integer MENU_TYPE_0  = 0;
   /**
    *  1：子菜单 
    */
    public static final Integer MENU_TYPE_1  = 1;
    /**
     *  2：按钮权限
     */
    public static final Integer MENU_TYPE_2  = 2;
    
    /**通告对象类型（USER:指定用户，ALL:全体用户）*/
    public static final String MSG_TYPE_USER  = "USER";
    public static final String MSG_TYPE_ALL  = "ALL";
    
    /**发布状态（0未发布，1已发布，2已撤销）*/
    public static final String NO_SEND  = "0";
    public static final String HAS_SEND  = "1";
    public static final String HAS_CANCLE  = "2";
    
    /**阅读状态（0未读，1已读）*/
    public static final String HAS_READ_FLAG  = "1";
    public static final String NO_READ_FLAG  = "0";
    
    /**优先级（L低，M中，H高）*/
    public static final String PRIORITY_L  = "L";
    public static final String PRIORITY_M  = "M";
    public static final String PRIORITY_H  = "H";

    /**业务类型(sys:后台发送 busi:业务发送)*/
    public static final String BUS_TYPE_S  = "sys";
    public static final String BUS_TYPE_B  = "busi";

    /**终端类型，B、C代表B端，C端*/
    public static final String CLIENT_TYPE_B  = "B";
    public static final String CLIENT_TYPE_C  = "C";

    /**
     * 短信模板方式  0 .登录模板、1.注册模板、2.忘记密码模板
     */
    public static final String SMS_TPL_TYPE_0  = "0";
    public static final String SMS_TPL_TYPE_1  = "1";
    public static final String SMS_TPL_TYPE_2  = "2";
    
    /**
     * 状态(0无效1有效)
     */
    public static final String STATUS_0 = "0";
    public static final String STATUS_1 = "1";
    
    /**
     * 同步工作流引擎1同步0不同步
     */
    public static final Integer ACT_SYNC_1 = 1;
    public static final Integer ACT_SYNC_0 = 0;

    /**
     * 消息类型1:通知公告2:系统消息
     */
    public static final String MSG_CATEGORY_1 = "1";
    public static final String MSG_CATEGORY_2 = "2";
    
    /**
     * 是否配置菜单的数据权限 1是0否
     */
    public static final Integer RULE_FLAG_0 = 0;
    public static final Integer RULE_FLAG_1 = 1;

    
    /**字典翻译文本后缀*/
    public static final String DICT_TEXT_SUFFIX = "_dictText";

    /**
     * 文件上传类型（本地：local，Minio：minio，阿里云：alioss）
     */
    public static final String UPLOAD_TYPE_LOCAL = "local";
    public static final String UPLOAD_TYPE_MINIO = "minio";
    public static final String UPLOAD_TYPE_OSS = "alioss";

    /**
     * 文档上传自定义桶名称
     */
    public static final String UPLOAD_CUSTOM_BUCKET = "eoafile";
    /**
     * 文档上传自定义路径
     */
    public static final String UPLOAD_CUSTOM_PATH = "eoafile";
    /**
     * 文件外链接有效天数
     */
    public static final Integer UPLOAD_EFFECTIVE_DAYS = 1;

    /**
     * Excel文件后缀
     */
    public final static String XLS = ".xls";
    
    /**
     * Excel文件后缀
     */
    public final static String XLSX = ".xlsx";
    
    /**
     * 系统日志类型：访问类型（查询，登录等
     */
    public static final String LOG_VISIT = "visitLog";
    
    /**
     * 系统日志类型：操作类型（增删改等操作）
     */
    public static final String LOG_OPERATE = "operateLog";
    
    /**
     * redis缓存
     */
    public static final String SYS_DEPART = "zc_social_risk::sys_depart_cache";
    public static final String QUERY_ALL_CHILD_NODE = "zc_social_risk::sys_depart_cache::queryAllChildNode";
    
    
}