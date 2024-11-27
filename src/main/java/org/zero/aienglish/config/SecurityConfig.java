package org.zero.aienglish.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.zero.aienglish.filter.JwtAuthenticationFilter;
import org.zero.aienglish.utils.OAuth2AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig implements WebMvcConfigurer {
    private final OAuth2AuthenticationSuccessHandler authenticationHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.addFilterAfter(jwtAuthenticationFilter, CsrfFilter.class);
        http
                .authorizeHttpRequests(
                        auth ->
                                auth.requestMatchers("/v1/api/oauth/**").permitAll()
                                        .requestMatchers("/v1/api/login").permitAll()
                                        .requestMatchers("/login").permitAll()
//                                        .anyRequest().permitAll()
                                        .anyRequest().authenticated()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .oauth2Login(oauth ->
                        oauth.authorizationEndpoint(auth -> auth.baseUri("/v1/api/oauth/authorization"))
                                .redirectionEndpoint(auth -> auth.baseUri("/v1/api/login/oauth2/code/*"))
                                .successHandler(authenticationHandler)
                );

        return http.build();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://wp8890.ddns.mksat.net", "http://localhost:5173")  // Разрешите ваш React-приложение
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
