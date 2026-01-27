package com.example.personalidad.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login").permitAll()
                .requestMatchers("/usuarios/**").hasRole("ADMIN")
                .requestMatchers("/productos/**", "/ventas/**","/dashboard/**")
                .hasAnyRole("ADMIN", "EMPLEADO")
                .requestMatchers("/ventas/mis-ventas/**").authenticated()
                .requestMatchers("/ventas/reporte/todas").hasRole("ADMIN")
                .requestMatchers("/ventas/reporte/mis-ventas").authenticated()
                .anyRequest().authenticated()
            )

            .formLogin(login -> login
            	    .loginPage("/login")
            	    .usernameParameter("email")   // 🔴 CLAVE
            	    .passwordParameter("password")
            	    .defaultSuccessUrl("/dashboard", true)
            	    .permitAll()
            	)


            // LOGOUT
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .permitAll()
            );

        return http.build();
    }

    // 🔐 ENCRIPTACIÓN REAL
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
