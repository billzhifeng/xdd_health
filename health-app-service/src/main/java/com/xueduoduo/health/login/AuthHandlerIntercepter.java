package com.xueduoduo.health.login;
//package com.xueduoduo.health.intercepter;
//
//import java.io.IOException;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.web.servlet.ModelAndView;
//import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
//
//public class AuthHandlerIntercepter extends HandlerInterceptorAdapter {
//
//    private static final Logger logger      = LoggerFactory.getLogger(AuthHandlerIntercepter.class);
//
//    //拦截相关配置
//    private AuthProperties      authProperties;
//
//    private final static String SESSION_KEY = "SESSIONID";
//
//    public AuthHandlerIntercepter(AuthProperties authProperties) {
//        this.authProperties = authProperties;
//    }
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
//            throws IOException {
//        String headerToken = request.getHeader(authProperties.getToken_header_key());
//
//        if (StringUtils.isNotBlank(headerToken)) {
//            logger.info("Header:{}-{}", authProperties.getToken_header_key(), headerToken);
//            if (getTokenFromRedis(headerToken, request)) {
//                //将SessionId放到Header中，并且需要cors中addExposedHeader()开放权限
//                response.addHeader(SESSION_KEY, request.getSession().getId());
//                return true;
//            }
//        }
//
//        //-------------- 检查seesion是否登录 --------------------//
//        String userToken = null;
//        if (request.getSession() != null) {
//            userToken = (String) request.getSession().getAttribute(authProperties.getToken_session_key());
//            if (StringUtils.isNotBlank(userToken)) {
//                logger.info("Session:{}-{}", authProperties.getToken_session_key(), userToken);
//
//                return true;
//            }
//        }
//
//        //-------------- 检查ip是否是白名单 ----------------------//
//        String ip = getClientIpAddr(request);
//        if (isWhiteListIp(ip)) {
//            logger.debug("White List Ip:{}.:)", ip);
//            return true;
//        }
//        if (HttpMethod.OPTIONS.name().equalsIgnoreCase(request.getMethod())) {
//            logger.debug("{}请求", HttpMethod.OPTIONS.name());
//            return true;
//        }
//
//        logger.warn(
//                "RemoteHost:{} No Authorization,please get token first!URL:{}-{},AuthHeader:{}-{},SessionId:{},SessionKey:{}-{}",
//                request.getRemoteHost(), request.getMethod(), request.getRequestURL(),
//                authProperties.getToken_header_key(), headerToken, request.getSession().getId(),
//                authProperties.getToken_session_key(), userToken);
//
//        response.setStatus(403);
//        response.getWriter().append("No Authorization!");
//        return false;
//
//    }
//
//    public boolean getTokenFromRedis(String token, HttpServletRequest request) {
//        try {
//            String tokenKey = REDIS_TOKEN_PREFIX + token;
//            String redisToken = redisTemplate.opsForValue().get(REDIS_TOKEN_PREFIX + token);
//            if (StringUtils.isNotBlank(redisToken)) {
//                logger.debug("\nkey:{}\nvalue:{}", token, redisToken);
//                request.getSession().setAttribute(authProperties.getToken_session_key(), redisToken);
//                redisTemplate.delete(REDIS_TOKEN_PREFIX + token);
//                return true;
//            } else {
//                logger.warn("Key:{} value is blank.", tokenKey);
//                return false;
//            }
//        } catch (Exception e) {
//            logger.error("Redis get Token Exception-{}", e.getMessage(), e);
//            return false;
//        }
//
//    }
//
//    @Override
//    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
//                           ModelAndView modelAndView)
//            throws Exception {
//        // TODO Auto-generated method stub
//    }
//
//    @Override
//    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
//            throws Exception {
//        // TODO Auto-generated method stub
//        logger.info("afterCompletion:\nsessionId:{}", request.getSession().getId());
//        response.addCookie(new Cookie("set-Cookie", "session=" + request.getSession().getId()));
//        response.setHeader("session", request.getSession().getId());
//    }
//
//    /**
//     * 白名单过滤
//     * 
//     * @param ip
//     * @return
//     */
//    public boolean isWhiteListIp(String ip) {
//        for (String whiteIp : authProperties.getWhiteList()) {
//            if (ip.equalsIgnoreCase(whiteIp)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    /***
//     * 获取客户端ip地址(可以穿透代理)
//     * 
//     * @param request
//     * @return
//     */
//    public static String getClientIpAddr(HttpServletRequest request) {
//        String ip = request.getHeader("X-Forwarded-For");
//        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
//            ip = request.getHeader("Proxy-Client-IP");
//        }
//        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
//            ip = request.getHeader("WL-Proxy-Client-IP");
//        }
//        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
//            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
//        }
//        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
//            ip = request.getHeader("HTTP_X_FORWARDED");
//        }
//        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
//            ip = request.getHeader("HTTP_X_CLUSTER_CLIENT_IP");
//        }
//        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
//            ip = request.getHeader("HTTP_CLIENT_IP");
//        }
//        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
//            ip = request.getHeader("HTTP_FORWARDED_FOR");
//        }
//        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
//            ip = request.getHeader("HTTP_FORWARDED");
//        }
//        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
//            ip = request.getHeader("HTTP_VIA");
//        }
//        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
//            ip = request.getHeader("REMOTE_ADDR");
//        }
//        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
//            ip = request.getRemoteAddr();
//        }
//        return ip;
//    }
//
//    public static String getIpAddr(HttpServletRequest request) {
//        String ip = request.getHeader("X-Real-IP");
//        if (null != ip && !"".equals(ip.trim()) && !"unknown".equalsIgnoreCase(ip)) {
//            return ip;
//        }
//        ip = request.getHeader("X-Forwarded-For");
//        if (null != ip && !"".equals(ip.trim()) && !"unknown".equalsIgnoreCase(ip)) {
//            // get first ip from proxy ip
//            int index = ip.indexOf(',');
//            if (index != -1) {
//                return ip.substring(0, index);
//            } else {
//                return ip;
//            }
//        }
//        return request.getRemoteAddr();
//    }
//}
