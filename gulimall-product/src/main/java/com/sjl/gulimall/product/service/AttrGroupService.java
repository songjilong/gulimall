package com.sjl.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sjl.common.utils.PageUtils;
import com.sjl.gulimall.product.entity.AttrGroupEntity;
import com.sjl.gulimall.product.vo.AttrgroupWithAttrsVo;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author songjilong
 * @email 2606587750@qq.com
 * @date 2020-04-01 23:18:20
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params, Long catelogId);

    /**
     * 获取分类下的所有属性分组及关联关系
     */
    List<AttrgroupWithAttrsVo> attrgroupWithAttrsByCatelogId(Long catelogId);
}

