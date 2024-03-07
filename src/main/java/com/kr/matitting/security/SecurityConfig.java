package com.kr.matitting.security;

import com.kr.matitting.jwt.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.reactive.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.Arrays;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
        http
                //http default 인증 관련
                .httpBasic(hb -> hb.disable())
                .csrf(cr -> cr.disable())
                .cors(withDefaults());

        http
                //token 기반 무상태성 설정
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http
                //인증 허용 관련 설정
                .authorizeHttpRequests(
                        getCustomizer(introspector,
                                "/", "/matitting**", "/member/signupForm", "/oauth2/**", "/resources/**", "/demo-ui.html",
                                "/swagger-ui/**", "/api-docs/**", "/api/main", "/api/search**", "/api/search/rank", "/api/party/{partyId}",
                                "/webjars/**", "/favicon.ico")
                )
                .formLogin((form) -> form
                        .loginPage("/")
                        .permitAll());

        http
                //jwt custom filter 적용
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http
                .exceptionHandling(exception -> exception.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));

        return http.build();
    }

    private Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> getCustomizer(HandlerMappingIntrospector introspector, String... patterns) {
        return (request) -> {
            Arrays.stream(patterns).forEach(pattern -> {
                if (PatternMatchUtils.simpleMatch("/api/party/**", pattern)
                        && !pattern.contains("-")) {
                    MvcRequestMatcher mvc = new MvcRequestMatcher(introspector, pattern);
                    mvc.setMethod(HttpMethod.GET);
                    request.requestMatchers(mvc).permitAll();
                }else {
                    request.requestMatchers(new MvcRequestMatcher(introspector, pattern)).permitAll();
                }
            });
            request.anyRequest().authenticated();
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(Arrays.asList("Authorization", "Authorization-Refresh"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}