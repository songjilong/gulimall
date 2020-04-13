package com.sjl.gulimall.product.service.impl;

import com.sjl.gulimall.product.service.AttrService;
import com.sjl.gulimall.product.vo.BaseAttrs;
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

import com.sjl.gulimall.product.dao.ProductAttrValueDao;
import com.sjl.gulimall.product.entity.ProductAttrValueEntity;
import com.sjl.gulimall.product.service.ProductAttrValueService;
import org.springframework.validation.annotation.Validated;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveBaseAttrs(List<ProductAttrValueEntity> productAttrValueList) {
        if(productAttrValueList == null || productAttrValueList.size() == 0){
            return;
        }
        this.saveBatch(productAttrValueList);
    }

}