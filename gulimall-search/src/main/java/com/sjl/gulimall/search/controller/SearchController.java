package com.sjl.gulimall.search.controller;

import com.sjl.gulimall.search.service.MallSearchService;
import com.sjl.gulimall.search.vo.SearchParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author songjilong
 * @date 2020/5/19 21:51
 */
@Controller
public class SearchController {

    @Autowired
    private MallSearchService mallSearchService;

    @GetMapping("/list.html")
    public String list(@RequestBody SearchParam param) {
        Object result = mallSearchService.search(param);
        return "list";
    }
}
