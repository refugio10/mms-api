package dsi.edoMex.modulomonitoreo.modulomonitoreo.controller.config

import dsi.edoMex.modulomonitoreo.modulomonitoreo.service.config.AuthService
import dsi.edoMex.modulomonitoreo.modulomonitoreo.service.utilerias.GeneralService
import dsi.edoMex.modulomonitoreo.saechvv.entity.administracion.Usuario
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Contiene las funciones de validación de credenciales para el uso del sistema
 *
 * @author lorenav
 * @version 1.0 13/11/2024
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
class AuthController {

    @Autowired
    private GeneralService generalService

    @Autowired
    private AuthService authService

    /**
     * Valida las credenciales enviadas para una autorización de uso de los enpoints del sistema
     *
     * @param parametros Parámetros [username, password] para la validación de inicio de sesión
     * @return Respuesta a la validación de login
     */
    @PostMapping("/login")
    @Secured("permitAll")
    def login(@RequestBody Map<String, String> parametros) {
        return authService.login(parametros?.username, parametros?.password)
    }

    /**
     * Genera un nuevo token y refreshToken para cuando el tiempo de vida de token haya acabado
     *
     * @return Mapa con el token y refresh token para el usuario
     */
    @GetMapping("/refreshToken")
    @Secured("isAuthenticated()")
    def refreshToken() {
        Usuario usuario = generalService.getUsuarioSession()
        def permisoToken = authService.getPermisosToken(usuario)
        return [token: permisoToken.token, refreshToken: permisoToken.refreshToken]
    }
}