package com.atguigu.gulimall.ware.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class PurchaseVo {

    /**
     * 采购单id
     */
    private Long id;
    /**
     * 采购人id
     */
    private Long assigneeId;
    /**
     * 采购人名
     */
    private String assigneeName;
    /**
     * 联系方式
     */
    private String phone;
    /**
     * 优先级
     */
    private Integer priority;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 仓库id
     */
    private Long wareId;
    /**
     * 仓库名称
     */
    private String wareName;
    /**
     * 总金额
     */
    private BigDecimal amount;
    /**
     * 创建日期
     */
    private Date createTime;
    /**
     * 更新日期
     */
    private Date updateTime;
}
