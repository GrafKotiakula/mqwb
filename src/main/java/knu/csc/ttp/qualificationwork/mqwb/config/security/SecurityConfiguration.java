package knu.csc.ttp.qualificationwork.mqwb.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfiguration {
    private final String[] jwtIgnoreAntMatchers = {"/auth/login", "/auth/signup"};
    private final JwtFilter jwtFilter;
    private final AnonymousFilter anonymousFilter;

    @Autowired
    public SecurityConfiguration(JwtProvider provider, ObjectMapper objMapper) {
        this.jwtFilter = new JwtFilter(provider, objMapper, jwtIgnoreAntMatchers);
        this.anonymousFilter = new AnonymousFilter(HttpMethod.GET);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(jwtIgnoreAntMatchers).permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(anonymousFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFilter, AnonymousFilter.class);
        return http.build();
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public static AuthenticationManager authManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
