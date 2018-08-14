package com.xueduoduo.health.domain.url;

import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.xueduoduo.health.dal.dao.RoleUrlMappingScoreDOMapper;
import com.xueduoduo.health.dal.dataobject.RoleUrlMappingScoreDO;
import com.xueduoduo.health.dal.dataobject.RoleUrlMappingScoreDOExample;

/**
 * @author wangzhifeng
 * @date 2018年8月10日 下午11:23:15
 */
@Service
public class RoleUrlMapCache {
    private static final Logger         logger      = LoggerFactory.getLogger(RoleUrlMapCache.class);
    //role,List<url>
    private Multimap<String, Long>      roleUrlsMap = ArrayListMultimap.create();
    @Autowired
    private RoleUrlMappingScoreDOMapper mapper;

    @PostConstruct
    private void init() {
        RoleUrlMappingScoreDOExample example = new RoleUrlMappingScoreDOExample();
        RoleUrlMappingScoreDOExample.Criteria cri = example.createCriteria();
        List<RoleUrlMappingScoreDO> map = mapper.selectByExample(example);
        for (RoleUrlMappingScoreDO m : map) {
            roleUrlsMap.put(m.getRole(), m.getUrlId());
        }
    }

    /**
     * 角色是否包含该功能
     * 
     * @param role
     * @param urlId
     */
    public boolean containUrl(String role, Long urlId) {
        List<Long> urlIds = (List) roleUrlsMap.get(role);
        if (null != urlIds && urlIds.contains(urlId)) {
            return true;
        }
        return false;
    }
}
