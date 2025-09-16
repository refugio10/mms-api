package dsi.edoMex.modulomonitoreo.modulomonitoreo.config

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

/**
 * Implementa la clase de UserDetails de SpringSecurity
 *
 * @author lorenav
 * @version 1.0 28/11/2024
 */
class CustomUserDetails implements UserDetails {
    String username
    String password
    Collection<? extends GrantedAuthority> authorities

    /**
     * Obtiene los roles de un usuario de sesión
     * @return Colección de roles
     */
    @Override
    Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities
    }

    /**
     * Obtiene la contraseña del usuario de sesión
     *
     * @return Obtiene la contraseña del usuario
     */
    @Override
    String getPassword() {
        return this.password
    }

    /**
     * Obtiene el username del usuario de sesión
     * @return Obtiene la clave de usuario
     */
    @Override
    String getUsername() {
        return this.username
    }
}