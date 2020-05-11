package com.sjl.common.constant;

/**
 * @author songjilong
 * @date 2020/4/10 16:11
 */
public class ProductConstant {
    public enum AttrEnum {
        //code：数据库对应的值  msg：提示消息
        ATTR_TYPE_SALE(0, "销售属性"),
        ATTR_TYPE_BASE(1, "基本属性");

        private int code;
        private String msg;

        AttrEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }

    public enum StatusEnum {
        //code：数据库对应的值  msg：提示消息
        NEW_SPU(0, "新建"),
        SPU_UP(1, "商品上架"),
        SPU_DOWN(2, "商品下架");

        private int code;
        private String msg;

        StatusEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }
}
