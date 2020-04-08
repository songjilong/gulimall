package com.sjl.gulimall.product.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sjl.common.utils.PageUtils;
import com.sjl.common.utils.Query;

import com.sjl.gulimall.product.dao.AttrGroupDao;
import com.sjl.gulimall.product.entity.AttrGroupEntity;
import com.sjl.gulimall.product.service.AttrGroupService;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
        IPage<AttrGroupEntity> page;
        if (catelogId == 0) {
            //查询所有
            page = this.page(new Query<AttrGroupEntity>().getPage(params),
                    new QueryWrapper<>());
        } else {
            QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<>();
            //根据 catelogId 查询
            wrapper.eq("catelog_id", catelogId);
            //如果key不为空，再加上 key 的查询条件
            String key = (String) params.get("key");
            if (StringUtils.isNotBlank(key)) {
                wrapper.and(obj -> obj.eq("attr_group_id", key).or().like("attr_group_name", key));
            }
            page = this.page(new Query<AttrGroupEntity>().getPage(params), wrapper);
        }
        return new PageUtils(page);
    }

}