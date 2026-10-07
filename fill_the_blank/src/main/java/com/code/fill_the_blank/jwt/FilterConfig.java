package com.code.fill_the_blank.jwt;

import org.springframework.boot.web.servlet.AbstractFilterRegistrationBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

	@Bean
	public AbstractFilterRegistrationBean<JwtAuthenticationFilter> jwtFilter(
			JwtAuthenticationFilter jwtAuthenticationFilter) {
		FilterRegistrationBean<JwtAuthenticationFilter> registrationBean = new FilterRegistrationBean<>();
		
		registrationBean.setFilter(jwtAuthenticationFilter);
		registrationBean.addUrlPatterns("/*"); // 모든 요청에 필터 적용
		registrationBean.setOrder(1);
		
		return registrationBean;
	}
}
