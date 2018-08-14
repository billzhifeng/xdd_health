package com.xueduoduo.health.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author wangzhifeng
 * @date 2018年8月9日 下午3:19:16
 */
@Configuration
@EnableSwagger2
public class UtilsAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(SpringContextHolder.class)
    @ConditionalOnExpression("${spring-context-holder-enable:false}")
    public SpringContextHolder springContextHolder(ApplicationContext applicationContext) {
        return new SpringContextHolder(applicationContext);
    }

    @Bean
    @ConditionalOnMissingBean(Docket.class)
    @ConditionalOnExpression("${swagger-enable:false}")
    public Docket buildDocket() {
        ApiInfo apiInfo = new ApiInfoBuilder().title("Spring Boot中使用Swagger2 UI构建API文档").contact("COM").version("1.0")
                .build();
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo).select()
                .apis(RequestHandlerSelectors.basePackage("com")) // 要扫描的API(Controller)基础包
                .paths(PathSelectors.any()).build();
    }

}
