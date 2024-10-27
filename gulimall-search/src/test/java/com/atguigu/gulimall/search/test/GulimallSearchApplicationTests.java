package com.atguigu.gulimall.search.test;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.NodeStatistics;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.cat.IndicesResponse;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.TotalHits;
import co.elastic.clients.elasticsearch.core.search.TotalHitsRelation;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import co.elastic.clients.util.ApiTypeHelper;
import com.atguigu.gulimall.search.constant.EsConstant;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.*;

@SpringBootTest
class GulimallSearchApplicationTests {
	@Autowired
	private ElasticsearchClient esClient;

	@Test
	public void testEsSearch() throws IOException {
		System.out.println("esClient = " + esClient);
		SearchResponse<BankAccountEntity> search = esClient.search(s -> s
						.index("bank")
						.query(q -> q.match(t -> t.field("account_number").query(20))),
				BankAccountEntity.class);

		for (Hit<BankAccountEntity> hit : search.hits().hits()) {
			System.out.println("hit.source() = " + hit.source());
		}

		System.out.println("新增文档===========================");
		BankAccountEntity bankAccountEntity = new BankAccountEntity();
		bankAccountEntity.setAccount_number(3000);
		bankAccountEntity.setBalance(13000);
		bankAccountEntity.setFirstname("cao");
		bankAccountEntity.setLastname("uuu11");
		bankAccountEntity.setAge(20);
		bankAccountEntity.setGender("F");
		bankAccountEntity.setAddress("浙江杭州");
		bankAccountEntity.setEmployer("yyhhgd");
		bankAccountEntity.setEmail("8765@163.com");
		bankAccountEntity.setCity("杭州");
		bankAccountEntity.setState("IL");

		IndexResponse bank = esClient.index(i -> i.index("bank").id(String.valueOf(bankAccountEntity.getAccount_number()))
				.document(bankAccountEntity));
		System.out.println("bank = " + bank);

		System.out.println("根据id查询========================");
		GetResponse<BankAccountEntity> bank1 = esClient.get(g ->
						g.index("bank").id(String.valueOf(bankAccountEntity.getAccount_number())),
				BankAccountEntity.class);
		System.out.println("bank1 = " + bank1);
	}

	@Test
	public void test02() throws IOException {
		//1、创建索引
//		CreateIndexResponse products = esClient.indices().create(c -> c.index("products"));
//		System.out.println("products = " + products);

		if (esClient.exists(b -> b.index("products").id("foo")).value()) {
			System.out.println("product exists");
		}
		System.out.println("===========================");
		RestClient restClient = RestClient.builder(
				new HttpHost("localhost", 9200)).build();
		// Create the transport with a Jackson mapper
		ElasticsearchTransport transport = new RestClientTransport(
				restClient, new JacksonJsonpMapper());
		//异步非阻塞客户端
		ElasticsearchAsyncClient asyncClient = new ElasticsearchAsyncClient(transport);
		asyncClient.exists(b -> b.index("products").id("foo"))
				.whenComplete((response, exception) -> {
					if (exception != null) {
						System.out.println("获取索引失败");
					} else {
						System.out.println("product exists");
					}
					System.out.println("response = " + response);
					System.out.println("exception = " + exception);
				});
		System.out.println("没阻塞，结束了");
	}

	@Test
	public void test03() {
		//准备索引列表
		List<String> names = Arrays.asList("idx-a", "idx-b", "idx-c");
		//准备聚合函数，对foo和bar字段进行计数
		HashMap<String, Aggregation> cardinalities = new HashMap<>();
		cardinalities.put("foo-count", Aggregation.of(s -> s.cardinality(c -> c.field("foo"))));
		cardinalities.put("bar-count", Aggregation.of(s -> s.cardinality(c -> c.field("bar"))));
		SortOptions balance = SortOptions.of(s -> s.field(f -> f.field("balance").order(SortOrder.Desc)));

		//聚合函数计算size的平均值
		Aggregation avgSize = Aggregation.of(s -> s.avg(c -> c.field("size")));

		// 需要查询出的部分字段
		List<String> sourceFields = Arrays.asList("firstname", "lastname", "account_number", "balance");

		//SearchRequest构建DSL复杂检索
		SearchRequest searchRequest = SearchRequest.of(r -> r
						.index("bank")
						//查询全部或按条件查询
//				.query(q->q.matchAll(m->m.queryName("findAll")))
//				.query(q->q.match(m->m.field("account_number").query(20)))
//				.query(q->q.match(m->m.field("address").query("mill road")))
//				.query(q->q.matchPhrase(m->m.field("address").query("mill road")))
						.query(q -> q.multiMatch(m -> m.query("mill").fields("address", "state")))
						// 两种排序写法
						.sort(s -> s.field(f -> f.field("age").order(SortOrder.Asc)))
						.sort(balance)
						//分页写法
						.from(0).size(10)
						//获取部分字段
						.source(s -> s.filter(f -> f.includes(sourceFields)))
		);
		System.out.println("searchRequest = " + searchRequest);
	}

	@Test
	public void test05() {
		List<Query> queryList = new ArrayList<>();
		queryList.add(new Query.Builder().match(s -> s.field("address").query("mill")).build());
		queryList.add(new Query.Builder().match(s -> s.field("gender").query("M")).build());
		//测试多条件查询
		SearchRequest searchRequest = SearchRequest.of(r -> r
				.query(q -> q.bool(b -> b
						.must(queryList)
						.should(new Query.Builder().match(s -> s.field("address").query("lane")).build())
						.mustNot(new Query.Builder().match(s -> s.field("email").query("@baluba.com")).build()))));
		System.out.println("searchRequest = " + searchRequest);
	}

	@Test
	public void test06() {
		// 写法一
		SearchRequest searchRequest = SearchRequest.of(r -> r
				.query(q -> q.match(m -> m.field("address").query("mill")))
				.aggregations("ageAgg", a -> a.terms(t -> t.field("age").size(100)))
				.aggregations("ageAvg", a -> a.avg(v -> v.field("age"))));
		System.out.println("searchRequest = " + searchRequest);
		System.out.println("=====================================");
		// 写法二
		Map<String, Aggregation> aggregations = new HashMap<>();
		aggregations.put("ageAgg", Aggregation.of(a -> a.terms(t -> t.field("age").size(100))));
		aggregations.put("ageAvg", Aggregation.of(a -> a.avg(v -> v.field("age"))));
		SearchRequest searchRequest2 = SearchRequest.of(r -> r
				.query(q -> q.match(m -> m.field("address").query("mill")))
				.aggregations(aggregations));
		System.out.println("searchRequest2 = " + searchRequest2);
	}

	@Test
	public void test07() throws IOException {
		//子聚合
		SearchRequest searchRequest = SearchRequest.of(r -> r
				.index("bank")
				.query(q -> q.match(m -> m.field("address").query("mill")))
				.aggregations("ageAgg", a -> a.terms(t -> t.field("age").size(100))
						.aggregations("balanceAvg", v -> v.avg(b -> b.field("balance"))))
				.aggregations("ageAvg", a -> a.avg(v -> v.field("age"))));
		System.out.println("searchRequest = " + searchRequest);

		SearchResponse<BankAccountEntity> search = esClient.search(searchRequest, BankAccountEntity.class);
		System.out.println("search = " + search);
	}

	@Test
	public void test08() {
		NodeStatistics stats = NodeStatistics.of(r -> r.total(1).failed(0).successful(1));
		System.out.println(stats.failures());
		// - it's not null
		assert (stats.failures() != null);
// - it's empty
		assert (stats.failures().size() == 0);
// - and if needed we can know it was actually not defined
		System.out.println(ApiTypeHelper.isDefined(stats.failures()));
		assert (!ApiTypeHelper.isDefined(stats.failures()));

		Aggregation aggregation = new Aggregation.Builder().avg(a -> a.field("age")).build();
		System.out.println("aggregation.isTerms() = " + aggregation.isTerms());
		System.out.println("aggregation._customKind() = " + aggregation._customKind());
		System.out.println("aggregation._get() = " + aggregation._get());
		System.out.println("aggregation._kind() = " + aggregation._kind());
		System.out.println("aggregation._isCustom() = " + aggregation._isCustom());
	}

	@Test
	public void test09() throws IOException {
		//创建索引
//		CreateIndexResponse products = esClient.indices().create(c -> c.index("products"));
//		System.out.println("products = " + products);

		//查看所有索引
		IndicesResponse indices = esClient.cat().indices();
		System.out.println("indices = " + indices);
	}

	@Test
	public void test10() throws IOException {
		//创建文档
//		Product product = new Product("bk-1", "City bike", 123.0);
//		IndexResponse indexResponse = esClient.index(i -> i
//				.index("products")
//				.id(product.getSKU())
//				.document(product));
//		System.out.println("indexResponse = " + indexResponse);

		//写法二
//		Product product1 = new Product("bk-2", "Bike Type2", 2239.0);
//		IndexRequest<Object> indexRequest1 = IndexRequest.of(i -> i.index("products")
//				.id(product1.getSKU())
//				.document(product1));
//		IndexResponse indexResponse1 = esClient.index(indexRequest1);
//		System.out.println("indexResponse1 = " + indexResponse1);

		//写法三
		Product product2 = new Product("bk-3", "Road Bike", 2590.0);
		IndexRequest<Object> indexRequest2 = new IndexRequest.Builder<>()
				.index("products").id(product2.getSKU()).document(product2).build();
		IndexResponse indexResponse2 = esClient.index(indexRequest2);
		System.out.println("indexResponse2 = " + indexResponse2);
	}

	@Test
	public void test11() throws IOException {
		//通过json创建文档
		Reader input = new StringReader(
				"{'@timestamp': '2022-04-08T13:55:32Z', 'level': 'warn', 'message': 'Some log message'}"
						.replace('\'', '"'));

		IndexRequest<JsonData> request = IndexRequest.of(i -> i
				.index("logs")
				.withJson(input)
		);

		IndexResponse response = esClient.index(request);
		System.out.println("response = " + response);
	}

	@Test
	public void test12() throws IOException {
		GetResponse<Product> products = esClient.get(g -> g.index("products").id("bk-1"), Product.class);
		System.out.println("products = " + products);
	}

	@Test
	public void test13() throws IOException {
		String searchText = "bike";
		SearchRequest searchRequest = SearchRequest.of(s -> s.index("products")
				.query(q -> q
						.match(m -> m
								.field("name")
								.query(searchText))));

		SearchResponse<Product> response = esClient.search(searchRequest, Product.class);
		TotalHits total = response.hits().total();
		boolean isExactResult = total.relation() == TotalHitsRelation.Eq;

		if (isExactResult) {
			System.out.println("There are " + total.value() + " results.");
		} else {
			System.out.println("More than " + total.value() + " results.");
		}

		List<Hit<Product>> hits = response.hits().hits();
		for (Hit<Product> hit : hits) {
			Product source = hit.source();
			System.out.println("source = " + source);
		}

	}

	@Test
	public void test14() throws IOException {
		//全文匹配检索
		Query textQuery = MatchQuery.of(m -> m.field("name").query("bike"))._toQuery();
		//范围匹配检索
		Query priceQuery = RangeQuery.of(r -> r.field("price").gte(JsonData.of(200)))._toQuery();
		//构造请求体
		SearchRequest searchRequest = SearchRequest.of(s -> s
				.query(q -> q
						.bool(b -> b
								.must(textQuery)
								.must(priceQuery))));
		//发送搜索请求
		SearchResponse<Product> response = esClient.search(searchRequest, Product.class);
		System.out.println("response.hits().hits() = " + response.hits().hits());
	}

	@Test
	public void test15() throws IOException {
		Query query = MatchQuery.of(m -> m.field("name").query("bike"))._toQuery();
		Aggregation price = Aggregation.of(a -> a.histogram(h -> h.field("price").interval(50.0)));

		SearchRequest request = SearchRequest.of(s -> s
				.index("products")
				.size(0)
				.query(query)
				.aggregations("price-histogram", price));
		SearchResponse<Product> search = esClient.search(request, Product.class);
		System.out.println("search = " + search);
	}

	@Test
	public void test(){
//		String skuPrice = "_500";
//		List<String> skuRange = Arrays.asList(skuPrice.split("_"));
//		System.out.println("1");
//
//		skuPrice = "500_";
//		skuRange = Arrays.asList(skuPrice.split("_"));
//		System.out.println("1");
//
//		skuPrice = "10_500";
//		skuRange = Arrays.asList(skuPrice.split("_"));
//		System.out.println("1");

		int ceil =(int) Math.ceil(3.0 / 2.0);
		System.out.println(1);
		ceil = (int) Math.ceil(4 / 2);
		System.out.println(1);
	}
}
