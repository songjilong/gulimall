package com.sjl.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sjl.common.utils.PageUtils;
import com.sjl.gulimall.product.entity.CategoryBrandRelationEntity;

import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author songjilong
 * @email 2606587750@qq.com
 * @date 2020-04-01 23:18:20
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveDetail(CategoryBrandRelationEntity categoryBrandRelation);

    void updateBrand(Long brandId, String name);

    void updateCategory(Long catId, String name);
}

