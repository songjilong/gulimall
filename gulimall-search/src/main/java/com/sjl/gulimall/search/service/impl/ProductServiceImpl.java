package com.sjl.gulimall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.sjl.common.to.es.SkuEsModel;
import com.sjl.gulimall.search.config.GulimallElasticsearchConfig;
import com.sjl.gulimall.search.constant.EsConstant;
import com.sjl.gulimall.search.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author songjilong
 * @date 2020/5/11 17:46
 */
@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Resource
    private RestHighLevelClient esClient;

    @Override
    public boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException {
        BulkRequest bulkRequest = new BulkRequest();

        for (SkuEsModel skuEsModel : skuEsModels) {
            //添加数据
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            indexRequest.id(skuEsModel.getSkuId().toString());
            String jsonString = JSON.toJSONString(skuEsModel);
            indexRequest.source(jsonString, XContentType.JSON);
            bulkRequest.add(indexRequest);
        }
        //响应数据
        BulkResponse bulkResponse = esClient.bulk(bulkRequest, GulimallElasticsearchConfig.COMMON_OPTIONS);
        boolean b = bulkResponse.hasFailures();
        if(!b){
            List<String> ids = Arrays.stream(bulkResponse.getItems()).map(BulkItemResponse::getId).collect(Collectors.toList());
            log.error("商品上架出错，ids：{}", ids);
        }
        return b;
    }
}
