package com.sjl.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sjl.common.utils.PageUtils;
import com.sjl.gulimall.product.entity.BrandEntity;

import java.util.Map;

/**
 * 品牌
 *
 * @author songjilong
 * @email 2606587750@qq.com
 * @date 2020-04-01 23:18:20
 */
public interface BrandService extends IService<BrandEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void updateDetail(BrandEntity brand);
}

