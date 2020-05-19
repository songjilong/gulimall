package com.sjl.gulimall.search.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author songjilong
 * @date 2020/5/19 21:51
 */
@Controller
public class SearchController {

    @GetMapping("/list.html")
    public String list() {
        return "list";
    }
}
