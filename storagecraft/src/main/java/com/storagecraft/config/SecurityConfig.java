package com.storagecraft.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // 1. Acceso público (visitantes sin sesión)
                .requestMatchers("/", "/items", "/items/consultar", "/reportes/**", "/css/**", "/js/**", "/img/**").permitAll()
                
                // 2. Acceso restringido (requiere autenticación y roles)
                .requestMatchers("/items/guardar", "/items/eliminar/**").hasAnyRole("OPERADOR", "JUGADOR")
                .requestMatchers("/dispositivos/**", "/transacciones/**").hasAnyRole("OPERADOR", "JUGADOR")
                
                // Cualquier otra ruta exige inicio de sesión
                .anyRequest().authenticated()
            )
            .formLogin(login -> login
                .defaultSuccessUrl("/items", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/items")
                .permitAll()
            );

        return http.build();
    }

    // Autenticación en memoria: estable, rápida e independiente de la latencia externa
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails operador = User.withDefaultPasswordEncoder()
                .username("Steve")
                .password("admin123")
                .roles("OPERADOR")
                .build();

        UserDetails jugador = User.withDefaultPasswordEncoder()
                .username("Alex")
                .password("user123")
                .roles("JUGADOR")
                .build();

        return new InMemoryUserDetailsManager(operador, jugador);
    }
}
