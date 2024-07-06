package com.atguigu.common.to;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SpuBoundsTo {
    /**
     * spu id
     */
    private Long spuId;
    /**
     * 金币
     */
    private BigDecimal buyBounds;
    /**
     * 成长值
     */
    private BigDecimal growBounds;
}
