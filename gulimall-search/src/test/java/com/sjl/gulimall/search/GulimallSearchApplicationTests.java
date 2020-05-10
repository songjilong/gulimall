package com.sjl.gulimall.search;

import com.alibaba.fastjson.JSON;
import com.sjl.gulimall.search.config.GulimallElasticsearchConfig;
import com.sjl.gulimall.search.entity.BankEntity;
import com.sjl.gulimall.search.entity.User;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class GulimallSearchApplicationTests {

    @Autowired
    private RestHighLevelClient client;

    @Test
    public void aggSearch() throws IOException {
        SearchRequest searchRequest = new SearchRequest("bank");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //匹配全部
        sourceBuilder.query(QueryBuilders.matchAllQuery());
        //聚合查询
        //根据年龄聚合
        TermsAggregationBuilder ageAgg = AggregationBuilders.terms("ageAgg").field("age").size(100);
        sourceBuilder.aggregation(ageAgg);

        //子聚合，根据性别聚合
        TermsAggregationBuilder ageGenderAgg = AggregationBuilders.terms("ageGenderAgg").field("gender.keyword");
        ageAgg.subAggregation(ageGenderAgg);
        //子子聚合，工资平均值
        AvgAggregationBuilder genderBalanceAvg = AggregationBuilders.avg("genderBalanceAvg").field("balance");
        ageGenderAgg.subAggregation(genderBalanceAvg);

        //子聚合，工资平均值
        AvgAggregationBuilder ageBalanceAgg = AggregationBuilders.avg("ageBalanceAgg").field("balance");
        ageAgg.subAggregation(ageBalanceAgg);

        System.out.println("检索条件：" + sourceBuilder.toString());

        //设置检索条件
        searchRequest.source(sourceBuilder);

        //获取结果
        SearchResponse searchResponse = client.search(searchRequest, GulimallElasticsearchConfig.COMMON_OPTIONS);
        System.out.println("检索结果：" + searchResponse);

        //解析结果
        //hits
        SearchHit[] hits = searchResponse.getHits().getHits();
        System.out.println("==========hit===========");
        for (SearchHit hit : hits) {
            String source = hit.getSourceAsString();
            BankEntity bankEntity = JSON.parseObject(source, BankEntity.class);
            System.out.println(bankEntity.toString());
            System.out.println("==========hit===========");
        }
        //aggs
        System.out.println("*******************************");
        Aggregations aggregations = searchResponse.getAggregations();
        Terms ageAgg1 = aggregations.get("ageAgg");
        for (Terms.Bucket bucket : ageAgg1.getBuckets()) {
            Number age = bucket.getKeyAsNumber();
            System.out.println("年龄：" + age);
            Aggregations aggregations1 = bucket.getAggregations();
            Terms ageGenderAgg1 = aggregations1.get("ageGenderAgg");
            for (Terms.Bucket ageGenderAgg1Bucket : ageGenderAgg1.getBuckets()) {
                String gender = ageGenderAgg1Bucket.getKeyAsString();
                System.out.println("年龄：" + age + "，性别：" + gender);
                Aggregations aggregations2 = ageGenderAgg1Bucket.getAggregations();
                Avg genderBalanceAvg1 = aggregations2.get("genderBalanceAvg");
                System.out.println("年龄：" + age + "，性别：" + gender + "，平均工资：" + genderBalanceAvg1.getValue());
            }
            Avg ageBalanceAgg1 = aggregations1.get("ageBalanceAgg");
            System.out.println("年龄：" + age + "，平均工资：" + ageBalanceAgg1.getValue());
            System.out.println("*******************************");
        }
    }


    @Test
    public void indexData() throws IOException {
        IndexRequest request = new IndexRequest("users").id("1");
        //request.source("name", "张三", "age", 12);
        String jsonString = JSON.toJSONString(new User("张无忌", 33));
        request.source(jsonString, XContentType.JSON);
        //执行操作
        IndexResponse index = client.index(request, GulimallElasticsearchConfig.COMMON_OPTIONS);

        System.out.println(index);
    }


    @Test
    void contextLoads() {
        System.out.println(client);
    }

}
