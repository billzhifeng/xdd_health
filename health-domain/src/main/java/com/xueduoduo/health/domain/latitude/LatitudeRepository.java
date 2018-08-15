package com.xueduoduo.health.domain.latitude;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.java.common.base.Page;
import com.github.java.common.utils.JavaAssert;
import com.xueduoduo.health.dal.dao.LatitudeDOMapper;
import com.xueduoduo.health.dal.dataobject.LatitudeDO;
import com.xueduoduo.health.dal.dataobject.LatitudeDOExample;
import com.xueduoduo.health.domain.common.HealthException;
import com.xueduoduo.health.domain.enums.IsDeleted;
import com.xueduoduo.health.domain.enums.ReturnCode;

/**
 * @author wangzhifeng
 * @date 2018年8月12日 上午12:18:25
 */
@Service
public class LatitudeRepository {

    private static final Logger logger = LoggerFactory.getLogger(LatitudeRepository.class);

    @Autowired
    private LatitudeDOMapper    latitudeDAO;

    /**
     * 全部查
     * 
     * @param req
     * @return
     */
    public List<Latitude> loadAll() {
        LatitudeDOExample example = new LatitudeDOExample();
        LatitudeDOExample.Criteria cri = example.createCriteria();
        List<LatitudeDO> dos = latitudeDAO.selectByExample(example);
        List<Latitude> list = new ArrayList<Latitude>();
        if (!CollectionUtils.isEmpty(dos)) {
            for (LatitudeDO src : dos) {
                list.add(convert(src));
            }
        }
        return list;
    }

    /**
     * 分页查
     * 
     * @param req
     * @return
     */
    public Page<Latitude> loadPage(String schoolYear, String displayName, String offSetStr, String lengthStr) {

        Page<Latitude> page = new Page<Latitude>();

        LatitudeDOExample example = new LatitudeDOExample();
        LatitudeDOExample.Criteria cri = example.createCriteria();

        int length = -1;
        int offSet = -1;
        if (StringUtils.isNotBlank(offSetStr)) {
            offSet = Integer.parseInt(offSetStr);
            if (StringUtils.isNotBlank(lengthStr)) {
                length = Integer.parseInt(lengthStr);
            } else {
                length = 10;
            }
        }
        //需要分页
        if (offSet > -1 && length > 0) {
            example.setOffSet(offSet);
            example.setLength(length);

            page.setLength(length);
            page.setOffSet(offSet);
        }

        if (!StringUtils.isEmpty(schoolYear)) {
            cri.andSchoolYearEqualTo(schoolYear);
        }

        if (!StringUtils.isEmpty(displayName)) {
            displayName = "%" + displayName + "%";
            cri.andDisplayNameLike(displayName);
        }

        long counts = latitudeDAO.countByExample(example);
        if (0 == counts) {
            page.setTotalCountNum(0);
            return page;
        }

        cri.andIsDeletedEqualTo(IsDeleted.N.name());

        List<LatitudeDO> dos = latitudeDAO.selectByExample(example);
        List<Latitude> list = new ArrayList<Latitude>();
        for (LatitudeDO src : dos) {
            list.add(convert(src));
        }
        page.setPageData(list);
        page.setTotalCountNum((int) counts);
        return page;
    }

    private Latitude convert(LatitudeDO src) {
        Latitude tar = new Latitude();
        BeanUtils.copyProperties(src, tar);
        return tar;
    }

    @Transactional
    public void save(String displayName, String year) {
        LatitudeDO ld = new LatitudeDO();
        ld.setSchoolYear(year);
        Date today = new Date();
        ld.setCreatedTime(today);
        ld.setUpdatedTime(today);
        ld.setDisplayName(displayName);
        ld.setIsDeleted(IsDeleted.N.name());
        int count = latitudeDAO.insertSelective(ld);
        JavaAssert.isTrue(1 == count, ReturnCode.DB_ERROR, "纬度保存异常", HealthException.class);
    }

    @Transactional
    public void update(Latitude src) {
        LatitudeDO ld = new LatitudeDO();
        ld.setId(src.getId());
        Date today = new Date();
        ld.setUpdatedTime(today);
        ld.setDisplayName(src.getDisplayName());
        ld.setIsDeleted(src.getIsDeleted());
        int count = latitudeDAO.updateByPrimaryKeySelective(ld);
        JavaAssert.isTrue(1 == count, ReturnCode.DB_ERROR, "纬度更新异常", HealthException.class);
    }

    @Transactional
    public void updateToDelete(Latitude src) {
        LatitudeDO ld = new LatitudeDO();
        ld.setId(src.getId());
        Date today = new Date();
        ld.setUpdatedTime(today);
        ld.setDisplayName(src.getDisplayName());
        ld.setIsDeleted(src.getIsDeleted());

        LatitudeDO db = latitudeDAO.selectByPrimaryKey(src.getId());
        if (null == db || db.getIsDeleted().equals(IsDeleted.Y.name())) {
            throw new HealthException(ReturnCode.DATA_NOT_EXIST, "该纬度不存在或已删除");
        }

        try {
            int count = latitudeDAO.updateByPrimaryKeySelective(ld);
            JavaAssert.isTrue(1 == count, ReturnCode.DB_ERROR, "纬度删除异常", HealthException.class);
        } catch (Exception e) {
            LatitudeDO dbInstance = latitudeDAO.selectByPrimaryKey(src.getId());
            if (null == dbInstance) {
                throw new HealthException(ReturnCode.DATA_NOT_EXIST, "该纬度不存在或已删除");
            }
        }
    }
}
