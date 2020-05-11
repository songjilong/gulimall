package com.sjl.gulimall.search.service;

import com.sjl.common.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

/**
 * @author songjilong
 * @date 2020/5/11 17:45
 */
public interface ProductService {
    boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException;
}
