package com.appsdeveloperblog.photoapp.api.gateway;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class MyPreFilter implements GlobalFilter, Ordered {

	final Logger logger = LoggerFactory.getLogger(MyPreFilter.class);

	//This filter is going to be pre filter and it will be executed for every single HTTP request before it
	//is routed to a destination microservice

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

		logger.info("My first Pre-filter is executed...");

		String requestPath = exchange.getRequest().getPath().toString();
		logger.info("Request path = " + requestPath);

		HttpHeaders headers = exchange.getRequest().getHeaders();

		Set<String> headerNames = headers.keySet();

		headerNames.forEach((headerName)-> {

			String headerValue = headers.getFirst(headerName);
			logger.info(headerName + " " + headerValue);

		});

		return chain.filter(exchange);
	}

	@Override
	public int getOrder() {
		//to apply order to this pre filter class.
		//I will need to make it implement a new interface that is called ordered
		//So here we can simply return an order index and the same rules apply to these type of global filter as well

		//Pre filters with lower index will be executed first and post filters with lower index will be executed last.
		return 0;
	}

}
