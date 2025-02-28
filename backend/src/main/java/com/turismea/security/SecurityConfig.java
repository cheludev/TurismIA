package com.turismea.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // We don't need CSRF, so we disable it.
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/public/**").permitAll() // Public rules
                        .requestMatchers("/admin/**").hasRole("ADMIN") // Rutas protegidas
                        .anyRequest().authenticated() // Todas las demás requieren autenticación
                )
                .formLogin(login -> login // Configurar login
                        .loginPage("/login")
                        .permitAll()
                )
                .logout(logout -> logout.permitAll()); // Configurar logout

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Encriptación de contraseñas
    }
}
