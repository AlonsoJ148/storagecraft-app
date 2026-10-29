package com.storagecraft.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final DataSource dataSource;

    public SecurityConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // 1. Acceso público (visitantes sin sesión)
                .requestMatchers("/", "/items", "/items/consultar", "/reportes/**", "/css/**", "/js/**", "/img/**").permitAll()
                
                // 2. Acceso restringido (requiere autenticación contra la BD)
                .requestMatchers("/items/guardar", "/items/eliminar/**").hasAnyAuthority("ROLE_OPERADOR", "ROLE_JUGADOR")
                .requestMatchers("/dispositivos/**", "/transacciones/**").hasAnyAuthority("ROLE_OPERADOR", "ROLE_JUGADOR")
                
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

    // Consulta de credenciales y roles directamente a la tabla JUGADORES en Aiven
    @Bean
    public UserDetailsService userDetailsService() {
        JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);
        
        // Consulta 1: usuario, contraseña y estado activo (1)
        manager.setUsersByUsernameQuery(
            "SELECT nombre_usuario, password, 1 as enabled FROM JUGADORES WHERE nombre_usuario = ?"
        );
        
        // Consulta 2: usuario y su rol/autoridad asignada
        manager.setAuthoritiesByUsernameQuery(
            "SELECT nombre_usuario, rango_acceso FROM JUGADORES WHERE nombre_usuario = ?"
        );
        
        return manager;
    }
}