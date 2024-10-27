package com.atguigu.gulimall.search.service;

import com.atguigu.gulimall.search.vo.SearchParam;
import com.atguigu.gulimall.search.vo.SearchResult;

public interface SearchService {
	/**
	 * 根据条件筛选出符合的数据
	 * @param param 检索的所有参数
	 * @return 返回检索的结果，里面包含页面所需所有结果
	 */
	SearchResult search(SearchParam param);
}
