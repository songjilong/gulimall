package com.sjl.gulimall.product.vo;

import com.sjl.gulimall.product.entity.AttrEntity;
import lombok.Data;

import java.util.List;

/**
 * @author songjilong
 * @date 2020/4/12 17:31
 */
@Data
public class AttrgroupWithAttrsVo {
    /**
     * 分组id
     */
    private Long attrGroupId;
    /**
     * 组名
     */
    private String attrGroupName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 描述
     */
    private String descript;
    /**
     * 组图标
     */
    private String icon;
    /**
     * 所属分类id
     */
    private Long catelogId;
    /**
     * 该分组下的所有属性
     */
    private List<AttrEntity> attrs;
}
