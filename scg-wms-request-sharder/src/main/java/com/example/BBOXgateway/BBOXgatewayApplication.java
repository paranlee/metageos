package com.example.BBOXgateway;

import com.example.BBOXgateway.model.MyConfig;
import io.netty.util.internal.StringUtil;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.RouteToRequestUrlFilter;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;


@RestController
@SpringBootApplication
public class BBOXgatewayApplication {

	@Autowired
	private CustomerFilter customerFilter;

	public BBOXgatewayApplication(CustomerFilter customerFilter) {
		this.customerFilter = customerFilter;
	}

	@RequestMapping("/hystrixfallback")
	public String hystrixfallback() {
		return "This is a fallback";
	}

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		//@formatter:off
		return builder.routes()
				.route("path_route", r -> r.path("/geoserver/**")
					.filters(f -> f.filter(customerFilter)).uri("no://op"))
				.build();
		//@formatter:on
	}

	@Bean
	RedisRateLimiter redisRateLimiter() {
		return new RedisRateLimiter(1, 2);
	}

	@Bean
	SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http) throws Exception {
		return http.httpBasic().and()
				.csrf().disable()
				.authorizeExchange()
				.pathMatchers("/anything/**").authenticated()
				.anyExchange().permitAll()
				.and()
				.build();
	}

	@Bean
	public MapReactiveUserDetailsService reactiveUserDetailsService() {
		UserDetails user = User.withDefaultPasswordEncoder().username("user").password("password").roles("USER").build();
		return new MapReactiveUserDetailsService(user);
	}

	public static void main(String[] args) {
		SpringApplication.run(BBOXgatewayApplication.class, args);
	}
}

@Service
class CustomerFilter implements GatewayFilter, Ordered {

	@Autowired
	MyConfig myConfig;

	CustomerFilter(MyConfig myConfig) {
		this.myConfig = myConfig;
	}

	@Override
	public int getOrder() {
		return RouteToRequestUrlFilter.ROUTE_TO_URL_FILTER_ORDER + 1;
	}

	@SneakyThrows
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();

		URI uri = getUriFromBBOX(request);

		// boolean encoded = ServerWebExchangeUtils.containsEncodedParts(uri);
		URI requestURI = UriComponentsBuilder.fromHttpRequest(request)
				.scheme(uri.getScheme())
				.host(uri.getHost())
				.port(uri.getPort())
				.build(true).toUri();

		exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR, requestURI);

		return chain.filter(exchange);
	}

	private URI getUriFromBBOX(ServerHttpRequest request) throws URISyntaxException {
		List<MyConfig.RouteElm> list = myConfig.getList();
		String uri = (list.get(0)).getUri();

		String bbox = request.getQueryParams().getFirst("BBOX");
		if (!StringUtil.isNullOrEmpty(bbox)) {
			String [] paramArr = bbox.split(",");
			int minx = Double.valueOf(paramArr[0]).intValue();
			int miny = Double.valueOf(paramArr[1]).intValue();
			int maxx = Double.valueOf(paramArr[2]).intValue();
			int maxy = Double.valueOf(paramArr[3]).intValue();

			Optional<MyConfig.RouteElm> elm = list.stream().parallel()
					.filter(v -> v.getMinx() >= minx && v.getMiny() >= miny
							&& v.getMaxx() < maxx && v.getMaxy() < maxy
					).findFirst();

			if(elm.isPresent()) {
				uri = elm.get().getUri();
			}
		}

		return new URI(uri);
	}
}
