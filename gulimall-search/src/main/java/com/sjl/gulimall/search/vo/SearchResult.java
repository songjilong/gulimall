package com.sjl.gulimall.search.vo;

import com.sjl.common.to.es.SkuEsModel;
import lombok.Data;

import java.util.List;

/**
 * @author songjilong
 * @date 2020/5/20 17:20
 */
@Data
public class SearchResult {
    /**
     * 商品信息
     */
    private List<SkuEsModel> products;
    /**
     * 当前页码
     */
    private Integer pageNum;
    /**
     * 总记录数
     */
    private Long total;
    /**
     * 总页数
     */
    private Integer totalPage;
    /**
     * 品牌信息
     */
    private List<BrandVo> brands;
    /**
     * 属性信息
     */
    private List<AttrVo> attrs;
    /**
     * 分类信息
     */
    private List<CatalogVo> catalogs;


    @Data
    public static class BrandVo {
        private Long brandId;
        private String brandName;
        private String brandImg;
    }
    @Data
    public static class AttrVo{
        private Long attrId;
        private String attrName;
        private String attrValue;
    }
    @Data
    public static class CatalogVo{
        private Long catalogId;
        private String catalogName;
    }
}
