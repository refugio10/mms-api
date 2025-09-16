package dsi.edoMex.modulomonitoreo.modulomonitoreo.service.config

import dsi.edoMex.modulomonitoreo.modulomonitoreo.config.CustomUserDetails
import dsi.edoMex.modulomonitoreo.saechvv.entity.administracion.Usuario
import dsi.edoMex.modulomonitoreo.saechvv.repository.administracion.UsuarioSaechvvRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

/**
 * Clase que implementa los datos de inicio de sesión proporcionados por el usuario
 *
 * @author lorenav
 * @version 1.0 13/11/2024
 */
@Service
class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UsuarioSaechvvRepository usuarioSaechvvRepository

    /**
     * Consulta el usuario por medio de su username y determina si existe o no el UserDetails
     *
     * @param clave Clave de usuario
     * @return Clase que almacena las credenciales del usuario
     */
    @Override
    UserDetails loadUserByUsername(String clave) throws UsernameNotFoundException {
        Usuario usuario = usuarioSaechvvRepository.findByClave(clave).orElseThrow(() -> new UsernameNotFoundException("El usuario no existe"))

        Set<GrantedAuthority> setAuths = new HashSet<GrantedAuthority>()
        for (def usuarioRole : usuario.usuarioRolesApi)
            setAuths.add(new SimpleGrantedAuthority(usuarioRole.roleApi.nombre))

        CustomUserDetails customUserDetails = new CustomUserDetails()
        customUserDetails.username = usuario.clave
        customUserDetails.password = usuario.password
        customUserDetails.authorities = new ArrayList<GrantedAuthority>(setAuths)

        return customUserDetails
    }
}