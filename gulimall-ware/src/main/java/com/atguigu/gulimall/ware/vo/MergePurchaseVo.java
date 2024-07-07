package com.atguigu.gulimall.ware.vo;

import lombok.Data;

import javax.validation.constraints.Size;
import java.util.List;

@Data
public class MergePurchaseVo {
    /**
     * 采购单id
     */
    private Long purchaseId;
    /**
     * 待合并的采购需求
     */
    @Size(min = 1, message = "请选择需要合并的采购需求")
    private List<Long> items;
}
