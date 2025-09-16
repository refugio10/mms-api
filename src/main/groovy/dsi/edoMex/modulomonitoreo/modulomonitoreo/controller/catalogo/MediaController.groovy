package dsi.edoMex.modulomonitoreo.modulomonitoreo.controller.catalogo

import dsi.edoMex.modulomonitoreo.modulomonitoreo.service.utilerias.GeneralService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

import static org.springframework.http.HttpStatus.BAD_REQUEST
import static org.springframework.http.HttpStatus.OK

/**
 * Contiene funciones para archivos multimedia
 *
 * @author lorenav
 * @version 1.0 18/12/2024
 */
@RestController
@RequestMapping("/media")
class MediaController {

    @Autowired
    private GeneralService generalService

    /**
     * Permite generar un archivo de formal temporal en el servidor
     * @param archivoAdjunto Objeto de clase MultipartFile con el contenido del archivo adjunto
     * @return Objeto de clase Map con el resultado del almacenamiento del documento
     */
    @PostMapping("/adjuntarDocumentoTemporal")
    @Secured("isAuthenticated()")
    def adjuntarDocumentoTemporal(@RequestPart("archivoAdjunto") MultipartFile archivoAdjunto) {
        def archivo = generalService.adjuntarDocumentoTemporal(archivoAdjunto)
        if (archivo == null) {
            def respuesta = generalService.respuestaRequest(BAD_REQUEST)
            respuesta.mensaje = "Ocurrió un error al importar el archivo, solicita la revisión del servidor"
            return respuesta
        }

        def respuesta = generalService.respuestaRequest(OK)
        respuesta.archivo = archivo
        return respuesta
    }

    /**
     * Permite obtener un archivo del servidor
     * @param parametros Proporciona la información contenida en los parámetros de las solicitudes HTTP
     * @return Objeto de clase Map el archivo
     */
    @GetMapping("/getDocumento")
    @Secured("isAuthenticated()")
    def getDocumento(@RequestParam Map<String, String> parametros) {
        def archivo = generalService.getArchivo(parametros?.ruta)
        if (archivo == null) {
            def respuesta = generalService.respuestaRequest(BAD_REQUEST)
            respuesta.mensaje = "No se encontró el archivo solicitado"
            return respuesta
        }

        def respuesta = generalService.respuestaRequest(OK)
        respuesta.archivo = archivo
        return respuesta
    }
}