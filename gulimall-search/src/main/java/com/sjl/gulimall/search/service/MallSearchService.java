package com.sjl.gulimall.search.service;

import com.sjl.gulimall.search.vo.SearchParam;
import com.sjl.gulimall.search.vo.SearchResult;

/**
 * @author songjilong
 * @date 2020/5/20 16:52
 */
public interface MallSearchService {

    SearchResult search(SearchParam param);
}
