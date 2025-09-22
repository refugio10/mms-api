package dsi.edoMex.modulomonitoreo.modulomonitoreo.controller.supervisionRemota

import dsi.edoMex.modulomonitoreo.modulomonitoreo.service.utilerias.GeneralService
import dsi.edoMex.modulomonitoreo.saechvv.service.supervisionRemota.ActaSupervisionRemotaService
import dsi.edoMex.modulomonitoreo.saechvv.service.supervisionRemota.SupervisionRemotaService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.OK

/**
 * Controlador en el que se encuentran los endpoints relacionados a las supervisiones remotas
 *
 * @author Refugio Rodríguez Bueno
 * @version 1.0 Julio 2025
 */
@RestController
@RequestMapping('/supervisionRemota')
class SupervisionRemotaController {

    @Autowired
    SupervisionRemotaService supervisionRemotaService

    @Autowired
    GeneralService generalService

    @Autowired
    ActaSupervisionRemotaService actaSupervisionRemota

    /**
     * Genera una supervisión remota aleatoria de un verificentro
     *
     * @return Map información del verificentro seleccionado aleatoriamente
     */
    @PostMapping('/supervisionAleatoriaVerificentro')
    def supervisionAleatoriaVerificentro(){
        return supervisionRemotaService.iniciarSupervisionRemotaVerificentroAleatorio()
    }

    /**
     * Genera una supervisión remota de un verificentro en específico
     * @param parametros Map mapa con los parámetros necesarios para iniciar la supervisión remota
     * @return Map información del verificentro seleccionado
     */
    @PostMapping('/supervisionVerificentroEspecifico')
    def supervisionVerificentroEspecifico(@RequestParam Map parametros){
        return supervisionRemotaService.iniciarSupervisionRemotaVerificentroEspecifico(parametros)
    }

    /**
     * Obtine los datos generales de un verificentro que se está supervisando de forma remota.
     * @param idVerificentro Integer identificador del verificentro del cual se está supervisando
     * @param folioSupervision String folio de la supervisión remota
     * @return Map información del verificentro supervisado
     */
    @GetMapping('/obtenerInformacionVerificentro')
    def obtenerInformacionVerificentro(@RequestParam("idVerificentro") Integer idVerificentro, @RequestParam("folioSupervision") String folioSupervision){

        return supervisionRemotaService.obtenerInformacionSupervisionVerificentro(idVerificentro, folioSupervision)
    }

    /**
     * Genera una supervisión remota aleatoria de una verificación
     *
     * @return Map información de la verificación seleccionada aleatoriamente
     */
    @PostMapping('/supervisionAleatoriaVerificacion')
    def supervisionAleatoriaVerificacion(){
        return supervisionRemotaService.iniciarSupervisionVerificacionAleatoria()
    }

    /**
     * Obtine los datos generales de una verificación que se está supervisando de forma remota.
     * @param idVerificacion Integer identificador de la verificación de la cual se está supervisando
     * @param folioSupervision String folio de la supervisión remota
     * @return Map información de la verificación supervisada
     */
    @GetMapping('/obenerInformacionGeneralVerificacion')
    def obtenerInformacionGeneralVerificacion(@RequestParam("idVerificacion") Integer idVerificacion, @RequestParam("folioSupervision") String folioSupervision){
        return supervisionRemotaService.informacionGeneralVerificacion(idVerificacion, folioSupervision)
    }

    /**
     * Obtiene la información detallada de una verificación que se está supervisando de forma remota.
     * @param idVerificacion Integer identificador de la verificación de la cual se está supervisando
     * @return Map información detallada de la verificación en supervisión
     */
    @GetMapping('/obtenerInformacionDetalladaVerificacion/{idVerificacion}')
    def obtenerInformacionDetalladaVerificacion(@PathVariable("idVerificacion") Integer idVerificacion){
        return supervisionRemotaService.obtenerInformacionDetalladaVerificacion(idVerificacion)

    }

    /**
     * Consulta las verificaciones para supervisar por los parametros proporcionados.
     * @param parametros Map mapa con los parámetros necesarios para la consulta de las verificaciones.
     * @return Map mapa con la respuesta de la consulta de las verificaciones.
     */
    @GetMapping('/consultaVerificaciones')
    def consultaVerificaciones(@RequestParam Map<String, String> parametros){
        return supervisionRemotaService.buscarVerificaciones(parametros)
    }

    /**
     * Consulta las verificaciones para supervisar por los parametros proporcionados.
     * @param parametros Map mapa con los parámetros necesarios para la consulta de las verificaciones.
     * @return Map mapa con la respuesta de la consulta de las verificaciones.
     */
    @GetMapping('/consultaVerificacionesVerificentro')
    def consultaVerificacionesVerificentro(@RequestParam Map<String, String> parametros){
        return supervisionRemotaService.buscarVerificacionesVerificentro(parametros)
    }

    /**
     * Consulta las supervisiones remotas realizadas por los parametros proporcionados.
     * @param parametros Map mapa con los parámetros necesarios para la consulta de las supervisiones remotas.
     * @return Map mapa con la respuesta de la consulta de las supervisiones remotas.
     */
    @GetMapping('/consultaSupervisionesRemotas')
    def consultaSupervisionesRemotas(@RequestParam Map<String, String> parametros){
        return supervisionRemotaService.consultaSupervisionesRemotas(parametros)
    }

    /**
     * Obtiene los catálogos necesarios para registrar una anomalia en una verificación.
     * @param idVerificacion Integer identificador de la verificación para la cual se desean obtener los catálogos de anomalia.
     * @return Map mapa con los catálogos de anomalia nececesarios para el registro de una anomalia.
     */
    @GetMapping('/consultaCatalogosAnomalia')
    def catalogoAnomalias(@RequestParam("idVerificacion") Integer idVerificacion){
        return supervisionRemotaService.consultaInformacionRegistroAnomalia(idVerificacion)
    }

    /**
     * Registra una anomalia en una verificación.
     * @param parametros Mapa con los parámetros necesarios para el registro de la anomalia.
     * @return Map mapa con la respuesta del registro de la anomalía.
     */
    @PostMapping('/registrarAnomalia')
    def registrarAnomalia(@RequestParam Map<String, String> parametros){
        supervisionRemotaService.registrarAnomaliaVerificacion(parametros)
    }

    /**
     * Obtiene la lista de documentos de una verificación en especifíco
     * @param idVerificacion Integer identificador de la verificación
     * @return Map mapa con la respuesta de la consulta de los documentos
     */
    @GetMapping('/getDocumentosVerificacion')
    def getDocumentosVerificacion(@RequestParam("idVerificacion") Integer idVerificacion) {
        return supervisionRemotaService.getDocumentosVerificacion(idVerificacion)
    }

    /**
     * Genera el acta de supervisión remota en formato PDF
     * @param claveAnomalia String clave de la anomalia registrada en la verificación
     * @return Mapa de objetos con el contenido del archivo PDF
     */
    @GetMapping("/getActaSupervisionRemota")
    def getActaSupervisionRemota(@RequestParam("claveAnomalia") String claveAnomalia) {
        def respuesta = generalService.respuestaRequest(OK)

        respuesta.put("tipoArchivo", "application/pdf")
        respuesta.put("arregloBytes", actaSupervisionRemota.generarPdfSupervisionRemota(claveAnomalia).toByteArray())
        respuesta.put("nombreDocumento", "Acta_supervision_" + System.currentTimeMillis() + ".pdf")

        return respuesta
    }

    /**
     * Genera un reporte en formato txt de las supervisiones realizadas a un verificentro
     * @param idVerificentro Integer identificador del verificentro del cual se desean obtener las supervisiones
     * @return Mapa de objetos con el contenido del archivo
     */
    @GetMapping("/getReporteSupervisiones")
    def getReporteSupervisiones(@RequestParam("idVerificentro") Integer idVerificentro) {
        try {
            def respuesta = generalService.respuestaRequest(OK)

            String contenidoReporteTxt = supervisionRemotaService.generarReporteSupervisionesTxt(idVerificentro)

            if (contenidoReporteTxt == null || contenidoReporteTxt.isEmpty()) {
                respuesta = generalService.respuestaRequest(NOT_FOUND)
                respuesta.mensaje = "Ocurrió un problema al generar el reporte de las verificaciones"
                return respuesta
            }

            byte[] archivoBytes = contenidoReporteTxt.getBytes("UTF-8")
            String contendidoArchivo = Base64.getEncoder().encodeToString(archivoBytes)

            respuesta.put("tipoArchivo", "text/plain")
            respuesta.put("bytes", contendidoArchivo)
            respuesta.put("nombreDocumento", "reporte_verificaciones_" + System.currentTimeMillis() + ".txt")

            return respuesta

        } catch (Exception excepcion) {
            excepcion.printStackTrace()
            def respuesta = generalService.respuestaRequest(INTERNAL_SERVER_ERROR)
            respuesta.mensaje = "Ocurrió un problema al generar el reporte de las verificaciones del verificentro"
            return respuesta
        }
    }

    /**
     * Genera una supervisión remota de una verificación en específico
     * @param idVerificacion Integer identificador de la verificación a supervisar
     * @return Map información de la verificación seleccionada
     */
    @PostMapping('/supervisionVerificacionEspecifica/{idVerificacion}')
    def supervisionVerificacionEspecifica(@PathVariable("idVerificacion") Integer idVerificacion){
        return supervisionRemotaService.iniciarSupervisionRemotaVerificacion(idVerificacion)
    }

    /**
     * Obtiene la información de una supervisión remota en específico
     * @param idSupervision Integer identificador de la supervisión remota
     * @return Map información de la supervisión remota
     */
    @GetMapping('/obtenerSupervisionRemota/{idSupervision}')
    def obtenerSupervisonRemota(@PathVariable("idSupervision") Integer idSupervision){

        return supervisionRemotaService.consultaSupervisionRemota(idSupervision)
    }

    /**
     * Cierra una supervisión remota en específico
     * @param parametros Map mapa con los parámetros necesarios para cerrar la supervisión remota
     * @return Map información de la supervisión remota cerrada
     */
    @PutMapping('/cerrarSupervisionRemota')
    def cerrarSupervisionRemota(@RequestParam Map<String, String> parametros){
        return supervisionRemotaService.cerrarSupervisionRemota(parametros)
    }

    /**
     * Consulta las anomalías registradas en una supervisión remota de un verificentro
     * @param idSupervision Integer identificador de la supervisión remota
     * @return Map mapa con la respuesta de la consulta de las anomalías registradas en la supervisión remota
     */
    @GetMapping('/consultaAnomaliasSupervisionVerificentro/{idSupervision}')
    def consultaAnomaliasSupervisionVerificentro(@PathVariable("idSupervision") Integer idSupervision){
        def respuesta = supervisionRemotaService.consultaAnomaliasSupervisionVerificentro(idSupervision)
        println("Respuesta en el constrolador "+respuesta)
        return respuesta
    }

    /**
     * Registra una anomalia en una verificentro.
     * @param datosAnomalia Mapa con los parámetros necesarios para el registro de la anomalia.
     * @return Map mapa con la respuesta del registro de la anomalía.
     */
    @PostMapping('/registrarAnomaliaVerificentro')
    def registrarAnomaliaVerificentro(@RequestParam Map<String, String> datosAnomalia){
        supervisionRemotaService.registrarAnomaliaVerificentro(datosAnomalia)
    }

}