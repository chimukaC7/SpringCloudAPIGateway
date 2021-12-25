package com.appsdeveloperblog.photoapp.api.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import reactor.core.publisher.Mono;

@Configuration
public class GlobalFiltersConfiguration {

	final Logger logger = LoggerFactory.getLogger(GlobalFiltersConfiguration.class);

	//Global which means that they will execute for every route that our Spring Cloud API Gateway
	//will make to a destination microservices
	//
	//The main difference between pre filter and post filter is that pre filter is executed before Spring Cloud.
	//API Gateway routes HTTP request to a destination microservice while
	// post filter is executed after the request is routed to a destination microservice.


	// I can actually make this function to work as both prefilled and post filter
	@Order(1)//To specify a specific order in which your filters need to be executed, you can use order annotation
	//So for this method here, the prefilled their code will be executed first by the post filter code will
	//be executed last when it comes to pre filters, the lower the order index, the higher is the priority
	//But for the post filters, the lower the value of order index, the lower is the execution priority
	//for the post filter.
	@Bean
	public GlobalFilter secondPreFilter() {

		return (exchange, chain) -> {

			logger.info("My second global pre-filter is executed...");

			return chain.filter(exchange).then(Mono.fromRunnable(() -> {
				logger.info("Third post-filter executed...");
			}));

		};

	}

	@Order(2)
	@Bean
	public GlobalFilter thirdPreFilter() {

		return (exchange, chain) -> {

			logger.info("My third global pre-filter is executed...");

			return chain.filter(exchange).then(Mono.fromRunnable(() -> {
				logger.info("My second post-filter is executed...");
			}));
		};

	}

	@Order(3)
	@Bean
	public GlobalFilter fourthPreFilter() {

		return (exchange, chain) -> {

			logger.info("My fourth global pre-filter is executed...");

			return chain.filter(exchange).then(Mono.fromRunnable(() -> {
				logger.info("My first post-filter is executed");
			}));
		};

	}

}
