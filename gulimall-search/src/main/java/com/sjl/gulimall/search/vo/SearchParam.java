package com.sjl.gulimall.search.vo;

import lombok.Data;

import java.util.List;

/**
 * @author songjilong
 * @date 2020/5/20 16:42
 */
@Data
public class SearchParam {
    /**
     * 页面传递过来的全文匹配关键字
     */
    private String keyword;
    /**
     * 三级分类id
     */
    private Long catalog3Id;
    /**
     * 排序条件
     * sort=saleCount_asc/desc
     * sort=skuPrice_asc/desc
     * sort=hotScore_asc/desc
     */
    private String sort;
    /**
     * 是否只显示有货
     */
    private Integer hasStock;
    /**
     * 价格区间查询
     * skuPrice=100_500
     * skuPrice=_500
     * skuPrice=500_
     */
    private String skuPrice;
    /**
     * 品牌id，可以多选
     */
    private List<Long> brandId;
    /**
     * 按照属性进行筛选
     * attrs=1_cpu:内存
     * attrs=2_安卓:苹果:其它
     */
    private List<String> attrs;
    /**
     * 页码
     */
    private Integer pageNum;

}
