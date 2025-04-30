package com.turismea.security;

import com.turismea.model.enumerations.Role;
import com.turismea.model.entity.User;
import com.turismea.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    private final UserRepository userRepository;

    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // We don't need CSRF, so we disable it.
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/public/**").permitAll() // Public rules
                        .requestMatchers("/admin/**").hasRole("ADMIN") // Protected Routes
                        .requestMatchers("/tourist/**").hasAnyRole("TOURIST", "ADMIN", "MODERATOR")
                        .requestMatchers("/moderator/**").hasAnyRole( "ADMIN", "MODERATOR")
                        .requestMatchers("/moderator/**").hasRole("ADMIN")
                        .anyRequest().authenticated() // All others require authentication
                )
                .formLogin(login -> login // Login configuration
                        .loginPage("/login")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                ); // Configurar logout

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Encriptación de contraseñas
    }

    public Role getRoleFromUser(Long id) {
        User user = userRepository.findById(id).orElse(null);
        return user == null? null : user.getRole();
    }
}
