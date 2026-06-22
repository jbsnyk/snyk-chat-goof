package io.snyk.snyklabs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        RequestMatcher csrfProtectedMatcher = new OrRequestMatcher(
            new AntPathRequestMatcher("/api/admin/**")
        );
        
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/admin/**").authenticated()
                .anyRequest().permitAll()
            )
            .csrf(csrf -> csrf
                .requireCsrfProtectionMatcher(csrfProtectedMatcher)
                .csrfTokenRepository(csrfTokenRepository())
            )
            .httpBasic(httpBasic -> {});
        
        return http.build();
    }
    
    private CsrfTokenRepository csrfTokenRepository() {
        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
        repository.setHeaderName("X-CSRF-TOKEN");
        return repository;
    }
}
