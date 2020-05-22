package com.sjl.gulimall.search.service.impl;

import com.sjl.gulimall.search.config.GulimallElasticsearchConfig;
import com.sjl.gulimall.search.constant.EsConstant;
import com.sjl.gulimall.search.service.MallSearchService;
import com.sjl.gulimall.search.vo.SearchParam;
import com.sjl.gulimall.search.vo.SearchResult;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchAction;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @author songjilong
 * @date 2020/5/20 16:52
 */
@Service
public class MallSearchServiceImpl implements MallSearchService {

    @Autowired
    private RestHighLevelClient client;

    @Override
    public SearchResult search(SearchParam param) {
        SearchResult result = null;
        //构建检索请求
        SearchRequest searchRequest = buildSearchRequest(param);
        try {
            //执行检索请求
            SearchResponse response = client.search(searchRequest, GulimallElasticsearchConfig.COMMON_OPTIONS);
            //封装响应结果
            result = buildSearchResponse(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 构建检索请求
     *
     * @return
     */
    private SearchRequest buildSearchRequest(SearchParam param) {
        SearchSourceBuilder source = new SearchSourceBuilder();
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //========查询========
        //关键字模糊匹配
        if (StringUtils.isNotBlank(param.getKeyword())) {
            boolQuery.must(QueryBuilders.matchQuery("skuTitle", param.getKeyword()));
        }
        //根据分类id查询
        if (param.getCatalog3Id() != null) {
            boolQuery.filter(QueryBuilders.termQuery("catalogId", param.getCatalog3Id()));
        }
        //根据是否有库存查询
        boolQuery.filter(QueryBuilders.termQuery("hasStock", param.getHasStock() == 1));
        //根据品牌id查询
        if (param.getBrandId() != null && param.getBrandId().size() != 0) {
            boolQuery.filter(QueryBuilders.termQuery("brandId", param.getBrandId()));
        }
        //根据价格区间查询
        String skuPrice = param.getSkuPrice();
        if (StringUtils.isNotBlank(skuPrice)) {
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");
            /*
             * 规则：
             * skuPrice=100_500
             * skuPrice=_500
             * skuPrice=500_
             */
            String[] s = skuPrice.split("_");
            if (s.length == 2) {
                rangeQuery.gte(s[0]).lte(s[1]);
            } else {
                if (skuPrice.startsWith("_")) {
                    rangeQuery.lte(s[0]);
                } else if (skuPrice.endsWith("_")) {
                    rangeQuery.gte(s[0]);
                }
            }
            boolQuery.filter(rangeQuery);
        }
        //根据属性查询
        List<String> attrs = param.getAttrs();
        if (attrs != null && attrs.size() != 0) {
            for (String attr : attrs) {
                BoolQueryBuilder boolQuery1 = QueryBuilders.boolQuery();
                /*
                 * 规则：
                 * attrs=1_cpu:内存
                 * attrs=2_安卓:苹果:其它
                 */
                String[] s1 = attr.split("_");
                String attrId = s1[0];
                boolQuery1.must(QueryBuilders.termQuery("attrs.attrId", attrId));
                String[] attrValues = s1[1].split(":");
                boolQuery1.must(QueryBuilders.termQuery("attrs.attrValue", attrValues));
                NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrs", boolQuery1, ScoreMode.None);
                boolQuery.filter(nestedQuery);
            }
        }
        source.query(boolQuery);
        //========排序、分页、高亮========
        //排序
        String sortStr = param.getSort();
        if (StringUtils.isNotBlank(sortStr)) {
            /*
             * 规则：
             * sort=saleCount_asc/desc
             * sort=skuPrice_asc/desc
             * sort=hotScore_asc/desc
             */
            String[] s = sortStr.split("_");
            FieldSortBuilder sort = SortBuilders.fieldSort(s[0]);
            sort.order(StringUtils.equalsIgnoreCase(s[1], "asc") ? SortOrder.ASC : SortOrder.DESC);
            source.sort(sort);
        }
        //分页
        Integer pageNum = param.getPageNum();
        source.from((pageNum - 1) * EsConstant.PRODUCT_PAGE_SIZE);
        source.size(EsConstant.PRODUCT_PAGE_SIZE);
        //高亮
        if (StringUtils.isNotBlank(param.getKeyword())) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle").preTags("<em>").postTags("</em>");
            source.highlighter(highlightBuilder);
        }
        System.out.println("DSL语句：" + source.toString());
        //========聚合========

        return new SearchRequest(new String[]{"gulimall_product"}, source);
    }

    /**
     * 封装响应结果
     *
     * @param response
     * @return
     */
    private SearchResult buildSearchResponse(SearchResponse response) {
        return null;
    }
}
