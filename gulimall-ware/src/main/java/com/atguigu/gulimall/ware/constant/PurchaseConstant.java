package com.atguigu.gulimall.ware.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

public interface PurchaseConstant {
    @AllArgsConstructor
    @Getter
    enum PurchaseStatusEnum {
        //采购需求状态：[0新建，1已分配，2已领取，3已完成，4有异常]
        NEW(0, "新建"),
        ALLOCATED(1, "已分配"),
        GOTTED(2, "已领取"),
        DONE(3, "已完成"),
        FAIL(4, "有异常");

        private int code;
        private String desc;
    }


    @AllArgsConstructor
    @Getter
    enum PurchaseDetailStatusEnum {
        //采购需求状态：[0新建，1已分配，2正在采购，3已完成，4采购失败]
        NEW(0, "新建"),
        ALLOCATED(1, "已分配"),
        PURCHASING(2, "正在采购"),
        DONE(3, "已完成"),
        FAIL(4, "失败");

        private int code;
        private String desc;
    }
}
