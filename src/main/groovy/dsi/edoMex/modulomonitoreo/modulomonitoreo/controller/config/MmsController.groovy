package dsi.edoMex.modulomonitoreo.modulomonitoreo.controller.config


import dsi.edoMex.modulomonitoreo.modulomonitoreo.service.utilerias.GeneralService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

import static org.springframework.http.HttpStatus.OK
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR

/**
 * Contiene las funciones inicio del api
 *
 * @author lorenav
 * @version 1.0 13/11/2024
 */
@RestController
class MmsController {

    @Autowired
    private GeneralService generalService

    /**
     * Consulta el estatus del proyecto mostrando la hora del servidor y el horario de base de datos
     *
     * @return Objeto de clase Map con el estatus del servidor
     */
    @GetMapping("/")
    @Secured("permitAll")
    def index() {
        def respuesta = generalService.respuestaRequest(OK)
        respuesta.estatus = "Los servicios de MMS-API se encuentran funcionando correctamente.\n Version: %{{version}}"
        //respuesta.horaBaseDatos = "Hora base de datos: " + generalService.generalRepository.obtenerHoraBaseDatos()
        respuesta.horaServidor = "Hora servidor: " +   Calendar.getInstance().getTime()
        return respuesta
    }

    /**
     * Responde un mensaje de error cuando exista un problema de codificación en la api
     *
     * @return Objeto de clase Map con el estatus del servidor
     */
    @GetMapping("/error")
    @Secured("permitAll")
    def error() {
        def respuesta = generalService.respuestaRequest(INTERNAL_SERVER_ERROR)
        respuesta.mensaje = "Solicita la revisión del LOG con el área de desarrollo, ocurrió un error en el servidor."
        return respuesta
    }
}