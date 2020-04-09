package com.sjl.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.sjl.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.sjl.gulimall.product.dao.AttrGroupDao;
import com.sjl.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.sjl.gulimall.product.entity.AttrGroupEntity;
import com.sjl.gulimall.product.entity.CategoryEntity;
import com.sjl.gulimall.product.service.AttrAttrgroupRelationService;
import com.sjl.gulimall.product.service.AttrGroupService;
import com.sjl.gulimall.product.service.CategoryService;
import com.sjl.gulimall.product.vo.AttrRespVo;
import com.sjl.gulimall.product.vo.AttrVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
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

import com.sjl.gulimall.product.dao.AttrDao;
import com.sjl.gulimall.product.entity.AttrEntity;
import com.sjl.gulimall.product.service.AttrService;
import org.springframework.transaction.annotation.Transactional;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AttrGroupService attrGroupService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void saveAttr(AttrVo attrVo) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrVo, attrEntity);
        this.save(attrEntity);
        //保存关联表信息
        AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
        //查出属性id
        relationEntity.setAttrId(attrEntity.getAttrId());
        relationEntity.setAttrGroupId(attrVo.getAttrGroupId());
        attrAttrgroupRelationService.save(relationEntity);
    }

    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId) {
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if (StringUtils.isNotBlank(key)) {
            queryWrapper.and(wrapper -> wrapper.eq("attr_id", key).or().like("attr_name", key));
        }
        if (catelogId != 0) {
            queryWrapper.eq("catelog_id", catelogId);
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), queryWrapper);

        //原始分页信息
        PageUtils pageUtils = new PageUtils(page);

        //添加上 分类名字、分组名字
        List<AttrRespVo> records = page.getRecords().stream().map(attrEntity -> {
            AttrRespVo attrRespVo = new AttrRespVo();
            BeanUtils.copyProperties(attrEntity, attrRespVo);
            //设置分类名字
            CategoryEntity categoryEntity = categoryService.getById(attrRespVo.getCatelogId());
            if (categoryEntity != null) {
                attrRespVo.setCatelogName(categoryEntity.getName());
            }
            //设置分组名字
            AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationService.getOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrRespVo.getAttrId()));
            if (relationEntity != null && relationEntity.getAttrGroupId() != null) {
                AttrGroupEntity attrGroupEntity = attrGroupService.getById(relationEntity.getAttrGroupId());
                attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
            }

            return attrRespVo;
        }).collect(Collectors.toList());

        pageUtils.setList(records);
        return pageUtils;
    }

    @Override
    public AttrRespVo getAttrInfo(Long attrId) {
        //查出基本信息
        AttrEntity attrEntity = this.getById(attrId);
        AttrRespVo attrRespVo = new AttrRespVo();
        BeanUtils.copyProperties(attrEntity, attrRespVo);

        //设置attrGroupId
        AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrAttrgroupRelationService.getOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));
        if (attrAttrgroupRelationEntity != null) {
            attrRespVo.setAttrGroupId(attrAttrgroupRelationEntity.getAttrGroupId());

        }
        //设置catelogPath
        Long[] catelogPath = categoryService.findCatelogPath(attrEntity.getCatelogId());
        attrRespVo.setCatelogPath(catelogPath);

        return attrRespVo;
    }

    @Override
    @Transactional
    public void updateAttr(AttrVo attrVo) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrVo, attrEntity);
        //更新基本属性
        this.updateById(attrEntity);
        //更新级联属性
        AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
        attrAttrgroupRelationEntity.setAttrGroupId(attrVo.getAttrGroupId());
        attrAttrgroupRelationService.update(attrAttrgroupRelationEntity,new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrVo.getAttrId()));
    }

}