package com.bcht.data_manager.configuration;

import com.bcht.data_manager.interceptors.LoginHandlerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

/**
 * application configuration
 */
@Configuration
public class AppConfiguration extends WebMvcConfigurationSupport {

  public static final String LOGIN_INTERCEPTOR_PATH_PATTERN = "/**/*";
  public static final String LOGIN_PATH_PATTERN = "/api/login/auth";
  public static final String PATH_PATTERN = "/**";

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(loginInterceptor()).addPathPatterns(LOGIN_INTERCEPTOR_PATH_PATTERN).excludePathPatterns(LOGIN_PATH_PATTERN);
  }

  @Bean
  public LoginHandlerInterceptor loginInterceptor() {
    return new LoginHandlerInterceptor();
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping(PATH_PATTERN).allowedOrigins("*").allowedMethods("*");
  }
}
