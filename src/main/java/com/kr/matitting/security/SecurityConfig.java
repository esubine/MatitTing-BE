package com.kr.matitting.security;

import com.kr.matitting.jwt.filter.JwtAuthenticationFilter;
import com.kr.matitting.oauth2.handler.OAuth2LoginFailureHandler;
import com.kr.matitting.oauth2.handler.OAuth2LoginSuccessHandler;
import com.kr.matitting.oauth2.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity(debug = true)
@RequiredArgsConstructor
public class SecurityConfig {
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(hb -> hb.disable())
                .csrf(cr -> cr.disable());

        http
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http
                .authorizeHttpRequests((request) ->
                        request.requestMatchers("/", "/home").permitAll() //default path
                                .requestMatchers("/member/signupForm", "/oauth2/signUp", "/loginSuccess").permitAll() //login path
                                .requestMatchers("/renew").permitAll() //token path
                                .requestMatchers("/resources/**").permitAll() //resource path
                                .anyRequest().authenticated())
                .formLogin((form) -> form
                        .loginPage("/")
                        .permitAll());

        http
                .oauth2Login(oauth ->
                        oauth
                                .successHandler(oAuth2LoginSuccessHandler)
                                .failureHandler(oAuth2LoginFailureHandler)
                                .userInfoEndpoint(userService -> userService.userService(customOAuth2UserService)));
        http
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


}

