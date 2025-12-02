package com.qingyunyouxiao.sbsn.config;

import java.util.Arrays;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.filter.CorsFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

@Configuration
@EnableWebMvc
public class WebConfig {
    
    private static final Long MAX_AGE_SECS = 3600L;
    private static final int CORS_FILTER_ORDER = -102;
    
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        UrlBasedCorsConfigurationSource sourse = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");
        config.setAllowedHeaders(Arrays.asList(
            HttpHeaders.AUTHORIZATION, 
            HttpHeaders.CONTENT_TYPE, 
            HttpHeaders.ACCEPT));
        config.setAllowedMethods(Arrays.asList(
        HttpMethod.GET.name(), 
        HttpMethod.POST.name(), 
        HttpMethod.PUT.name(), 
        HttpMethod.DELETE.name()));

        config.setMaxAge(MAX_AGE_SECS);
        sourse.registerCorsConfiguration("/**", config);
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<CorsFilter>(new CorsFilter(sourse));
        
        // should be set order to -100 because we need to CorsFilter before SpringSecurityFilter 
        bean.setOrder(CORS_FILTER_ORDER);
        return bean;
    }
}
