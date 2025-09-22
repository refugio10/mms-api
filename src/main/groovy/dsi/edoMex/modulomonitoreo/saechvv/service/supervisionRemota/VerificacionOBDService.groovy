package dsi.edoMex.modulomonitoreo.saechvv.service.supervisionRemota

import org.springframework.stereotype.Service

/**
 * Servicio con los métodos para procesar los resultados de las pruebas OBD y convertirlos
 * en información legible.
 * @author Refugio Rodríguez Bueno
 * @version 1.0 Agosto 2025
 */
@Service
class VerificacionOBDService {

    /**
     * Procesa los datos brutos de una prueba OBD y los convierte en un formato estructurado
     * con descripciones legibles.
     *
     * @param datosPruebaOBD Map Mapa que contiene los datos crudos de la prueba OBD.
     * @param idTipoHolograma Integer Identificador del tipo de holograma obtenido en la verificación
     * @return Map Mapa con los datos procesados y descripciones de cada valor.
     */
    def procesarRespuestaPruebaOBD(Map datosPruebaOBD, Integer idTipoHolograma){
        try{
            Integer resultado = datosPruebaOBD.get("RESULTADO") as Integer

            String descripcionResultado

            if (resultado == 1) {
                descripcionResultado = "Aprobado"
            } else {
                descripcionResultado = (idTipoHolograma == 12) ? "Informe SDB" : "Rechazado"
            }
            def respuesta = [:]
            respuesta.idVerificacionOBD = datosPruebaOBD.get("IDVERIFICACIONOBD")
            respuesta.idVerificacion = datosPruebaOBD.get("IDVERIFICACION")
            respuesta.folio = datosPruebaOBD.get("FOLIO")
            respuesta.fecha = datosPruebaOBD.get("FECHA")
            respuesta.idUsuario = datosPruebaOBD.get("IDUSUARIO")
            respuesta.mil = (datosPruebaOBD.get("MIL") as Integer) == 0 ? "Apagada" : "Encendida"
            respuesta.dtc = datosPruebaOBD.get("DTC")
            respuesta.falloEncendido = obtenerEstadoMonitor(datosPruebaOBD.get("FALLO_ENCENDIDO") as Integer, resultado)
            respuesta.sistemaCombustible = obtenerEstadoMonitor(datosPruebaOBD.get("SISTEMA_COMBUSTIBLE") as Integer, resultado)
            respuesta.componentes = obtenerEstadoMonitor(datosPruebaOBD.get("COMPONENTES") as Integer, resultado)
            respuesta.catalizador = obtenerEstadoMonitor(datosPruebaOBD.get("CATALIZADOR") as Integer, resultado)
            respuesta.catalizadorSC = obtenerEstadoMonitor(datosPruebaOBD.get("CATALIZADOR_SC") as Integer, resultado)
            respuesta.sistemaEvaporativo = obtenerEstadoMonitor(datosPruebaOBD.get("SISTEMA_EVAPORATIVO") as Integer, resultado)
            respuesta.sistemaSecundario = obtenerEstadoMonitor(datosPruebaOBD.get("SISTEMA_SECUNDARIO") as Integer, resultado)
            respuesta.refrigeranteAC = obtenerEstadoMonitor(datosPruebaOBD.get("REFRIGERANTE_AC") as Integer, resultado)
            respuesta.sensorOxigeno = obtenerEstadoMonitor(datosPruebaOBD.get("SENSOR_OXIGENO") as Integer, resultado)
            respuesta.sensorOxigenoSC = obtenerEstadoMonitor(datosPruebaOBD.get("SENSOR_OXIGENO_SC") as Integer, resultado)
            respuesta.sistemaEGR = obtenerEstadoMonitor(datosPruebaOBD.get("SISTEMA_EGR") as Integer, resultado)
            respuesta.resultado = resultado
            respuesta.descripcionResultado = descripcionResultado
            respuesta.estatus = datosPruebaOBD.get("ESTATUS")
            respuesta.tipoOBD = datosPruebaOBD.get("TIPO_OBD")
            respuesta.fechaImpresion = datosPruebaOBD.get("FECHA_IMPRESION")
            respuesta.serie = datosPruebaOBD.get("SERIE")
            respuesta.resultadoOBD = obtenerResultadoOBD(datosPruebaOBD.get("RESULTADO") as Integer)

            return respuesta
        }catch (Exception excepcion){
            excepcion.printStackTrace()
            return [:]
        }
    }

    /**
     * Convierte el código numérico de resultado de la prueba OBD en su descripción correspondiente.
     *
     * @param resultado Integer el código numérico del resultado de la prueba OBD.
     * @return String descripción del resultado de la prueba OBD. Retorna "N/A" si el código
     * no coincide con los valores conocidos.
     */
    static String obtenerResultadoOBD(Integer resultado){

        switch(resultado){
            case -2:
                return "Aprobado"
                break
            case -1:
                return "ECU no catalogada"
                break
            case 0:
                return "Sin conectividad"
                break
            case 1:
                return "Aprobado"
                break;
            case 2:
                return "Monitores No Listos"
                break
            case 3:
                return "DTC encontrados"
                break
            case 4:
                return "Monitores no soportados"
                break
            case 5:
                return "Mil encendida";
                break
            default:
                return "N/A"
                break
        }
    }

    /**
     * Determina el estado de un monitor específico del sistema OBD basado en su valor numérico
     * y el resultado general de la verificación.
     *
     * @param estadoMonitor código numérico que representa el estado del monitor
     * @param resultadoVerificacionObd Integer resultado general de la verificación OBD que afecta
     * la interpretación del estado del monitor
     * @return String descripción del estado del monitor. Puede ser:
     * - "No soportado", "N/A", "Listo" o "No listo"
     */
    static String obtenerEstadoMonitor(Integer estadoMonitor, Integer resultadoVerificacionObd){

        String monitorNoSoportado = (resultadoVerificacionObd == 0) ? "N/A" : "No soportado"

        switch(estadoMonitor) {
            case 0: return monitorNoSoportado
                break
            case 1: return  "Listo"
                break
            default: return "No listo"
                break
        }
    }
}