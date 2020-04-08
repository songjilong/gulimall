package com.sjl.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import com.sjl.common.valid.ListValue;
import com.sjl.common.valid.SaveGroup;
import com.sjl.common.valid.UpdateGroup;
import com.sjl.common.valid.UpdateStatusGroup;
import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * 品牌
 *
 * @author songjilong
 * @email 2606587750@qq.com
 * @date 2020-04-01 23:18:20
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 品牌id
     */
    @NotNull(message = "品牌id不能为空", groups = {UpdateGroup.class, UpdateStatusGroup.class})
    @Null(message = "品牌id必须为空", groups = SaveGroup.class)
    @TableId
    private Long brandId;
    /**
     * 品牌名
     */
    @NotBlank(message = "品牌名不能为空", groups = SaveGroup.class)
    private String name;
    /**
     * 品牌logo地址
     */
    @NotBlank(message = "品牌logo地址不能为空", groups = SaveGroup.class)
    @URL(message = "品牌logo地址需要是一个合法的url地址", groups = {SaveGroup.class, UpdateGroup.class})
    private String logo;
    /**
     * 介绍
     */
    @NotBlank(message = "品牌介绍不能为空", groups = SaveGroup.class)
    private String descript;
    /**
     * 显示状态[0-不显示；1-显示]
     */
    @NotNull(message = "显示状态不能为空", groups = {SaveGroup.class, UpdateStatusGroup.class})
    @ListValue(vals = {0, 1}, groups = {SaveGroup.class, UpdateGroup.class, UpdateStatusGroup.class})
    private Integer showStatus;
    /**
     * 检索首字母
     */
    @NotBlank(message = "检索首字母不能为空", groups = SaveGroup.class)
    @Pattern(regexp = "^[a-zA-Z]$", message = "检索首字母必须是一个a-z或A-Z的字母", groups = {SaveGroup.class, UpdateGroup.class})
    private String firstLetter;
    /**
     * 排序
     */
    @NotNull(message = "排序不能为空", groups = SaveGroup.class)
    @Min(value = 0, message = "排序必须大于或等于0", groups = {SaveGroup.class, UpdateGroup.class})
    private Integer sort;

}
