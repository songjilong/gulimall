package com.sjl.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sjl.common.utils.PageUtils;
import com.sjl.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.sjl.gulimall.product.vo.AttrRelationVo;

import java.util.Map;

/**
 * 属性&属性分组关联
 *
 * @author songjilong
 * @email 2606587750@qq.com
 * @date 2020-04-01 23:18:20
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 批量删除属性与属性分组关联数据
     * @param vos
     */
    void deleteAttrGroupRelation(AttrRelationVo[] vos);

    /**
     * 添加属性与分组的关联关系
     * @param vos
     */
    void addRelation(AttrRelationVo[] vos);
}

