package com.xueduoduo.health.login;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "login.auth")
public class AuthProperties {

    /**
     * 验证是否开启
     */
    private boolean enable                 = false;

    /**
     * sessionKey
     */
    private String  token_session_key      = "AUTH_SESSION_KEY";

    private int     sessionRedisExpireTime = 1800;

    public int getSessionRedisExpireTime() {
        return sessionRedisExpireTime;
    }

    public void setSessionRedisExpireTime(int sessionRedisExpireTime) {
        this.sessionRedisExpireTime = sessionRedisExpireTime;
    }

    /**
     * 需要验证路径URI
     */
    private final List<String> includePatterns = new ArrayList<String>();
    /**
     * 拦截器忽略路径URI
     */
    private final List<String> excludePatterns = new ArrayList<String>();
    /**
     * 白名单路径
     */
    private final List<String> whiteList       = new ArrayList<>();

    public String filterRulesToString() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("\nInclude URI:");
        for (String include : includePatterns) {
            stringBuffer.append("\t").append(include).append(",");
        }
        stringBuffer.append("\nExclude URI:");
        for (String exclude : excludePatterns) {
            stringBuffer.append("\t").append(exclude).append(",");
        }
        stringBuffer.append("\nWhite List:");
        for (String white : whiteList) {
            stringBuffer.append("\t").append(white).append(",");
        }
        return stringBuffer.toString();
    }

    public String getToken_session_key() {
        return token_session_key;
    }

    public void setToken_session_key(String token_session_key) {
        this.token_session_key = token_session_key;
    }

    public List<String> getIncludePatterns() {
        return includePatterns;
    }

    public List<String> getExcludePatterns() {
        return excludePatterns;
    }

    public List<String> getWhiteList() {
        return whiteList;
    }

}
