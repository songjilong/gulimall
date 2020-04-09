package com.sjl.gulimall.product.service.impl;

import com.sjl.gulimall.product.service.CategoryBrandRelationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sjl.common.utils.PageUtils;
import com.sjl.common.utils.Query;

import com.sjl.gulimall.product.dao.BrandDao;
import com.sjl.gulimall.product.entity.BrandEntity;
import com.sjl.gulimall.product.service.BrandService;
import org.springframework.transaction.annotation.Transactional;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String key = (String) params.get("key");
        QueryWrapper<BrandEntity> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(key)) {
            queryWrapper.eq("brand_id", key).or().like("name", key);
        }
        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                queryWrapper
        );
        return new PageUtils(page);
    }

    /**
     *
     * @param brand
     */
    @Override
    @Transactional
    public void updateDetail(BrandEntity brand) {
        this.updateById(brand);
        //更新 分类品牌关联表 的信息
        if(StringUtils.isNotEmpty(brand.getName())){
            categoryBrandRelationService.updateBrand(brand.getBrandId(), brand.getName());
            //TODO 更新其他关联信息
        }
    }

}