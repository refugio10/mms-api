package dsi.edoMex.modulomonitoreo.modulomonitoreo.service.config


import dsi.edoMex.modulomonitoreo.modulomonitoreo.service.utilerias.GeneralService
import dsi.edoMex.modulomonitoreo.saechvv.entity.administracion.Usuario
import dsi.edoMex.modulomonitoreo.saechvv.entity.config.RoleApi
import dsi.edoMex.modulomonitoreo.saechvv.entity.enums.ModuloRequest
import dsi.edoMex.modulomonitoreo.saechvv.repository.administracion.UsuarioSaechvvRepository
import dsi.edoMex.modulomonitoreo.saechvv.service.config.RoleApiService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

import static org.springframework.http.HttpStatus.UNAUTHORIZED
import static org.springframework.http.HttpStatus.OK

/**
 * Contiene las funciones para la autenticación del usuario
 *
 * @author lorenav
 * @version 1.0 13/11/2024
 */
@Service
class AuthService {

    @Autowired
    private TokenService tokenService

    @Autowired
    private GeneralService generalService

    @Autowired
    private UsuarioSaechvvRepository usuarioSaechvvRepository

    @Autowired
    private PasswordEncoder passwordEncoder

    @Autowired
    private RoleApiService roleApiService

    /**
     * Válida las credenciales enviadas del request para dar acceso al sistema
     *
     * @param username Clave del usuario
     * @param password Contraseña del usuario
     * @return Respuesta a la validación de login
     */
    def login(String username, String password) {
        def respuesta = generalService.respuestaRequest(UNAUTHORIZED)
        respuesta.mensaje = "Credenciales incorrectas, verifique la información proporcionada"

        try {
            Usuario usuario = usuarioSaechvvRepository.findByClave(username).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado, verifique la información proporcionada"))

            if (!(usuario.activo in [1, 3])) {
                respuesta.mensaje = "El usuario no se encuentra en un estatus adecuado, verifique con su administrador"
                return respuesta
            }

            if (usuario.password != generalService.encripta(password)) return respuesta

            respuesta = generalService.respuestaRequest(OK)

            def permisosToken = getPermisosToken(usuario)

            respuesta.usuario = [
                    id    : usuario.id,
                    clave : usuario.clave,
                    nombre: usuario.nombre + " " + usuario.apellidoPaterno + " " + usuario.apellidoMaterno,
                    activo: usuario.activo
            ]

            respuesta.putAll(permisosToken)
        } catch (AuthenticationException e) {
            respuesta.mensaje = e.getMessage()
        }

        return respuesta
    }

    /**
     * Obtiene los permisos y los token de la sesión del usuario
     *
     * @param usuario Objeto de clase Usuario de SAECHVV
     * @return Mapa de objetos con los permisos y token de la sesón del usuario
     */
    def getPermisosToken(Usuario usuario) {
        def permisosApi = []
        def esRoleAdmin = usuario?.usuarioRolesApi?.roleApi?.nombre?.contains("ROLE_ADMIN_MONITOREO")
        String nombreRoles = esRoleAdmin ? 'ROLE_ADMIN_MONITOREO' : 'ROLE_LOGIN'

        if (!esRoleAdmin) {
            def rolesApi = roleApiService.consulta([usuario: usuario.id, activo: 1, moduloRequest: ModuloRequest.MODULO_MONITOREO_API.id])
            for (RoleApi roleApi : rolesApi) permisosApi.addAll(roleApi?.permisosApi?.url)
            if (rolesApi != null && !rolesApi.empty)
                nombreRoles = rolesApi?.nombre?.toString()
                        ?.replace("[", "")
                        ?.replace("]", "")
                        ?.replace(" ", "")
        }

        String token = tokenService.generateJwt(usuario.clave, nombreRoles)
        String refreshToken = tokenService.createRefreshToken(usuario.clave, nombreRoles)

        return [token: token, refreshToken: refreshToken, permisosApi: permisosApi, nombreRoles: nombreRoles]
    }
}