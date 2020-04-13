package com.sjl.gulimall.product.service.impl;

import com.sjl.gulimall.product.vo.Skus;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sjl.common.utils.PageUtils;
import com.sjl.common.utils.Query;

import com.sjl.gulimall.product.dao.SkuInfoDao;
import com.sjl.gulimall.product.entity.SkuInfoEntity;
import com.sjl.gulimall.product.service.SkuInfoService;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        this.save(skuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        /**
         * key: '华为',//检索关键字
         * catelogId: 0,
         * brandId: 0,
         * min: 0,
         * max: 0
         */
        QueryWrapper<SkuInfoEntity> queryWrapper = new QueryWrapper<>();

        String key = (String) params.get("key");
        if(StringUtils.isNotBlank(key)){
            queryWrapper.and(wrapper -> wrapper.eq("sku_id", key).or().like("sku_name", key));
        }
        String catelogId = (String) params.get("catelogId");
        if(StringUtils.isNotBlank(catelogId) && !"0".equalsIgnoreCase(catelogId)){
            queryWrapper.eq("catalog_id", catelogId);
        }
        String brandId = (String) params.get("brandId");
        if(StringUtils.isNotBlank(brandId) && !"0".equalsIgnoreCase(brandId)){
            queryWrapper.eq("brand_id", brandId);
        }
        String min = (String) params.get("min");
        if(StringUtils.isNotBlank(min)){
            try {
                BigDecimal minPrice = new BigDecimal(min);
                if(minPrice.compareTo(new BigDecimal(0)) == 1){
                    queryWrapper.ge("price", minPrice);
                }
            } catch (Exception ignored) {}
        }
        String max = (String) params.get("max");
        if(StringUtils.isNotBlank(max)){
            try {
                BigDecimal maxPrice = new BigDecimal(max);
                if(maxPrice.compareTo(new BigDecimal(0)) == 1){
                    queryWrapper.le("price", maxPrice);
                }
            } catch (Exception ignored) {}
        }
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                queryWrapper
        );
        return new PageUtils(page);
    }

}