package com.farmaciaoasis.config;

import com.farmaciaoasis.repository.AdministradorRepository;
import com.farmaciaoasis.repository.CajeroRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Inicio de sesión con sesiones. Hay dos tipos de usuario, cada uno en su tabla:
 * administrador (rol ADMINISTRADOR) y cajero (rol CAJERO).
 */
@Configuration
public class SeguridadConfig {

    @Bean
    public SecurityFilterChain filtros(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**").permitAll()
                .requestMatchers("/productos/**", "/lotes/**").hasRole("ADMINISTRADOR")
                .anyRequest().authenticated())
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .permitAll())
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** Busca el correo primero en la tabla administrador y luego en la tabla cajero. */
    @Bean
    public UserDetailsService userDetailsService(AdministradorRepository administradores,
                                                 CajeroRepository cajeros) {
        return correo -> administradores.findByCorreo(correo)
                .map(a -> usuario(a.getCorreo(), a.getContrasenaHash(), a.isActivo(), "ADMINISTRADOR"))
                .or(() -> cajeros.findByCorreo(correo)
                        .map(c -> usuario(c.getCorreo(), c.getContrasenaHash(), c.isActivo(), "CAJERO")))
                .orElseThrow(() -> new UsernameNotFoundException("No existe un usuario con el correo " + correo));
    }

    private static UserDetails usuario(String correo, String contrasenaHash, boolean activo, String rol) {
        return User.withUsername(correo)
                .password(contrasenaHash)
                .disabled(!activo)
                .roles(rol)
                .build();
    }
}
