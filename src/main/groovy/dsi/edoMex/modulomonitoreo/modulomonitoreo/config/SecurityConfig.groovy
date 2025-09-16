package dsi.edoMex.modulomonitoreo.modulomonitoreo.config

import com.nimbusds.jose.jwk.JWK
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext

import dsi.edoMex.modulomonitoreo.modulomonitoreo.config.jwt.AuthFilter
import dsi.edoMex.modulomonitoreo.modulomonitoreo.config.jwt.PocClosureVoter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

/**
 * Configuración de spring security para las solicitudes recibidas en el api
 *
 * @author lorenav
 * @version 1.0 13/11/2024
 */
@Configuration
class SecurityConfig {

    private RSAKeyProperties keys
    private RequestSecured requestSecured

    /**
     * Constructor que inicializa las variables de clase
     *
     * @param keys Componente con llaves RSA
     */
    SecurityConfig(RSAKeyProperties keys, RequestSecured requestSecured) {
        this.keys = keys
        this.requestSecured = requestSecured
    }

    /**
     * Proporciona el recurso para la autenticación de accesos
     *
     * @return Clase que determina si la solicititud es autenticado para permitir el acceso a algún recurso
     */
    @Bean
    PocClosureVoter autorizationManager() {
        return new PocClosureVoter(requestSecured)
    }

    /**
     * Proporciona el recurso para la autenticación de un token por medio de filtros
     *
     * @return Clase que filtra las solicitudes HTTP el cual solo se invocará una sola vez por solicitud
     */
    @Bean
    AuthFilter authenticationJwtTokenFilter() {
        return new AuthFilter()
    }

    /**
     * Proporciona el recurso para codificar contraseñas
     *
     * @return Clase que permite codificar contraseñas
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder()
    }

    /**
     * Filtro de solicitudes con políticas específicas para el uso del recurso de la api
     *
     * @param http Clase para configurar las solicirtudes HTTP
     * @return Clase que protege el recurso de la aplicación
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/", "/auth/**", "/error").permitAll()
                    auth.anyRequest().access(autorizationManager())
                })
                .oauth2ResourceServer(oauth2 -> {
                    oauth2.jwt(jwt -> {
                        jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())
                    })
                })
                .addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> {
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                })
        return http.build()
    }

    /**
     * Proporciona el recurso para decodificar la llave pública
     *
     * @return Clase responsable de decodificar un json web token
     */
    @Bean
    JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(this.keys.getPublicKey()).build()
    }

    /**
     * Proporciona el recurso para codificar las llaves de RSA para generar el json web token
     *
     * @return Clase responsable de codificar un json web token
     */
    @Bean
    JwtEncoder jwtEncoder() {
        JWK jwk = new RSAKey.Builder(keys.getPublicKey()).privateKey(keys.getPrivateKey()).build()
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk))
        return new NimbusJwtEncoder(jwks)
    }

    /**
     * Configura un conversor de Authorities, para establecerlos en el contexto de autenticación
     *
     * @return Clase que establece los roles de autenticación
     */
    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter()
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("roles")
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("")
        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter()
        jwtConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter)
        return jwtConverter
    }
}