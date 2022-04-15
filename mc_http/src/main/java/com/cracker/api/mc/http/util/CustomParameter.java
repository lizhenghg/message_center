package com.cracker.api.mc.http.util;

import java.util.List;

/**
 * 自定义参数类，存储系统中自定义的HTTP头及URI匹配中的获取到的分组信息
 * CustomParameter: CustomParameter.java
 * @author lizhg<2486479615@qq.com>
 * <br/>=================================
 * <br/>公司：myself
 * <br/>版本：1.1.0
 * <br/>创建时间：2020-10-13
 * <br/>jdk版本：1.8
 * <br/>=================================
 */
public class CustomParameter {

	private String clientVersion;
	private String appId;
	private String accessToken;
	private String uri;
	private List<String> uriGroup;
	
	public String getClientVersion()
    {
	    return clientVersion;
    }
	public void setClientVersion(String clientVersion)
    {
	    this.clientVersion = clientVersion;
    }
	public String getAppId()
    {
	    return appId;
    }
	public void setAppId(String appId)
    {
	    this.appId = appId;
    }
	public String getAccessToken()
    {
	    return accessToken;
    }
	public void setAccessToken(String accessToken)
    {
	    this.accessToken = accessToken;
    }
	public List<String> getUriGroup()
    {
	    return uriGroup;
    }
	public void setUriGroup(List<String> uriGroup)
    {
	    this.uriGroup = uriGroup;
    }
	public String getUri()
    {
	    return uri;
    }
	public void setUri(String uri)
    {
	    this.uri = uri;
    }
}
