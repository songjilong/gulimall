package com.sjl.gulimall.product.service.impl;

import com.sjl.gulimall.product.entity.AttrEntity;
import com.sjl.gulimall.product.service.AttrService;
import com.sjl.gulimall.product.vo.AttrgroupWithAttrsVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Autowired
    private AttrService attrService;

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
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<>();
        //如果key不为空，再加上 key 的查询条件
        String key = (String) params.get("key");
        if (StringUtils.isNotBlank(key)) {
            wrapper.and(obj -> obj.eq("attr_group_id", key).or().like("attr_group_name", key));
        }
        if (catelogId == 0) {
            //查询所有
            page = this.page(new Query<AttrGroupEntity>().getPage(params), wrapper);
        } else {
            //根据 catelogId 查询
            wrapper.eq("catelog_id", catelogId);
            page = this.page(new Query<AttrGroupEntity>().getPage(params), wrapper);
        }
        return new PageUtils(page);
    }

    /**
     * 获取分类下的所有属性分组及关联关系
     */
    @Override
    public List<AttrgroupWithAttrsVo> attrgroupWithAttrsByCatelogId(Long catelogId) {
        //获取分类下的分组
        List<AttrGroupEntity> attrGroupList = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        //获取分组下的属性
        return attrGroupList.stream().map(attrGroupEntity -> {
            AttrgroupWithAttrsVo vo = new AttrgroupWithAttrsVo();
            BeanUtils.copyProperties(attrGroupEntity, vo);
            List<AttrEntity> attrs = attrService.getAttrRelation(attrGroupEntity.getAttrGroupId());
            vo.setAttrs(attrs);
            return vo;
        }).collect(Collectors.toList());
    }

}