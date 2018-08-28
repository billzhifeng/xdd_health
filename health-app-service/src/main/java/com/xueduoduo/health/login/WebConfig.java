package com.xueduoduo.health.login;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.xueduoduo.health.dal.dao.RoleUrlMappingScoreDOMapper;
import com.xueduoduo.health.dal.dao.UrlDOMapper;
import com.xueduoduo.health.dal.dataobject.RoleUrlMappingScoreDO;
import com.xueduoduo.health.dal.dataobject.RoleUrlMappingScoreDOExample;
import com.xueduoduo.health.dal.dataobject.UrlDO;
import com.xueduoduo.health.dal.dataobject.UrlDOExample;
import com.xueduoduo.health.domain.enums.IsDeleted;

@Configuration
@EnableWebMvc
@ComponentScan
public class WebConfig extends WebMvcConfigurerAdapter implements ApplicationContextAware {

    private ApplicationContext          applicationContext;

    @Autowired
    private RoleUrlMappingScoreDOMapper map;
    @Autowired
    private UrlDOMapper                 urlMapper;

    private Map<String, Set<String>>    roleAndUrl = new HashMap<String, Set<String>>();

    @Value("${role.auth.enable}")
    private boolean                     roleAuthEnable;
    @Value("${allow.Origin.ip}")
    private String                      allowOriginIp;

    private void init() {
        UrlDOExample ue = new UrlDOExample();
        UrlDOExample.Criteria uci = ue.createCriteria();
        uci.andIsDeletedEqualTo(IsDeleted.N.name());
        List<UrlDO> urls = urlMapper.selectByExample(ue);
        if (CollectionUtils.isEmpty(urls)) {
            return;
        }
        Map<Long, String> urlmaps = new HashMap<Long, String>();
        for (UrlDO u : urls) {
            urlmaps.put(u.getId(), u.getUrlPath());
        }

        RoleUrlMappingScoreDOExample example = new RoleUrlMappingScoreDOExample();
        RoleUrlMappingScoreDOExample.Criteria cri = example.createCriteria();
        cri.andIsDeletedEqualTo(IsDeleted.N.name());
        List<RoleUrlMappingScoreDO> ins = map.selectByExample(example);

        if (CollectionUtils.isNotEmpty(ins)) {
            for (RoleUrlMappingScoreDO rm : ins) {
                if (roleAndUrl.containsKey(rm.getRole())) {
                    Set<String> urlSet = roleAndUrl.get(rm.getRole());
                    urlSet.add(urlmaps.get(rm.getUrlId()));
                } else {
                    Set<String> urlSet = new HashSet<String>();
                    urlSet.add(urlmaps.get(rm.getUrlId()));
                    roleAndUrl.put(rm.getRole(), urlSet);
                }
            }
        }
    }

    public WebConfig() {
        super();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX + "/static/");
        registry.addResourceHandler("/templates/**")
                .addResourceLocations(ResourceUtils.CLASSPATH_URL_PREFIX + "/templates/");

        super.addResourceHandlers(registry);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        init();
        //拦截规则：除了login，其他都拦截判断
        registry.addInterceptor(new MyInterceptor(roleAndUrl, roleAuthEnable, allowOriginIp))
                .addPathPatterns("/admin/**").addPathPatterns("/student/**").excludePathPatterns("/toLogin")
                .excludePathPatterns("/login").excludePathPatterns("/admin/account/uploadTeachers")
                .excludePathPatterns("/admin/account/uploadStudents").excludePathPatterns("/files/**");

        super.addInterceptors(registry);
    }

}
