package com.pingan.traffic.exption;

public interface ErrorCode {
	/**
	 * 成功
	 */
	public static final int success = 0;
	/**
	 * 登陆错误(其他)
	 */
	public static final int LOGIN_FAIL_OTHER = 101;
	/**
	 * 不存在数据
	 */
	public static final int DATA_NULL = 102;
	/**
	 * 数据格式错误
	 */
	public static final int PARAS_ERROR = 103;
	/**
	 * 无效操作
	 */
	public static final int INVALID = 104;
	/**
	 * 服务器出错
	 */
	public static final int SYS_ERROR = 105;
	/**
	 * 等待结果
	 */
	public static final int WAIT = 106;
}
