//package dsi.edoMex.modulomonitoreo.modulomonitoreo.controller.administracion
//
//import dsi.edoMex.modulomonitoreo.saechvv.repository.administracion.PagedList
//import dsi.edoMex.modulomonitoreo.modulomonitoreo.service.administracion.UsuarioMmsService
//import dsi.edoMex.modulomonitoreo.modulomonitoreo.service.utilerias.GeneralService
//import dsi.edoMex.modulomonitoreo.saechvv.entity.administracion.Usuario
//import dsi.edoMex.modulomonitoreo.saechvv.service.administracion.UsuarioSaechvvService
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.security.access.annotation.Secured
//import org.springframework.security.crypto.password.PasswordEncoder
//import org.springframework.web.bind.annotation.GetMapping
//import org.springframework.web.bind.annotation.PathVariable
//import org.springframework.web.bind.annotation.PostMapping
//import org.springframework.web.bind.annotation.PutMapping
//import org.springframework.web.bind.annotation.RequestBody
//import org.springframework.web.bind.annotation.RequestMapping
//import org.springframework.web.bind.annotation.RequestParam
//import org.springframework.web.bind.annotation.RestController
//
//import static org.springframework.http.HttpStatus.BAD_REQUEST
//import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
//import static org.springframework.http.HttpStatus.OK
//
///**
// * Contiene funciones para la administración de Usuarios de los diversas aplicaciones de SAECHVV
// *
// * @author lorenav
// * @version 1.0 14/11/2024
// */
//@RestController
//@RequestMapping("/usuario")
//class UsuarioController {
//    @Autowired
//    private GeneralService generalService
//
//    @Autowired
//    private UsuarioSaechvvService usuarioSaechvvService
//
//    @Autowired
//    PasswordEncoder passwordEncoder
//
//    @Autowired
//    UsuarioMmsService usuarioMmsService
//
//    /**
//     * Consulta los registros de Usuario
//     *
//     * @param parametros Proporciona la información contenida en los parámetros de las solicitudes HTTP
//     * @return Lista de objetos de clase Usuario
//     */
//    @GetMapping("/index")
//    def index(@RequestParam Map<String, String> parametros) {
//        def respuesta = generalService.respuestaRequest(OK)
//        PagedList<Usuario> pageListUsuario = usuarioSaechvvService.consulta(parametros)
//        respuesta.usuarios = pageListUsuario
//        respuesta.totalCount = pageListUsuario.totalCount
//        return respuesta
//    }
//
//    /**
//     * Obtiene un registro específico de usuario
//     *
//     * @return Objeto de clase Map con la consulta individual de Usuario
//     */
//    @GetMapping("/visualizar/{id}")
//    def visualizar(@PathVariable("id") Integer id) {
//        def respuesta = generalService.respuestaRequest(OK)
//        respuesta.putAll(usuarioSaechvvService.getUsuario(id))
//        return respuesta
//    }
//
//    /**
//     * Valida y guarda los datos de un usuario en las distintas bases de datos
//     *
//     * @param parametros Proporciona la información contenida en el body de las solicitudes HTTP
//     * @return Objeto de clase Map con el resultado del registro de Usuario
//     */
//    @PostMapping("/guardar")
//    def guardar(@RequestBody Map<String, Object> parametros) {
//        String contrasenaTemporal = usuarioSaechvvService.generaPassword()
//        parametros.esActualizacion = false
//        parametros.contrasenaTemporal = contrasenaTemporal
//        parametros.contrasenaEncode = generalService.encripta(contrasenaTemporal)
//        return usuarioMmsService.guardar(parametros)
//    }
//
//    /**
//     * Actualiza la información contenida en la tabla de usuario en las distintas bases de datos
//     *
//     * @param parametros Proporciona la información contenida en el body de las solicitudes HTTP
//     * @return Objeto de clase Map con el resultado de la actualización de Usuario
//     */
//    @PutMapping("/actualizar")
//    def actualizar(@RequestBody Map<String, Object> parametros) {
//        parametros.esActualizacion = true
//        String contrasenaTemporal = usuarioSaechvvService.generaPassword()
//        parametros.contrasenaTemporal = contrasenaTemporal
//        return usuarioMmsService.guardar(parametros)
//    }
//
//    /**
//     * Modificación de estatus de un registro de usuario
//     *
//     * @param id Identificador del registro de Usuario
//     * @return Objeto de clase Map con el resultado de la actualización de Usuario
//     */
//    @PutMapping("/cambiarEstatus")
//    def cambiarEstatus(@RequestBody Map<String, Object> parametros) {
//        return usuarioMmsService.cambiarEstatus(parametros)
//    }
//
//    /**
//     * Actualiza la contraseña del usuario por uno establecido por sistema
//     * y cambia el estatus = 3 (activo, pendiente de cambio de contraseña)
//     *
//     * @param parametros Proporciona la información contenida en el body de las solicitudes HTTP
//     * @return Objeto de clase Map con el resultado de la actualización de Usuario
//     */
//    @PutMapping("/restablecerContrasena")
//    def restablecerContrasena(@RequestBody Map<String, Object> parametros) {
//        try {
//            def resultado = usuarioSaechvvService.nuevaContrasena(parametros)
//
//            if (resultado.usuario == null) {
//                def respuesta = generalService.respuestaRequest(BAD_REQUEST)
//                respuesta.mensaje = resultado.mensaje
//                return respuesta
//            }
//
//            resultado.password = parametros.password
//            return usuarioMmsService.actualizarContrasenas(resultado.usuario as Usuario, resultado)
//        } catch (Exception e) {
//            e.printStackTrace()
//
//            def respuesta = generalService.respuestaRequest(INTERNAL_SERVER_ERROR)
//            respuesta.mensaje = "Ocurrió un error al actualizar la contraseña, comunícate con el área de soporte técnico y reporta el error"
//            return respuesta
//        }
//    }
//
//    /**
//     * Actualiza la contraseña del usuario y cambia el estatus = 1 (activo)
//     *
//     * @param parametros Proporciona la información contenida en el body de las solicitudes HTTP
//     * @return Objeto de clase Map con el resultado de la actualización de Usuario
//     */
//    @PutMapping("/actualizarContrasena")
//    @Secured("isAuthenticated()")
//    def actualizarContrasena(@RequestBody Map<String, Object> parametros) {
//        try {
//            def resultado = usuarioSaechvvService.actualizarContrasena(parametros)
//
//            if (resultado.usuario == null) {
//                def respuesta = generalService.respuestaRequest(BAD_REQUEST)
//                respuesta.mensaje = resultado.mensaje
//                return respuesta
//            }
//
//            return usuarioMmsService.actualizarContrasenas(resultado.usuario as Usuario, resultado)
//        } catch (Exception e) {
//            e.printStackTrace()
//
//            def respuesta = generalService.respuestaRequest(INTERNAL_SERVER_ERROR)
//            respuesta.mensaje = "Ocurrió un error al actualizar la contraseña, comunícate con el área de soporte técnico y reporta el error"
//            return respuesta
//        }
//    }
//
//    /**
//     * Obtiene el usuario que se encuentra en sesión
//     *
//     * @return Objeto de clase usuario de SAECHVV
//     */
//    @GetMapping("/me")
//    @Secured("isAuthenticated()")
//    def me() {
//        def respuesta = generalService.respuestaRequest(OK)
//        respuesta.usuario = generalService.usuarioSession
//        return respuesta
//    }
//
//    /**
//     * Genera el archivo PDF del usuario que fue registrado o actualizado
//     * @param parametros Proporciona la información contenida en el body de las solicitudes HTTP
//     * @return Mapa de objetos con el contenido del archivo
//     */
//    @GetMapping("/getArchivoPdf")
//    @Secured("isAuthenticated()")
//    def getArchivoPdf(@RequestParam Map<String, String> parametros) {
//        def respuesta = generalService.respuestaRequest(OK)
//        respuesta.put("tipoArchivo", "application/pdf")
//        respuesta.put("bytes", usuarioMmsService.generaPdf(parametros).toByteArray())
//        respuesta.put("nombreDocumento", "Usuario_" + System.currentTimeMillis() + ".pdf")
//        return respuesta
//    }
//}