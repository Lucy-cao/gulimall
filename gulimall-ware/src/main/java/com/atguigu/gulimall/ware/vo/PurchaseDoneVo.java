package com.atguigu.gulimall.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class PurchaseDoneVo {
    /**
     * 采购单id
     */
    @NotNull(message = "采购单id不能为空")
    private Long id;
    /**
     * 采购需求详情
     */
    private List<ItemVo> items;
    /**
     * 采购需求类
     */
    @Data
    public static class ItemVo{
        /**
         * 采购需求id
         */
        private Long itemId;
        /**
         * 采购需求状态
         */
        private Integer status;
        /**
         * 采购备注
         */
        private String reason;
    }
}
