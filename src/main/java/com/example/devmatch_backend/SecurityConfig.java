package com.example.devmatch_backend;

import com.example.devmatch_backend.domain.user.handler.OAuth2AuthenticationFailureHandler;
import com.example.devmatch_backend.domain.user.handler.OAuth2AuthenticationSuccessHandler;
import com.example.devmatch_backend.domain.user.jwt.*;
import com.example.devmatch_backend.domain.user.repository.UserRepository;
import com.example.devmatch_backend.domain.user.service.CustomOAuth2UserService;
import com.example.devmatch_backend.exception.GlobalExceptionHandlerFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final AuthRepository authRepository;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                // disable session
                .sessionManagement((sessionManagement) ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling((handling) ->
                        handling.authenticationEntryPoint(jwtAuthenticationEntryPoint)
                                .accessDeniedHandler(jwtAccessDeniedHandler)
                )

                .headers((header) ->
                        header.frameOptions(
                                HeadersConfigurer.FrameOptionsConfig::sameOrigin
                        )
                )

                .authorizeHttpRequests((registry) ->
                        registry
                                .requestMatchers("/login/**").permitAll()
                                .requestMatchers("/oauth2/**").permitAll()
                                .requestMatchers("/error/**").permitAll()
                                .requestMatchers("/favicon.ico").permitAll()
                                .anyRequest().authenticated()
                )
                .oauth2Login(oauth2Configurer -> oauth2Configurer
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService))
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .failureHandler(oAuth2AuthenticationFailureHandler))
//                .userInfoEndpoint().userService(customOAuth2UserService)
                .addFilterBefore(new JwtFilter(tokenProvider, userRepository), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new RefreshFilter(tokenProvider, authRepository), JwtFilter.class) // JwtFilter 다음에 RefreshFilter를 추가
                .addFilterBefore(new GlobalExceptionHandlerFilter(), RefreshFilter.class);


        ;
        return httpSecurity.build();
    }


    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("HEAD", "POST", "GET", "DELETE", "PUT"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }



    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}