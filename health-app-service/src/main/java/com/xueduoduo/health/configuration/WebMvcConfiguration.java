package com.xueduoduo.health.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 处理静态资源展示
 * 
 * @author wangzhifeng
 * @date 2018年8月23日 下午5:26:19
 */
@Configuration
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {

    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/files/**").addResourceLocations("file:D:/files/");
        registry.addResourceHandler("/data/heartTest/files/**").addResourceLocations("file:/data/heartTest/files/");
        registry.addResourceHandler("/data/heartTest/healthTestFiles/**")
                .addResourceLocations("file:/data/heartTest/healthTestFiles/");
        super.addResourceHandlers(registry);
    }

}
