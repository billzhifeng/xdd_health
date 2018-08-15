package com.xueduoduo.health.domain.url;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.xueduoduo.health.dal.dao.UrlDOMapper;
import com.xueduoduo.health.dal.dataobject.UrlDO;
import com.xueduoduo.health.dal.dataobject.UrlDOExample;

/**
 * @author wangzhifeng
 * @date 2018年8月10日 下午12:14:59
 */
@Service
public class UrlCache {

    /**
     * 双向唯一对应map
     */
    public BiMap<Long, String> urlMap = HashBiMap.create();

    @Autowired
    private UrlDOMapper        urlDOMapper;

    @PostConstruct
    public void init() {
        UrlDOExample example = new UrlDOExample();
        UrlDOExample.Criteria cri = example.createCriteria();
        List<UrlDO> dos = urlDOMapper.selectByExample(example);
        if (!CollectionUtils.isEmpty(dos)) {
            for (UrlDO src : dos) {
                urlMap.put(src.getId(), src.getUrlPath());
            }
        }
    }

    /**
     * 反向获取ID
     * 
     * @param urlPath
     * @return
     */
    public Long getUrlId(String urlPath) {
        return urlMap.inverse().get(urlPath);
    }

    private Url convert(UrlDO src) {
        Url tar = new Url();
        BeanUtils.copyProperties(src, tar);
        return tar;
    }

}
