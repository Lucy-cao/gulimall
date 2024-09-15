package com.atguigu.gulimall.product.feign;

import com.atguigu.common.to.SkuHasStockVo;
import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 远程调用仓库服务
 */
@FeignClient("gulimall-ware")
public interface WareFeignService {
	@PostMapping("/ware/waresku/hasStock")
	R hasStock(@RequestBody List<Long> skuIds);
}
