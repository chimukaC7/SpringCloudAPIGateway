package com.appsdeveloperblog.photoapp.api.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Component
public class MyPostFilter implements GlobalFilter, Ordered {

	final Logger logger = LoggerFactory.getLogger(MyPostFilter.class);

	//this filter will be executed after spring Cloud API Gateway routes HTTP request to a destination webservice endpoint.

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

		return chain.filter(exchange).then(Mono.fromRunnable(()-> {

			logger.info("My last post filter is executed");

		}));
	}

	@Override
	public int getOrder() {
		//if I want this post filter to be executed last, then I need to give it the lowest order index
		//for me, the lowest order index for this post filter will be index zero.
		return 0;
	}

}
