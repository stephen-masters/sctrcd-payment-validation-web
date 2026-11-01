package com.sctrcd.payments.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

/**
 * The Spring MVC set-up that used to live in <code>payment-web-servlet.xml</code>.
 * Component scanning, the view resolver and the static resource handler for
 * <code>/resources/**</code> now come from Spring Boot (see
 * <code>application.properties</code>).
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Saves a locale change using a cookie. Spring Boot backs off its own
     * locale resolver when a bean of this name exists.
     */
    @Bean
    public LocaleResolver localeResolver() {
        return new CookieLocaleResolver();
    }

    /**
     * Changes the locale when a <code>locale</code> request parameter is sent;
     * for example <code>/?locale=de</code>.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LocaleChangeInterceptor());
    }

    /**
     * Forwards requests for the <code>/</code> resource to the <code>welcome</code> view.
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("welcome");
    }
}
