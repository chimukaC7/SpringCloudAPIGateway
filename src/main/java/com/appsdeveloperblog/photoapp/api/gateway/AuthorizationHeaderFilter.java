package com.appsdeveloperblog.photoapp.api.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.google.common.net.HttpHeaders;

import io.jsonwebtoken.Jwts;
import reactor.core.publisher.Mono;

@Component
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

	/*
	* we're going to create a custom filter class that can be assigned to a specific gateway route
	 it will be executed before spring cloud API gateway routes HTTP request to a destination microservice.
	 *
	 * I will use this filter to check if the request to a certain point does contain JWT token and
	 * if the provided JWT token has been signed with the correct token secret.
	 *
	 * I will use this particular filter to read the authorization header. So I'm calling it authorization header filter
	 *
	 * Now that we have a custom filter class created, I will assign it to a specific route for which I want this filter to be executed.
	* */


	@Autowired
	Environment env;

	public AuthorizationHeaderFilter() {
		super(Config.class);
	}

	public static class Config {
		// Put configuration properties here
	}

	//this is the function that gets triggered when our authorization header filter gets executed.
	//And this is where the main business logic is written.
	@Override
	public GatewayFilter apply(Config config) {

		//this time the code style that we will write will slightly differ and we will use Java lambdas.
		//this is because spring Cloud API gateway was created to be non blocking and to support reactive programming.
		return (exchange, chain) -> {

			ServerHttpRequest request = exchange.getRequest();

			if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
				return onError(exchange, "No authorization header", HttpStatus.UNAUTHORIZED);
			}

			String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
			String jwt = authorizationHeader.replace("Bearer", "");

			if (!isJwtValid(jwt)) {
				return onError(exchange, "JWT token is not valid", HttpStatus.UNAUTHORIZED);
			}

			return chain.filter(exchange);
		};
	}

	private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
		ServerHttpResponse response = exchange.getResponse();
		response.setStatusCode(httpStatus);

		return response.setComplete();
	}

	private boolean isJwtValid(String jwt) {
		boolean returnValue = true;

		String subject = null;

		try {

			subject = Jwts
					.parser()//for this parser to be able to parse claims that this JWT token contains,
					// we will need to set the signing key with which this JWT token was signed when it was initially created
					.setSigningKey(env.getProperty("token.secret"))//
					.parseClaimsJws(jwt)
					.getBody()
					.getSubject();

		} catch (Exception ex) {
			returnValue = false;
		}

		if (subject == null || subject.isEmpty()) {
			returnValue = false;
		}

		return returnValue;
	}

}
