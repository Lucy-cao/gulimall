package com.atguigu.gulimall.search.test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
	private String SKU;
	private String name;
	private Double price;
}
