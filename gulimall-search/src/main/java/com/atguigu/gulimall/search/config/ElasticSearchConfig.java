package com.atguigu.gulimall.search.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticSearchConfig {
	@Value("${es.host}")
	private String esHost;
	@Value("${es.port}")
	private Integer esPort;
	@Bean
	public ElasticsearchClient esClient() {
		System.out.println("esHost = " + esHost);
		System.out.println("esPort = " + esPort);
		// Create the low-level client
		RestClient restClient = RestClient.builder(
				new HttpHost(esHost, esPort)).build();

		// Create the transport with a Jackson mapper
		ElasticsearchTransport transport = new RestClientTransport(
				restClient, new JacksonJsonpMapper());

		// And create the API client。后续都注入这个Client进行使用
		// 同步阻塞客户端
		ElasticsearchClient esClient = new ElasticsearchClient(transport);

		return esClient;
	}
}
