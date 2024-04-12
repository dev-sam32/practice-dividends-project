package com.dayone.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.util.AntPathMatcher;

@Slf4j
@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // https://www.inflearn.com/questions/741951/websecurityconfigureradapter%EB%A5%BC-%EC%82%AC%EC%9A%A9-%EB%AA%BB%ED%95%98%EB%8A%94-%EA%B2%BD%EC%9A%B0?gad_source=1
    // https://github.com/spring-projects/spring-security/issues/12546
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)  // 이렇게 해야 실행됨;;
                .headers(
                        headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)   //    headers.frameOptions().disable()) <- Deprecate
                )
                // 권한 관련
                .authorizeHttpRequests(
                        req -> req
                                .requestMatchers("/**/signup", "/**/signin").permitAll()
                                .requestMatchers(PathRequest.toH2Console()).permitAll()
//                                .anyRequest().authenticated()
                                .anyRequest().permitAll()
//                                .requestMatchers("").hasAnyAuthority("READ")
                )
                // 세션 관련
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
//                .authenticationProvider(authenticationProvider())
                // jwt 필터
                .addFilterBefore(this.jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

//                .formLogin(Customizer.withDefaults());

        return http.build();
    }
//
//    private AuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
//        daoAuthenticationProvider.setUserDetailsService();
//    }

}
