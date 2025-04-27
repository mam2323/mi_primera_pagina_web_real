package com.t2kat.annur.config;


import com.t2kat.annur.persistence.model.UsuariosAutorizado;
import com.t2kat.annur.persistence.service.UsuarioAutorizadoService;
import com.t2kat.annur.util.Utilidades;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig implements UserDetailsService{

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests                 //Explicacion abajo de LAMBDA y de parametro request (puedes cambiarlo por otra variable a los dos)
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/login").permitAll()

                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/login")
                        .permitAll()
                )
                .logout(logout -> logout
                .logoutSuccessUrl("/login?logout") // Redirigir después del logout
                .invalidateHttpSession(true)
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET")) // Permitir GET

                        .permitAll()                    // Permitir acceso sin autenticación
        );
        return http.build();
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        System.out.println("userName---- "+username);

        
        // Buscar el usuario en la base de datos
        UsuariosAutorizado usuariosAutorizado = usuarioAutorizadoService.findByUsuario(username);
        System.out.println("usuariosAutorizado.getUsuario()---- "+usuariosAutorizado.getUsuario());
        System.out.println("getPass()----- "+usuariosAutorizado.getPass());

        if (usuariosAutorizado == null) {
            throw new UsernameNotFoundException("Usuario no encontrado");
        }

        return
                User.withDefaultPasswordEncoder()
                        .username(usuariosAutorizado.getUsuario())
                        .password(usuariosAutorizado.getPass())
                        .roles("USER")
                        .build();
    }

    @Bean
    public Utilidades utilidades() {
        return new Utilidades();
    }
    @Autowired
    private UsuarioAutorizadoService usuarioAutorizadoService;
}



                                                                                /** LAMBDA
 El nombre lo eliges tú( reques -> request.

 Es solo el nombre del parámetro que va a recibir el objeto de configuración

 Lo importante es que ese parámetro es de tipo AuthorizationManagerRequestMatcherRegistry(se sobreentiende ***/

                                                            //Forma clásina SIN LAMBDA = objeto  y metodo de configuracion(http.aut..)+ clase anonima + metodo costomize
/*****
 * http.authorizeHttpRequests(
 *     new org.springframework.security.config.Customizer<org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry>() {
 *         @Override
 *         public void customize(org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry requests) {
 *             requests
 *                 .requestMatchers("/css/**", "/js/**", "/images/**", "/login") // Estas rutas se permiten sin login
 *                 .permitAll() // acceso libre
 *                 .anyRequest().authenticated(); // cualquier otra ruta requiere estar autenticado
 *         }
 *     }
 * );
 *
 *
 *
 *
 * Línea	                                Explicación
 *
 * http.authorizeHttpRequests(...)	        Llama a la configuración de seguridad de rutas.
 * new Customizer<...>() { ... }	        Crea una clase anónima que personaliza esa configuración.
 * customize(...)	                        Método que Spring Security llama automáticamente para aplicar las reglas.
 *
 *
 *
 * **/


