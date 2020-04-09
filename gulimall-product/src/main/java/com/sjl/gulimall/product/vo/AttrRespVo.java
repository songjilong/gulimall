package com.sjl.gulimall.product.vo;

import lombok.Data;

/**
 * @author songjilong
 * @date 2020/4/9 22:43
 */
@Data
public class AttrRespVo extends AttrVo {
    /**
     * 所属分类名字
     */
    private String catelogName;
    /**
     * 所属分组名字
     */
    private String groupName;
    /**
     * 分类路径 [父、子、孙]
     */
    private Long[] catelogPath;
}
