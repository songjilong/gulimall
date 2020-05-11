package com.sjl.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sjl.common.constant.ProductConstant;
import com.sjl.gulimall.product.entity.*;
import com.sjl.gulimall.product.service.*;
import com.sjl.gulimall.product.vo.AttrRespVo;
import com.sjl.gulimall.product.vo.AttrVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sjl.common.utils.PageUtils;
import com.sjl.common.utils.Query;

import com.sjl.gulimall.product.dao.AttrDao;
import org.springframework.transaction.annotation.Transactional;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AttrGroupService attrGroupService;
    @Autowired
    private AttrService attrService;
    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAttr(AttrVo attrVo) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrVo, attrEntity);
        this.save(attrEntity);
        //如果是基本属性，保存关联表信息
        if (attrVo.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() && attrVo.getAttrGroupId() != null) {
            //查出属性id
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrId(attrEntity.getAttrId());
            relationEntity.setAttrGroupId(attrVo.getAttrGroupId());
            attrAttrgroupRelationService.save(relationEntity);
        }
    }

    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, String attrType, Long catelogId) {
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>()
                .eq("attr_type", "base".equalsIgnoreCase(attrType) ? ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() : ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());

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
            if ("base".equalsIgnoreCase(attrType)) {
                AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationService.getOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrRespVo.getAttrId()));
                if (relationEntity != null && relationEntity.getAttrGroupId() != null) {
                    AttrGroupEntity attrGroupEntity = attrGroupService.getById(relationEntity.getAttrGroupId());
                    attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
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
        if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            //设置attrGroupId
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrAttrgroupRelationService.getOne(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrId));
            if (attrAttrgroupRelationEntity != null) {
                attrRespVo.setAttrGroupId(attrAttrgroupRelationEntity.getAttrGroupId());
            }
        }
        //设置catelogPath
        Long[] catelogPath = categoryService.findCatelogPath(attrEntity.getCatelogId());
        attrRespVo.setCatelogPath(catelogPath);

        return attrRespVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAttr(AttrVo attrVo) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrVo, attrEntity);
        //更新基本属性
        this.updateById(attrEntity);
        //更新级联属性
        if (attrEntity.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            attrAttrgroupRelationEntity.setAttrGroupId(attrVo.getAttrGroupId());
            attrAttrgroupRelationService.update(attrAttrgroupRelationEntity, new UpdateWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrVo.getAttrId()));
        }
    }

    @Override
    public List<AttrEntity> getAttrRelation(Long attrGroupId) {
        List<AttrAttrgroupRelationEntity> relationEntities = attrAttrgroupRelationService.list(
                new QueryWrapper<AttrAttrgroupRelationEntity>()
                        .eq("attr_group_id", attrGroupId));
        List<Long> ids = relationEntities.stream()
                .map(AttrAttrgroupRelationEntity::getAttrId)
                .collect(Collectors.toList());
        if(ids.size() == 0){
            return null;
        }
        return (List<AttrEntity>) attrService.listByIds(ids);
    }

    @Override
    public PageUtils getAttrNoRelation(Map<String, Object> params, Long attrGroupId) {
        //1、当前分组只能关联自己所属分类下的属性
        Long catelogId = attrGroupService.getById(attrGroupId).getCatelogId();

        //2、当前分组只能关联别的分组没有关联的属性
        //2.1 查询当前分类下的所有分组
        List<AttrGroupEntity> attrGroups = attrGroupService.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        List<Long> attrGroupIds = attrGroups.stream().map(AttrGroupEntity::getAttrGroupId).collect(Collectors.toList());
        //2.2 查询这些分组关联的属性
        List<AttrAttrgroupRelationEntity> relations = attrAttrgroupRelationService.list(new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id", attrGroupIds));
        List<Long> attrIds = relations.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
        //2.3 从所有属性中移除已关联的属性
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>().eq("catelog_id", catelogId);
        if(attrIds.size() > 0){
            queryWrapper.notIn("attr_id", attrIds);
        }

        String key = (String) params.get("key");
        if(StringUtils.isNotBlank(key)){
            queryWrapper.and(wrapper -> wrapper.eq("attr_id", key).or().like("attr_name", key));
        }

        //设置分页信息
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), queryWrapper);
        return new PageUtils(page);
    }

    @Override
    public List<ProductAttrValueEntity> listAttrValueForSpu(Long spuId) {
        return productAttrValueService.list(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateAttrValueBySpuId(Long spuId, List<ProductAttrValueEntity> attrValues) {
        //删除spuid下的所有属性
        productAttrValueService.remove(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));
        //添加属性
        attrValues.forEach(attrValue -> attrValue.setSpuId(spuId));
        productAttrValueService.saveBaseAttrs(attrValues);
    }

    @Override
    public List<Long> querySearchAttrIds(List<Long> attrIds) {
        Collection<AttrEntity> attrs = listByIds(attrIds);
        return attrs.stream()
                .filter(attr -> attr.getSearchType() == 1)
                .map(attr -> attr.getAttrId())
                .collect(Collectors.toList());
    }

}