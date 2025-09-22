package dsi.edoMex.modulomonitoreo.saechvv.service.supervisionRemota

import dsi.edoMex.modulomonitoreo.modulomonitoreo.service.utilerias.GeneralService
import dsi.edoMex.modulomonitoreo.saechvv.entity.administracion.Usuario
import dsi.edoMex.modulomonitoreo.saechvv.entity.catalogo.MotivoAnomalia
import dsi.edoMex.modulomonitoreo.saechvv.entity.catalogo.Verificacion
import dsi.edoMex.modulomonitoreo.saechvv.entity.catalogo.VerificacionAuxiliar
import dsi.edoMex.modulomonitoreo.saechvv.entity.catalogo.Verificentro
import dsi.edoMex.modulomonitoreo.saechvv.entity.enums.EstatusVerificacion
import dsi.edoMex.modulomonitoreo.saechvv.entity.supervisionRemota.AnomaliaMesaControl
import dsi.edoMex.modulomonitoreo.saechvv.entity.supervisionRemota.AnomaliaSupervisionRemota
import dsi.edoMex.modulomonitoreo.saechvv.entity.supervisionRemota.MesaControl
import dsi.edoMex.modulomonitoreo.saechvv.entity.supervisionRemota.SupervisionRemota

import dsi.edoMex.modulomonitoreo.saechvv.repository.administracion.Criteria
import dsi.edoMex.modulomonitoreo.saechvv.repository.administracion.UsuarioSaechvvRepository
import dsi.edoMex.modulomonitoreo.saechvv.repository.catalogo.CalleRepository
import dsi.edoMex.modulomonitoreo.saechvv.repository.catalogo.CamaraVerificentroRepository
import dsi.edoMex.modulomonitoreo.saechvv.repository.catalogo.ColoniaRepository
import dsi.edoMex.modulomonitoreo.saechvv.repository.catalogo.EntidadRepository
import dsi.edoMex.modulomonitoreo.saechvv.repository.catalogo.LineaVerificacionRepository
import dsi.edoMex.modulomonitoreo.saechvv.repository.catalogo.MunicipioRepository
import dsi.edoMex.modulomonitoreo.saechvv.repository.catalogo.VerificacionRepository
import dsi.edoMex.modulomonitoreo.saechvv.repository.catalogo.VerificentroRepository
import dsi.edoMex.modulomonitoreo.saechvv.repository.supervisionRemota.AnomaliaMesaControlRepository
import dsi.edoMex.modulomonitoreo.saechvv.repository.supervisionRemota.AnomaliaSupervisionRemotaRepository
import dsi.edoMex.modulomonitoreo.saechvv.repository.supervisionRemota.BitacoraAprRepository
import dsi.edoMex.modulomonitoreo.saechvv.repository.supervisionRemota.IncidenciaOCRRepository
import dsi.edoMex.modulomonitoreo.saechvv.repository.supervisionRemota.MesaControlRepository
import dsi.edoMex.modulomonitoreo.saechvv.repository.supervisionRemota.MotivoAnomaliaRepository
import dsi.edoMex.modulomonitoreo.saechvv.repository.supervisionRemota.SupervisionRemotaRepository
import dsi.edoMex.modulomonitoreo.saechvv.repository.supervisionRemota.SupervisionVerificacionCentroRepository
import dsi.edoMex.modulomonitoreo.saechvv.service.catalogo.LineaVerificacionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.text.ParseException
import java.text.SimpleDateFormat

import static org.springframework.http.HttpStatus.*

/**
 * Contiene funciones necesarias para supervisiones remotas aletorias por verificación y verificentro.
 *
 * @author Refugio Rodríguez Bueno
 * @version 1.0 Julio 2025
 */
@Service
class SupervisionRemotaService {

    @Autowired
    private GeneralService generalService

    @Autowired
    private LineaVerificacionRepository lineaVerificacionRepository

    @Autowired
    private CamaraVerificentroRepository camaraVerificentroRepository

    @Autowired
    private VerificentroRepository verificentroRepository

    @Autowired
    private Criteria criteria

    @Autowired
    private ColoniaRepository coloniaRepository

    @Autowired
    private MunicipioRepository municipioRepository

    @Autowired
    private EntidadRepository entidadRepository

    @Autowired
    private CalleRepository calleRepository

    @Autowired
    private LineaVerificacionService lineaVerificacionService

    @Autowired
    SupervisionRemotaRepository supervisionRemotaRepository

    @Autowired
    MesaControlRepository mesaControlRepository

    @Autowired
    VerificacionRepository verificacionRepository

    @Autowired
    MotivoAnomaliaRepository motivoAnomaliaRepository

    @Autowired
    AnomaliaMesaControlRepository anomaliaMesaControlRepository

    @Autowired
    AnomaliaSupervisionRemotaRepository anomaliaSupervisionRemotaRepository

    @Autowired
    IncidenciaOCRRepository incidenciaOCRRepository

    @Autowired
    BitacoraAprRepository bitacoraAprRepository

    @Autowired
    UsuarioSaechvvRepository usuarioSaechvvRepository

    @Autowired
    VerificacionOBDService verificacionOBDService

    @Autowired
    ReporteTxtVerificacionService reporteTxtVerificacionService

    @Autowired
    SupervisionVerificacionCentroRepository supervisionVerificacionCentroRepository

    /**
     * Inicia una supervisión remota a un verificentro aleatorio que cumpla con los criterios para ser seleccionado
     *
     * @return Mapa con la información del verificentro seleccionado y el folio de la supervisión remota iniciada
     */
    def iniciarSupervisionRemotaVerificentroAleatorio() {
        Integer idVerificentro = obtenerIdVerificentroAleatorio()

        return registrarSupervisionVerificentro(idVerificentro)
    }

    /**
     * Inicia una supervisión remota a un verificentro específico proporcionado en los parámetros
     *
     * @param parametros Mapa con los parámetros necesarios para iniciar la supervisión remota
     *                    - verificentro: Identificador del verificentro
     *                    - totalVerificaciones: Total de verificaciones a obtener en los últimos 7 días
     * @return Mapa con la información del verificentro seleccionado, el folio de la supervisión remota iniciada
     *         y las verificaciones realizadas en los últimos 7 días
     */
    def iniciarSupervisionRemotaVerificentroEspecifico(Map parametros) {

        Integer idVerificentro = parametros.get("verificentro") as Integer
        Integer totalVerificaciones = parametros.get("totalVerificaciones") as Integer

        def respuestaRegistroSupervision = registrarSupervisionVerificentro(idVerificentro)

        if (respuestaRegistroSupervision?.error){
            return respuestaRegistroSupervision
        }

        String fechaInicio = new SimpleDateFormat("dd/MM/yyyy")?.format(restarDias(new Date(), -7))
        def verificacionesVerificentro = verificentroRepository.getVerificacionesVerificentro(fechaInicio, totalVerificaciones)

        respuestaRegistroSupervision.put("verificaciones", verificacionesVerificentro)
        return respuestaRegistroSupervision
    }

    /**
     * Registra una supervisión remota para un verificentro específico
     *
     * @param idVerificentro Integer Identificador del verificentro
     * @return Map Mapa con la información del verificentro y el folio de la supervisión remota iniciada
     */
    def registrarSupervisionVerificentro(Integer idVerificentro){
        if (!idVerificentro || idVerificentro == 0) {
            return [error  : true, statusCode: NOT_FOUND.value(), status: NOT_FOUND,
                    message: "No hay verificentros disponibles para supervisar, favor de intentarlo más tarde."]
        } else {
            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy")

            def datosVerificentro = datosVerificentroAleatorio(verificentroRepository.findVerificentroById(idVerificentro))

            SimpleDateFormat formatoAnio = new SimpleDateFormat("yyyy")
            SimpleDateFormat formatoMes = new SimpleDateFormat("MM")

            String anioActual = formatoAnio.format(new Date())
            String mesActual = formatoMes.format(new Date())

            Integer idSupervisionRemota = supervisionRemotaRepository.getSiguienteId()
            String folio = idVerificentro + mesActual + anioActual + idSupervisionRemota
            datosVerificentro.folio = folio

            Date fechaHoy = new Date()

            String fechaProxima = formatoFecha.format(restarDias(fechaHoy, 15))

            def respuestaRegistro = registrarSupervisionRemota(idVerificentro, folio, 1, idSupervisionRemota)
            if (respuestaRegistro?.error) {
                return [error: true, message: respuestaRegistro?.message, status: CONFLICT, statusCode: CONFLICT.value()]
            }
            actualizaFechaSupervisionRemota(idVerificentro, fechaProxima)

            return [statusCode       : OK.value(),
                    status: OK,
                    message          : "Se inició la supervisión remota correctamente.",
                    datosVerificentro: datosVerificentro]
        }
    }

    /**
     * Obtiene la información del verificentro y el total de verificaciones realizadas en los últimos 7 días
     * para una supervisión remota en específico
     *
     * @param idVerificentro Integer Identificador del verificentro
     * @param folioSupervision String Folio de la supervisión remota
     * @return Map Mapa con la información del verificentro y el total de verificaciones realizadas en los últimos 7 días
     */
    def obtenerInformacionSupervisionVerificentro(Integer idVerificentro, String folioSupervision) {

        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy")
        String fechaActual = formatoFecha.format(new Date())

        def datosVerificentro = datosVerificentroAleatorio(verificentroRepository.findVerificentroById(idVerificentro))

        def supervisionRemota = supervisionRemotaRepository.findByFolio(folioSupervision)
        if(!supervisionRemota || !supervisionRemota.isPresent()){
            return [error: true, message: "No se encontró información de la supervisión remota", status: NOT_FOUND, statusCode: NOT_FOUND.value()]
        }

        String fechaInicio = formatoFecha.format(restarDias(new Date(), -7))

        datosVerificentro.folio = folioSupervision
        datosVerificentro.idSupervisionRemota = supervisionRemota?.get()?.id
        datosVerificentro.fechaMuestra = fechaInicio + " al " + fechaActual
        datosVerificentro.totalVerificaciones = verificentroRepository.findTotalVerificacionesByVerificentro(idVerificentro, fechaInicio, fechaActual) ?: 0

        return [statusCode       : OK.value(), status: OK, message: "Información del verificentro.",
                datosVerificentro: datosVerificentro
        ]
    }

    /**
     * Formatea la información del verificentro para una supervisión remota
     *
     * @param datosVerificentro Object Objeto con la información del verificentro
     * @return Map Mapa con la información formateada del verificentro
     */
    def datosVerificentroAleatorio(def datosVerificentro) {

        def respuesta = [:]
        respuesta.idVerificentro = datosVerificentro.idVerificentro
        respuesta.direccion = "Calle " + (datosVerificentro?.calle ?: "N/A") +
                " #" + (datosVerificentro?.noExterior ?: "N/A") +
                ", C.P. " + (datosVerificentro?.colonia ?: "N/A") +
                ", " + (datosVerificentro?.municipio ?: "N/A") +
                ", " + (datosVerificentro?.entidad ?: "N/A")
        respuesta.centroNombre = datosVerificentro.centroNombre ?: 'N/A'
        respuesta.razonSocial = datosVerificentro.razonSocial ?: 'N/A'
        respuesta.correoElectronico = datosVerificentro.email1 + " " + datosVerificentro.email2
        respuesta.telefono = datosVerificentro.telefono1 + " " + datosVerificentro.telefono2
        respuesta.representante = datosVerificentro.representante ?: 'N/A'
        respuesta.folio = datosVerificentro.folio ?: 'N/A'

        return respuesta
    }

    /**
     * Obtiene el identificador de un verificentro aleatorio que cumpla con los criterios para ser seleccionado
     *
     * @return Integer Identificador del verificentro aleatorio
     */
    Integer obtenerIdVerificentroAleatorio() {

        int maximoIntentos = 100
        boolean bandera = true
        Integer idVerificentro = 0
        int contador = 0
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy")
        String fechaActual = formatoFecha.format(new Date())

        while (bandera && contador < maximoIntentos) {
            contador++

            idVerificentro = verificentroRepository.findIdCentroAleatorio()

            if (idVerificentro == null) {
                idVerificentro = 0
                bandera = false
                continue
            }

            String fechaProximaSupervision = verificentroRepository.findProximaFechaSupervisionByCentroId(idVerificentro)

            if (fechaProximaSupervision == null) {
                bandera = false
                continue
            }

            try {
                Date fechaProximaSupervisionVerificentro = formatoFecha.parse(fechaProximaSupervision)
                Date fechaHoy = formatoFecha.parse(fechaActual)

                if (!fechaHoy.before(fechaProximaSupervisionVerificentro)) {
                    bandera = false
                }
            } catch (ParseException excepcion) {
                excepcion.printStackTrace()
            }
        }

        return contador >= maximoIntentos ? 0 : idVerificentro
    }

    /** Resta o suma días a una fecha
     * @param date Date Fecha a la que se le restarán o sumarán días
     * @param dias int Días a restar (negativo) o sumar (positivo)
     * @return Date Fecha resultante de la operación
     */
    Date restarDias(Date date, int dias) {
        Calendar calendar = Calendar.getInstance()
        calendar.setTime(date)
        calendar.add(Calendar.DAY_OF_YEAR, dias)
        return calendar.getTime()
    }

    /**
     * Registra una supervisión remota en la base de datos
     *
     * @param idVerificentro Integer Identificador del verificentro
     * @param folio String Folio de la supervisión remota
     * @param tipoSupervision Integer Tipo de supervisión (1 = verificación, 2 = verificentro)
     * @param idTabla Integer Identificador de la tabla
     * @return Map Mapa con el resultado del registro de la supervisión remota
     */
    @Transactional
    def registrarSupervisionRemota(Integer idVerificentro, String folio, Integer tipoSupervision, Integer idTabla) {

        Optional<Verificentro> verificentro = verificentroRepository.findById(idVerificentro)
        if (!verificentro.isPresent()) {
            return [error: true, message: 'No se encontró el verificentro para iniciar la supervisión remota.']
        }

        SupervisionRemota supervisionRemota = new SupervisionRemota()
        supervisionRemota.id = idTabla
        supervisionRemota.usuario = generalService.usuarioSession
        supervisionRemota.verificentro = verificentro.get()
        supervisionRemota.folio = folio
        supervisionRemota.estatus = 0
        supervisionRemota.fechaRegistro = new Date()
        supervisionRemota.tipoSupervision = tipoSupervision
        supervisionRemota.preguardado = 0

        def supervisionRemotaRegistrada = supervisionRemotaRepository.save(supervisionRemota)
        if (!supervisionRemotaRegistrada) {
            return [error: true, message: 'Ocurrió un error al iniciar la supervisión remota.']
        }
        return [error: false, message: 'Se inició la supervisión remota correctamente.']
    }


    /**
     * Actualiza la fecha de próxima supervisión remota de un verificentro
     *
     * @param idVerificentro Integer Identificador del verificentro
     * @param nuevaFecha String Nueva fecha de próxima supervisión remota en formato "dd/MM/yyyy"
     * @return Integer 1 si se actualizó correctamente, 0 en caso contrario
     */
    int actualizaFechaSupervisionRemota(int idVerificentro, String nuevaFecha) {
        try {
            def verificentro = verificentroRepository.findById(idVerificentro).orElse(null)
            if (verificentro) {
                SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy")

                verificentro.fechaProximaSupervision = formatoFecha.parse(nuevaFecha)
                verificentroRepository.save(verificentro)
                return 1
            }
            return 0
        } catch (Exception e) {
            e.printStackTrace()
            return 0
        }
    }

    /**
     * Genera un reporte en formato .txt con las supervisiones realizadas en los últimos 7 días para un verificentro específico
     *
     * @param idVerificentro Integer Identificador del verificentro
     * @return String Contenido del archivo .txt con el reporte de supervisiones
     */
    String generarReporteSupervisionesTxt(Integer idVerificentro) {
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy")
        String fechaFin = formatoFecha.format(new Date())
        String fechaInicio = formatoFecha.format(restarDias(new Date(), -7))
        def verificacionesVerificentro = verificacionRepository.findVerificacionesByVerificentro(idVerificentro, fechaInicio, fechaFin)

        String contenidoReporte = reporteTxtVerificacionService.generarReporteVerificacionesTxt(verificacionesVerificentro)

        return contenidoReporte
    }

    /**
     * Consulta la información general de una verificación específica selecionda aleatoriamente.
     * @param idVerificacion Integer identificador de la verificación a consultar.
     * @return Map con los datos de la verificación, estatus y mensaje.
     */
    def informacionGeneralVerificacion(Integer idVerificacion, String folioSupervision) {
        if (idVerificacion == null || folioSupervision == null) {
            return [error: true, statusCode: NOT_FOUND.value(), status: NOT_FOUND, message: "No se encontró la verificación solicitada."]
        }
        try {
            Optional<SupervisionRemota> supervisionRemota = supervisionRemotaRepository.findByFolio(folioSupervision)

            if (!supervisionRemota.isPresent()) {
                return [error: true, statusCode: NOT_FOUND.value(), status: NOT_FOUND, message: "No se encontró la información de la supervisión remota."]
            }

            String folio = supervisionRemota?.get()?.folio

            def datosVerificacion = datosVerificacionAleatoria(verificentroRepository.findDatosVerificacionById(idVerificacion))
            datosVerificacion.folio = folio
            return [datosVerificacion: datosVerificacion, status: OK, statusCode: OK.value(), message: "Consulta de datos exitosa"]
        } catch (Exception exception) {
            exception.printStackTrace()
            return [error: true, statusCode: INTERNAL_SERVER_ERROR.value(), status: INTERNAL_SERVER_ERROR, message: "Ocurrió un error al consultar los datos de la verificación."]
        }
    }

    /**
     * Inicia el proceso de supervisión remota aleatoria sobre una verificación.
     * @return Map con los datos del verificentro seleccionado, el folio generado y el estado de la operación.
     */
    def iniciarSupervisionVerificacionAleatoria() {
        Integer idVerificacion = obtenerIdVerificacionAleatoria()
        if (idVerificacion == null || idVerificacion <= 0) {
            return [error: true, statusCode: NOT_FOUND.value(), status: NOT_FOUND, message: "No se encontraron verificaciones disponibles para supervisar, favor de intentarlo más tarde."]
        }
        return iniciarSupervisionRemotaVerificacion(idVerificacion)
    }

    /**
     * Inicia la supervisión remota de una verificación específica por su id.
     * @param idVerificacion Integer identificador de la verificación
     * @return Map con los datos del verificentro seleccionado, el folio generado y el estado de la operación.
     */
    def iniciarSupervisionRemotaVerificacion(Integer idVerificacion) {
        try {
            if (!idVerificacion) {
                return [error: true, statusCode: NOT_FOUND.value(), status: NOT_FOUND, message: "No se encontró la verificación solicitada."]
            }
            def datosVerificacion = datosVerificacionAleatoria(verificentroRepository.findDatosVerificacionById(idVerificacion))
            def respuestaRegistroMesaControl = registraMesaControl(datosVerificacion?.ticket as String, "Pendiente", idVerificacion)
            if (respuestaRegistroMesaControl?.error) {
                return [error: true, statusCode: NOT_FOUND.value(), status: NOT_FOUND, message: respuestaRegistroMesaControl?.message]
            }
            String anio = new SimpleDateFormat("yyyy").format(new Date())
            String mes = new SimpleDateFormat("MM").format(new Date())
            Integer idSupervisionRemota = supervisionRemotaRepository.getSiguienteId()
            String folioSupervisionRemota = datosVerificacion.idVerificentro + mes + anio + idSupervisionRemota
            def respuestaRegistro = registrarSupervisionRemota(datosVerificacion?.idVerificentro as Integer, folioSupervisionRemota, 2, idSupervisionRemota)
            if (respuestaRegistro?.error) {
                return [error: true, message: respuestaRegistro?.message, status: CONFLICT, statusCode: CONFLICT.value()]
            }
            datosVerificacion.folio = folioSupervisionRemota
            return [datosVerificacion: datosVerificacion, status: OK, statusCode: OK.value(), message: "Se inició la supervisión remota correctamente"]
        } catch (Exception e) {
            e.printStackTrace()
            return [error: true, statusCode: INTERNAL_SERVER_ERROR.value(), status: INTERNAL_SERVER_ERROR, message: "Ocurrió un error al iniciar la supervisión remota de la verificación."]
        }
    }

    /**
     * Obtiene el id de una verificación aleatoria disponible para supervisión.
     * @return Integer id de la verificación aleatoria o -700 si no hay disponible.
     */
    private Integer obtenerIdVerificacionAleatoria() {
        int intentos = 0
        Integer idVerificacion = verificacionRepository.getIdVerificacionAleatoria()
        while (idVerificacion == null || idVerificacion == 0) {
            idVerificacion = verificacionRepository.getIdVerificacionAleatoria()
            intentos++
            if (intentos == 1500) {
                return -700
            }
        }
        return idVerificacion
    }

    /**
     * Registra una mesa de control con los datos proporcionados.
     * Verifica que los parámetros no sean nulos o vacíos antes de proceder.
     * @param ticket Identificador único del ticket de verificación.
     * @param anomalia String que describe la anomalía detectada.
     * @param idVerificacion int identificador de la verificación asociada.
     * @return Map con el estado de la operación y un mensaje.
     */
    def registraMesaControl(String ticket, String anomalia, Integer idVerificacion) {

        def respuesta = [error: true, message: 'Ocurrió un error al registrar la mesa de control.']

        if (!ticket || !anomalia || !idVerificacion) {
            return respuesta
        }
        Integer idMesaControl = mesaControlRepository.getSiguienteIdMesaControl()
        if (!idMesaControl) {
            return respuesta
        }
        try {
            MesaControl mesaControl = new MesaControl()
            mesaControl.id = idMesaControl
            mesaControl.usuario = generalService.usuarioSession.id
            mesaControl.idUnique = ticket
            mesaControl.anomalia = anomalia
            mesaControl.fechaMonitoreo = new Date()
            mesaControl.verificacion = idVerificacion
            def registroMesaControl = mesaControlRepository.save(mesaControl)

            if (!registroMesaControl) {
                return respuesta
            }

            respuesta.error = false
            respuesta.message = "Se registró la mesa de control correctamente"

            actualizarVerificacion(idVerificacion)

            return respuesta

        } catch (Exception excepcion) {
            excepcion.printStackTrace()
            respuesta.error = true
            return respuesta
        }

    }

    /**
     * Actualiza el campo FECHA_MONITOREO con la fecha actual de tabla VERIFICACION por un registro en
     * específico
     * @param idVerificacion Integer identificador del registro a actualizar
     */
    void actualizarVerificacion(Integer idVerificacion) {
        Optional<Verificacion> verificacionBuscar = verificacionRepository.findById(idVerificacion)
        if (verificacionBuscar.isPresent()) {
            Verificacion verificacion = verificacionBuscar.get()
            verificacion.fechaMonitoreo = new Date()
            verificacionRepository.save(verificacion)
        }
    }

    /**
     * Procesa y estructura los datos generales de una verificación aleatoria obtenidos desde la base de datos para su uso en el sistema.
     * @param datosVerificacion Map Mapa que contiene los datos crudos de la verificación
     * @return Map<String, Object> Mapa con los datos estructurados de la verificación
     */
    def datosVerificacionAleatoria(def datosVerificacion) {

        def respuesta = [:]
        respuesta.idVerificentro = datosVerificacion.idCentro
        respuesta.direccion = "Calle " + (datosVerificacion?.calle ?: "N/A") +
                " #" + (datosVerificacion?.noExt ?: "N/A") +
                ", C.P. " + (datosVerificacion.cp ?: "N/A") + ", " + (datosVerificacion?.colonia ?: "N/A") +
                ", " + (datosVerificacion?.municipio ?: "N/A") +
                ", " + (datosVerificacion?.entidad ?: "N/A")
        respuesta.nombreCentro = (datosVerificacion.nombreCentro ?: 'N/A') + " - " + (datosVerificacion.razonSocial ?: 'N/A')
        respuesta.correoElectronico = datosVerificacion.email1 + (datosVerificacion.email2 ? ",  " + datosVerificacion.email2 : " ")
        respuesta.telefono = datosVerificacion.telefono1 + (datosVerificacion.telefono2 ? ",  " + datosVerificacion.telefono2 : " ")
        respuesta.estatusVerificacion = EstatusVerificacion.getProperties(datosVerificacion.estatusVerif as Integer)
        respuesta.tipoLinea = datosVerificacion.tipoLinea
        respuesta.numeroLinea = datosVerificacion.noLinea
        respuesta.idLineaVerificacion = datosVerificacion.idLineaVerificacion
        respuesta.idVerificacion = datosVerificacion.idVerificacion
        respuesta.representante = datosVerificacion.representante
        respuesta.ticket = datosVerificacion.ticket
        respuesta.numeroSerie = datosVerificacion?.numeroSerie
        respuesta.numeroPlaca = datosVerificacion.numeroPlaca
        respuesta.marca = datosVerificacion?.marca
        respuesta.submarca = datosVerificacion?.submarca
        respuesta.modelo = datosVerificacion?.modelo

        return respuesta
    }

    /**
     * Retorna la información detallada de una verificación específica por su identificador.
     *
     * @param idVerificacion Integer Identificador único de la verificación
     * @return Map con información de la verificación o error si no se encuentra
     */
    def obtenerInformacionDetalladaVerificacion(Integer idVerificacion) {
        Map respuestaVerificacion = verificacionRepository.getDatosVerificacion(idVerificacion)

        if (!respuestaVerificacion || respuestaVerificacion.isEmpty()) {
            return [error: true, message: "No se encontró información de la verificación solicitada", status: NOT_FOUND, statusCode: NOT_FOUND.value()]
        }

        def respuestaVerificacionProcesada = procesarDatosVerificacion(respuestaVerificacion)

        Map datosPruebaOBD = verificacionRepository.findVerificacionObdByVerificacion(idVerificacion)
        Integer combustible = respuestaVerificacionProcesada?.datosEvaluacion?.combustible as Integer


        if (datosPruebaOBD && !datosPruebaOBD.isEmpty() && combustible != 2) {
            Integer idTipoHolograma = respuestaVerificacion.get("IDTIPOHOLOGRAMA") as Integer
            Map verificacionOBD = verificacionOBDService.procesarRespuestaPruebaOBD(datosPruebaOBD, idTipoHolograma)
            respuestaVerificacionProcesada.put("verificacionOBD", verificacionOBD)
        }

        return respuestaVerificacionProcesada
    }


    /**
     * Procesa y estructura los datos completos de una verificación es específico para su uso en el sistema.
     * @param datosVerificacion Map Mapa que contiene los datos crudos de la verificación obtenidos de la base de datos
     * @return Map<String, Object> Mapa con los datos estructurados de la verificación
     */
    def procesarDatosVerificacion(Map detalleVerificacion) {
        Map datosVerificentro = [:]
        Map datosVehiculo = [:]
        Map datosVerificacion = [:]
        Map datosEvaluacionPrueba = [:]

        datosVerificentro.nombreVerificentro = detalleVerificacion.CENTRO_NOM + " " + detalleVerificacion.RAZON_SOCIAL
        datosVerificentro.equipoVerificacion = detalleVerificacion.equipover
        datosVerificentro.numeroLinea = detalleVerificacion.NO_LINEA
        datosVerificentro.tipoLinea = detalleVerificacion.tipolinea
        datosVerificentro.nombreTecnico = detalleVerificacion.NOMTECNICO
        datosVerificentro.idverificentro = detalleVerificacion.IDVERIFICENTRO
        datosVerificentro.idLineaVerificacion = detalleVerificacion.IDLINEAVERIF

        datosVehiculo.idtablamaestra = detalleVerificacion.idtablamaestra
        datosVehiculo.marca = detalleVerificacion.Marca
        datosVehiculo.submarca = detalleVerificacion.Submarca
        datosVehiculo.modelo = detalleVerificacion.MODELO
        datosVehiculo.vehiculo = detalleVerificacion.Marca + " " + detalleVerificacion.Submarca + " Modelo " + detalleVerificacion.MODELO
        datosVehiculo.tipoServicio = detalleVerificacion.SERVICIO
        datosVehiculo.numeroPlaca = detalleVerificacion.NO_PLACA
        datosVehiculo.kilometraje = detalleVerificacion.km
        datosVehiculo.tarjetaCirculacion = detalleVerificacion.tarjeta
        datosVehiculo.entidadPlaca = detalleVerificacion.ENTIDAD
        datosVehiculo.fechaEmisionTarjeta = detalleVerificacion.FECHA_TARJETA
        datosVehiculo.numeroSerie = detalleVerificacion.NO_SERIE
        datosVehiculo.tipoCombustible = detalleVerificacion.tipocombus
        datosVehiculo.numeroMotor = detalleVerificacion.no_motor
        datosVehiculo.fechaFactura = detalleVerificacion.FECHAFAC
        datosVehiculo.idpadronvehicular = detalleVerificacion.IDPADRONVEHICULAR
        datosVehiculo.domicilio = detalleVerificacion.DOMICILIO

        datosVerificacion.numeroVerificacion = detalleVerificacion.IDUNIQUE
        datosVerificacion.motivoVerificacion = detalleVerificacion.Motivo
        datosVerificacion.folioAnterior = detalleVerificacion.FOLIO_ANTERIOR
        datosVerificacion.hologramaSolicitado = detalleVerificacion.TIPOHOLOINI
        datosVerificacion.fechaInicioVerificacion = detalleVerificacion.FECHA_REGISTRO
        datosVerificacion.fechaInicioPrueba = detalleVerificacion.FECHA_INI_PRUEBA
        datosVerificacion.resultado = detalleVerificacion.resultado
        datosVerificacion.tipoRechazo = detalleVerificacion.TIPO_RECHAZO
        datosVerificacion.descripcionTipoPrueba = detalleVerificacion.TIPO_PRUEBA
        datosVerificacion.tipoPrueba = detalleVerificacion.tipoPrueba
        datosVerificacion.folioMulta = detalleVerificacion.FOLIO_MULTA
        datosVerificacion.hologramaObtenido = detalleVerificacion.TIPOHOLOFIN
        datosVerificacion.fechaTerminoVerificacion = detalleVerificacion.FECHAFIN
        datosVerificacion.fechaTerminoPrueba = detalleVerificacion.FECHA_FIN_PRUEBA
        datosVerificacion.folioObtenido = detalleVerificacion.FOLIO
        datosVerificacion.idverificacion = detalleVerificacion.IDVERIFICACION
        datosVerificacion.estatus = EstatusVerificacion.getProperties(detalleVerificacion.estatus as Integer)

        datosEvaluacionPrueba.combustible = detalleVerificacion.COMBUSTIBLE
        datosEvaluacionPrueba.tipoPrueba = detalleVerificacion.tipoPrueba
        datosEvaluacionPrueba.cal = detalleVerificacion.CAL
        datosEvaluacionPrueba.evalcal = detalleVerificacion.EVALCAL
        datosEvaluacionPrueba.temcam = detalleVerificacion.TEMCAM
        datosEvaluacionPrueba.opacidad = detalleVerificacion.OPACIDAD
        datosEvaluacionPrueba.maxrpm = detalleVerificacion.MAXRPM
        datosEvaluacionPrueba.idParametroPirec = detalleVerificacion.ID_PARAM_PIREC
        datosEvaluacionPrueba.hc2 = detalleVerificacion.HC2
        datosEvaluacionPrueba.evalhc = detalleVerificacion.EVALHC
        datosEvaluacionPrueba.hc1 = detalleVerificacion.HC1
        datosEvaluacionPrueba.co2 = detalleVerificacion.CO2
        datosEvaluacionPrueba.co1 = detalleVerificacion.CO1
        datosEvaluacionPrueba.evalpirco = detalleVerificacion.EVALPIRCO
        datosEvaluacionPrueba.evalco = detalleVerificacion.EVALCO
        datosEvaluacionPrueba.evalpirco2 = detalleVerificacion.EVALPIRCO2
        datosEvaluacionPrueba.co22 = detalleVerificacion.CO22
        datosEvaluacionPrueba.co21 = detalleVerificacion.CO21
        datosEvaluacionPrueba.coco22 = detalleVerificacion.COCO22
        datosEvaluacionPrueba.evalcoco2fin = detalleVerificacion.EVALCOCO2FIN
        datosEvaluacionPrueba.evalcoco2ini = detalleVerificacion.EVALCOCO2INI
        datosEvaluacionPrueba.coco21 = detalleVerificacion.COCO21
        datosEvaluacionPrueba.o22 = detalleVerificacion.O22
        datosEvaluacionPrueba.evalpiro2 = detalleVerificacion.EVALPIRO2
        datosEvaluacionPrueba.o21 = detalleVerificacion.O21
        datosEvaluacionPrueba.evalo2est = detalleVerificacion.EVALO2EST
        datosEvaluacionPrueba.evalo2 = detalleVerificacion.EVALO2
        datosEvaluacionPrueba.no2 = detalleVerificacion.NO2
        datosEvaluacionPrueba.evalnox = detalleVerificacion.EVALNOX
        datosEvaluacionPrueba.no1 = detalleVerificacion.NO1
        datosEvaluacionPrueba.lamda2 = detalleVerificacion.LAMDA2
        datosEvaluacionPrueba.evallamdaest = detalleVerificacion.EVAL_LAMDAEST
        datosEvaluacionPrueba.lamda1 = detalleVerificacion.LAMDA1
        datosEvaluacionPrueba.evallamda = detalleVerificacion.EVAL_LAMDA
        datosEvaluacionPrueba.kph2 = detalleVerificacion.KPH2
        datosEvaluacionPrueba.kph1 = detalleVerificacion.KPH1
        datosEvaluacionPrueba.pot_5024 = detalleVerificacion.POT_5024
        datosEvaluacionPrueba.pot_25 = detalleVerificacion.POT_2540

        datosEvaluacionPrueba.gobernador = detalleVerificacion.GOBERNADOR
        datosEvaluacionPrueba.tempMot = detalleVerificacion.TEMP_MOT
        datosEvaluacionPrueba.temCam = detalleVerificacion.TEM_CAM
        datosEvaluacionPrueba.temGas = detalleVerificacion.TEM_GAS
        datosEvaluacionPrueba.velGob = detalleVerificacion.VEL_GOB
        datosEvaluacionPrueba.presGas = detalleVerificacion.PRES_GAS
        datosEvaluacionPrueba.hc5024b = detalleVerificacion.HC2B
        datosEvaluacionPrueba.hc5024 = detalleVerificacion.HC2
        datosEvaluacionPrueba.hc2540b = detalleVerificacion.HC1B
        datosEvaluacionPrueba.hc2540 = detalleVerificacion.HC1
        datosEvaluacionPrueba.co5024b = detalleVerificacion.CO2B
        datosEvaluacionPrueba.co5024 = detalleVerificacion.CO2
        datosEvaluacionPrueba.co2540b = detalleVerificacion.CO1B
        datosEvaluacionPrueba.co2540 = detalleVerificacion.CO1
        datosEvaluacionPrueba.co25024b = detalleVerificacion.CO22B
        datosEvaluacionPrueba.co25024 = detalleVerificacion.CO22
        datosEvaluacionPrueba.co22540b = detalleVerificacion.CO21B
        datosEvaluacionPrueba.co22540 = detalleVerificacion.CO21
        datosEvaluacionPrueba.o25024b = detalleVerificacion.O22B
        datosEvaluacionPrueba.o25024 = detalleVerificacion.O22
        datosEvaluacionPrueba.o22540b = detalleVerificacion.O21B
        datosEvaluacionPrueba.o22540 = detalleVerificacion.O21
        datosEvaluacionPrueba.nox25024b = detalleVerificacion.NO2B
        datosEvaluacionPrueba.no25024 = detalleVerificacion.NO2
        datosEvaluacionPrueba.nox2540b = detalleVerificacion.NO1B
        datosEvaluacionPrueba.no2540 = detalleVerificacion.NO1

        datosEvaluacionPrueba.lambda5024 = detalleVerificacion.LAMDA2
        datosEvaluacionPrueba.lambda2540 = detalleVerificacion.LAMDA1
        datosEvaluacionPrueba.coco25024 = detalleVerificacion.COCO22
        datosEvaluacionPrueba.coco22540 = detalleVerificacion.COCO21
        datosEvaluacionPrueba.psi5024 = detalleVerificacion.PSI_5024
        datosEvaluacionPrueba.psi2540 = detalleVerificacion.PSI_2540
        datosEvaluacionPrueba.rpm5024 = detalleVerificacion.RPM_5024
        datosEvaluacionPrueba.rpm2540 = detalleVerificacion.RPM_2540
        datosEvaluacionPrueba.kph5024 = detalleVerificacion.KPH2
        datosEvaluacionPrueba.kph2540 = detalleVerificacion.KPH1
        datosEvaluacionPrueba.volts5024 = detalleVerificacion.VOLTS_5024
        datosEvaluacionPrueba.volts2540 = detalleVerificacion.VOLTS_2540
        datosEvaluacionPrueba.hr5024 = detalleVerificacion.HR_5024
        datosEvaluacionPrueba.hr2540 = detalleVerificacion.HR_2540
        datosEvaluacionPrueba.thp5024 = detalleVerificacion.THP_5024
        datosEvaluacionPrueba.thp2540 = detalleVerificacion.THP_2540
        datosEvaluacionPrueba.fcnox5024 = detalleVerificacion.FCNOX_5024
        datosEvaluacionPrueba.fcnox2540 = detalleVerificacion.FCNOX_2540
        datosEvaluacionPrueba.fcdil5024 = detalleVerificacion.FCDIL_5024
        datosEvaluacionPrueba.fcdil2540 = detalleVerificacion.FCDIL_2540
        datosEvaluacionPrueba.efic5024 = detalleVerificacion.EFIC_5024
        datosEvaluacionPrueba.efic2540 = detalleVerificacion.EFIC_2540

        double HC = (detalleVerificacion.HC1B as double) * 0.0006
        double CO = detalleVerificacion.CO1B as double
        double NO = (detalleVerificacion.NO1B as double) / 10000
        double CO2 = detalleVerificacion.CO21B as double
        double O2 = detalleVerificacion.O21B as double

        double aux1 = CO2 + (CO / 2) + (NO / 2) + O2
        double aux2 = CO2 + CO
        double aux3 = (3.5 / (3.5 + (CO / CO2))) * 0.45425
        aux2 = aux3 * aux2
        aux1 = aux1 + aux2
        double aux4 = 1.45425 * (CO2 + CO + HC)
        double lamda_c1 = aux1 / aux4

        HC = (detalleVerificacion.HC2B as double) * 0.0006
        CO = detalleVerificacion.CO2B as double
        NO = (detalleVerificacion.NO2B as double) / 10000
        CO2 = detalleVerificacion.CO22B as double
        O2 = detalleVerificacion.O22B as double

        aux1 = CO2 + (CO / 2) + (NO / 2) + O2
        aux2 = CO2 + CO
        aux3 = (3.5 / (3.5 + (CO / CO2))) * 0.45425
        aux2 = aux3 * aux2
        aux1 = aux1 + aux2
        aux4 = 1.45425 * (CO2 + CO + HC)
        double lamda_c2 = aux1 / aux4

        return [datosVerificentro: datosVerificentro,
                datosVehiculo    : datosVehiculo,
                datosVerificacion: datosVerificacion,
                datosEvaluacion  : datosEvaluacionPrueba,
                lamda_c1         : lamda_c1,
                lamda_c2         : lamda_c2,
                statusCode       : OK.value(),
                status           : OK,
                message          : "Información verificación"
        ]
    }

    /**
     * Retorna la repuesta estructurada con la información necesaria para el registro de una anomlía de una verificación.
     * @param Integer idVerificacion de la cual se quiere consultar la información
     * @return Map mapa con la información necesaria para el registro de la anomalía
     */
    def consultaInformacionRegistroAnomalia(Integer idVerificacion) {
        List<MotivoAnomalia> motivosAnomalia = motivoAnomaliaRepository.findAllByOrderByIdAsc()

        if (!motivosAnomalia || motivosAnomalia.isEmpty()) {
            return [error: true, message: "No se encontró la información necesaria para el registro de la anomalía", status: NOT_FOUND, statusCode: NOT_FOUND.value()]
        }

        def respuestaCalogos = respuestaCatalogos(motivosAnomalia)

        def datosVerificacion = datosVerificacionAleatoria(verificentroRepository.findDatosVerificacionById(idVerificacion))

        if (!datosVerificacion || datosVerificacion?.isEmpty()) {
            return [error: true, message: "No se encontró información de la verificación solicitada", status: NOT_FOUND, statusCode: NOT_FOUND.value()]
        }

        def respuestaRegistroAnomalia = [datosVerificacion: datosVerificacion, statusCode: OK.value()]
        respuestaRegistroAnomalia.putAll(respuestaCalogos)
        return respuestaRegistroAnomalia
    }

    /**
     * Genera la respuesta con los cátalogos necesarios para el registro de una anomalía en una verificación.
     * @param motivosAnomalia List<MotivoAnomalia> Lista de objetos con los motivos de anomalía
     * @return Map mapa con los catálogos necesarios para el registro  de anomalía
     */
    static respuestaCatalogos(List<MotivoAnomalia> motivosAnomalia) {

        List respuestaMotivosAnomalia = []
        if (motivosAnomalia != null && !motivosAnomalia.isEmpty()) {
            respuestaMotivosAnomalia = motivosAnomalia.collect { motivoAnomalia ->
                [
                        id         : motivoAnomalia?.id,
                        descripcion: motivoAnomalia?.descripcion
                ]
            }
        }

        List repuestaMedidasTomar = [
                [id: 1, descripcion: "Notificación al verificentro"],
                [id: 2, descripcion: "Registro de expediente"],
                [id: 3, descripcion: "Cierre de línea de verificación"]
        ]

        return [motivosAnomalia: respuestaMotivosAnomalia, medidasTomar: repuestaMedidasTomar]

    }

    /**
     * Registra una anomalía en la mesa de control de supervisión remota.
     * @param datosAnomalia Map con los datos necesarios para el registro de la anomalía.
     * @return Map mapa con la respuesta de la operación y un mensaje.
     */
    @Transactional
    def registrarAnomaliaVerificacion(Map datosAnomalia) {

        Integer idVerificacion = datosAnomalia.get("idVerificacion") as Integer
        String numeroVerificacion = datosAnomalia.get("numeroVerificacion")
        Integer tipoAnomalia = datosAnomalia.get("tipoAnomalia") as Integer
        Integer medidaTomar = datosAnomalia.get("medidaTomar") as Integer
        String descripcionAnomalia = datosAnomalia.get("descripcionAnomalia")
        String folioSupervisionRemota = datosAnomalia.get("folioSupervisionRemota") as String
        Integer idLineaVerificacion = datosAnomalia.get("idLineaVerificacion") as Integer

        String folioAnomalia = verificacionRepository.getFolioVerificacion(idVerificacion)

        def respuestaRegistroAnomalia = registrarAnomaliaMesaControl(idVerificacion, tipoAnomalia, medidaTomar, descripcionAnomalia)

        if (respuestaRegistroAnomalia.error) {
            return [status: CONFLICT, statusCode: CONFLICT.value(), message: respuestaRegistroAnomalia?.message]
        }
        String claveAnomalia = respuestaRegistroAnomalia?.claveAnomalia
        Integer idAnomalia = anomaliaSupervisionRemotaRepository.getIdAnomaliaSupervision()
        def respuestaRegistroBitacoraAnomalia = registrarAnomaliaSupervisionRemota(idAnomalia, folioSupervisionRemota, folioAnomalia, tipoAnomalia, descripcionAnomalia)

        if (respuestaRegistroBitacoraAnomalia.error) {
            return [status: CONFLICT, statusCode: CONFLICT.value(), message: respuestaRegistroBitacoraAnomalia.message]
        }

        String asuntoNotificacion
        String descripcionNotificacion
        switch (medidaTomar) {
            case 1:
                asuntoNotificacion = "Anomalía en el proceso de verificación"
                descripcionNotificacion = "<b>Detalle de la anomalía reportada por el supervisor:</b> ${descripcionAnomalia}"
                def respuestaEnvioNotificacion = enviarNotificacion(idVerificacion, descripcionNotificacion, tipoAnomalia, asuntoNotificacion)
                if (respuestaEnvioNotificacion?.error) {
                    return [status: CONFLICT, statusCode: CONFLICT.value(), message: respuestaEnvioNotificacion.message]
                }
                break
            case 3:
                def respuestaCierreLinea = lineaVerificacionService.cerrarLineaVerificacion(idLineaVerificacion)
                if (respuestaCierreLinea?.error) {
                    return [status: CONFLICT, statusCode: CONFLICT.value(), message: respuestaCierreLinea.message]
                }
                asuntoNotificacion = "Cierre temporal de línea de verificación"
                descripcionNotificacion = "<b> El supervisor ha determinado el cierre temporal de la línea de verificación, para mayor información favor de comunicarse con Mesa de Control.</b> "
                enviarNotificacion(idVerificacion, descripcionNotificacion, tipoAnomalia, asuntoNotificacion)
                break
        }
        def respuestaActualizacion = actualizaMesaControl(numeroVerificacion, claveAnomalia, idVerificacion)
        if (respuestaActualizacion.error) {
            return [status: CONFLICT, statusCode: CONFLICT.value(), message: respuestaActualizacion.message]
        }
        return [status: OK, statusCode: OK.value(), message: "Se ha registrado la anomalía de la verificación correctamente, es posible iniciar un nuevo proceso de supervisión.", claveAnomalia: claveAnomalia]
    }

    /**
     * Guarda una anomalía en la mesa de control de supervisión remota.
     * @param idVerificacion Integer identificador de la verificación donde se registrará la anomalía.
     * @param tipoAnomalia Integer tipo de anomalía encontrada en la verificación.
     * @param medidaTomar Integer acción a tomar por la anomalía encontrada (1: Notificación, 2: Registro de expediente, 3: Cierre de línea de verificación).
     * @param descripcion String descripción de la anomalía encontrada.
     * @return Map con el resultado del registro de la anomalía. Si ocurre un error, se devuelve un mensaje de error.
     */
    def registrarAnomaliaMesaControl(Integer idVerificacion, Integer tipoAnomalia, Integer medidaTomar, String descripcion) {
        def respuesta = [error: true, message: 'Ocurrió un error al registrar la anomalía.']

        try {
            Usuario usuario = generalService.getUsuarioSession()
            String clave = generaClaveAnomalia(medidaTomar, usuario?.id, tipoAnomalia)

            AnomaliaMesaControl anomaliaMesaControl = new AnomaliaMesaControl()
            anomaliaMesaControl.id = anomaliaMesaControlRepository.getIdAnomalia()
            anomaliaMesaControl.clave = clave
            anomaliaMesaControl.descripcion = descripcion
            anomaliaMesaControl.verificacion = verificacionRepository.findById(idVerificacion)?.get()
            anomaliaMesaControl.motivoAnomalia = motivoAnomaliaRepository.findById(tipoAnomalia)?.get()
            anomaliaMesaControl.usuario = usuario
            anomaliaMesaControl.tipoAccion = medidaTomar
            anomaliaMesaControl.fechaRegistro = new Date()

            def anomaliaRegistrada = anomaliaMesaControlRepository.save(anomaliaMesaControl)

            if (!anomaliaRegistrada) {
                return respuesta
            }

            actualizarVerificacion(idVerificacion)
            actualizaBitacoras(idVerificacion)

            return [error: false, message: "Anomalía registrada correctamente", claveAnomalia: clave]

        } catch (Exception excepcion) {
            excepcion.printStackTrace()
            return respuesta
        }
    }

    /**
     * Genera la clave para el registro de una anomalía en una verificación
     * @param medidaTomar Integer acción a tomar por la anomalía encontrada
     * @param tipoAnomalia Integer tipo de anomalía encontrada en la verificación
     * @param usuario Integer identificador del usuario en sesión
     * @return String clave única generada para el registro de la anomalía
     */
    static String generaClaveAnomalia(Integer medidaTomar, Integer usuario, Integer tipoAnomalia) {
        return usuario.toString() + tipoAnomalia.toString() + System.currentTimeMillis() + medidaTomar.toString()
    }


    /**
     * Envía una notificación a los destinatarios de un verificentro cuando se identifica una anomalía durante una supervisión remota de una verificación
     * @param idVerificacion Integer Identificador de la verificación asociada a la anomalía.
     * @param descripcionNotificacion String descripción de la notificación.
     * @param motivoAnomalia Integer identificador del motivo de anomalía, correspondiente a un motivo existente en el catálogo de anomalías.
     * @param asuntoNotificacion String Asunto del mensaje de la notificación
     * @return Map<String, Object> respuesta del envío de la notificación
     */
    def enviarNotificacion(Integer idVerificacion, String descripcionNotificacion, Integer motivoAnomalia, String asuntoNotificacion) {

        try {
            Verificacion verificacion = verificacionRepository.findById(idVerificacion)?.get()
            if (verificacion == null || verificacion?.verificentro == null) {
                return [error: true, message: "No se encontró el verificentro para el envío de la notificación."]
            }

            String destinatarios = (usuarioSaechvvRepository.detinatariosMensaje(verificacion?.verificentro?.id) ?: []).join(',')
            if (destinatarios == null || destinatarios.isEmpty()) {
                return [error: true, message: "No se encontraron destinatarios para el envío de la notificación."]
            }
            String nombreVerificentro = verificacion?.verificentro?.centroNombre + " " + verificacion?.verificentro?.razonSocial
            String descripcionMotivoAnomalia = motivoAnomaliaRepository.getDescripcionAnomalia(motivoAnomalia)

            String descripcionMensaje = "El verificentro <b>${nombreVerificentro}</b> fue seleccionado aleatoriamente para una supervisión remota." +
                    " Durante esta revisión, el supervisor identificó la siguiente anomalía: <b>${descripcionMotivoAnomalia}</b>.<br><br>${descripcionNotificacion}"

            Map datosNotificacion = [
                    tipoDestinatario: 4, // Gerente de verificentro
                    asunto          : asuntoNotificacion,
                    descripcion     : descripcionMensaje,
                    modulo          : 2, // Módulo saechvv
                    tipoMensaje     : 2,
                    destinatarios   : destinatarios,
                    idUsuario       : generalService.getUsuarioSession()?.id // Usuario que registra la notificación
            ]

            String url = "http://10.9.6.40:8090/mia-api/monitoreoSupervicion/registrarMensajeModuloMonitoreo"
            def respuestaRegistroMensaje = generalService.consumeServiciosRest(url, null, "POST", [:], datosNotificacion, LinkedHashMap.class)

            if (!respuestaRegistroMensaje || !respuestaRegistroMensaje?.success) {
                return [error: true, message: "Ocurrió un error al enviar la notificación al verificentro."]
            }

            return [error: false, message: "Notificación enviada correctamente a los destinatarios del verificentro."]

        } catch (Exception exception) {
            exception.printStackTrace()
            return [error: true, message: "Ocurrió un error al enviar la notificación"]
        }
    }

    /**
     * Inserta un registro de anomalía en la bitácora de supervisión remota.
     * @param idAnomalia Integer identificador de la anomalía a registrar
     * @param folioSupervisionRemota String folio de la supervisión remota
     * @param folioAnomalia String folio a asignar a la anomalía
     * @param motivoAnomalia Integer identificador del motivo de la anomalía
     * @param descripcionAnomalia String descripción de la anomalía encontrada
     * @return Map con el resultado de la operación, incluyendo error y mensaje.
     */
    @Transactional
    def registrarAnomaliaSupervisionRemota(Integer idAnomalia, String folioSupervisionRemota, String folioAnomalia, Integer motivoAnomalia, String descripcionAnomalia) {
        def respuesta = [error: true, message: 'Ocurrió un error al registrar la anomalía de la supervisión remota.']
        try {
            def supervisionRemota = supervisionRemotaRepository.findByFolio(folioSupervisionRemota)?.orElse(null)
            if (!supervisionRemota) {
                respuesta.message = "No se encontró la supervisión remota con el folio proporcionado."
                return respuesta
            }

            def descripcionTipoAnomalia = motivoAnomaliaRepository.getDescripcionAnomalia(motivoAnomalia)
            def usuario = generalService.getUsuarioSession()

            AnomaliaSupervisionRemota anomaliaSupervisionRemota = new AnomaliaSupervisionRemota(
                    id: idAnomalia,
                    fechaRegistro: new Date(),
                    supervisionRemota: supervisionRemota,
                    folio: folioAnomalia,
                    tipoAnomalia: descripcionTipoAnomalia,
                    observacion: descripcionAnomalia,
                    activo: 1,
                    usuario: usuario
            )

            def anomaliaRegistrada = anomaliaSupervisionRemotaRepository.save(anomaliaSupervisionRemota)
            if (!anomaliaRegistrada) {
                return respuesta
            }

            actualizarSupervisionRemota(supervisionRemota)
            return [error: false, message: "Anomalía registrada correctamente"]
        } catch (Exception exception) {
            exception.printStackTrace()
            return [error: true, message: "Error al registrar la anomalía"]
        }
    }

    /**
     * Actualiza la superivisión remota para indicar que se ha registrado una anomalía
     * @param supervisionRemota SupervisionRemota objeto de la supervisión remota a actualizar
     */
    void actualizarSupervisionRemota(SupervisionRemota supervisionRemota) {
        supervisionRemota.conAnomalias = 1
        supervisionRemotaRepository.save(supervisionRemota)
    }

    /**
     * Llama a los servicios para actualizar el estatus de BITACORA_APR e INCIDENCIAS_OCR
     * @param verificacion Integer identificador de la verificación asociada
     */
    void actualizaBitacoras(Integer verificacion) {
        try {
            bitacoraAprRepository.updateEstatusBitacoraByVerificacion(verificacion, 4)
            incidenciaOCRRepository.updateEstatusIncidenciaByVerificacion(verificacion, 4)
        } catch (Exception excepcion) {
            excepcion.printStackTrace()
        }
    }

    /**
     * Actualiza la mesa de control con la anomalía encontrada en una verificación.
     * @param numeroVerificacion String número de verificación único
     * @param claveAnomalia String clave de la anomalía registrada
     * @param idVerificacion Integer identificador de la verificación asociada
     * @return Map con el resultado de la operación, incluyendo error y mensaje.
     */
    def actualizaMesaControl(String numeroVerificacion, String claveAnomalia, Integer idVerificacion) {

        def respuesta = [error: true, message: 'Ocurrió un error al actualizar la mesa de control.']

        try {
            List<MesaControl> mesasControl = mesaControlRepository.findAllByIdUnique(numeroVerificacion)
            if (!mesasControl || mesasControl.isEmpty()) {
                return respuesta
            }
            mesasControl.each { mesaControl ->
                mesaControl.anomalia = claveAnomalia
                mesaControlRepository.save(mesaControl)
            }

            actualizarVerificacion(idVerificacion)
            actualizaBitacoras(idVerificacion)

            respuesta.error = false
            respuesta.message = "Mesa de control actualizada correctamente"
            return respuesta
        } catch (Exception exception) {
            exception.printStackTrace()
            return respuesta
        }
    }

    /**
     * Obtiene la lista de documentos de un vehículo asociados a una verificación
     * @param idVerificacion Integer identificador de la verificación
     * @return List<Map> lista de documentos asociados a la verificación
     */
    def getDocumentosVerificacion(Integer idVerificacion) {
        if (!idVerificacion) {
            return [error: true, message: "No se encontró la verificación para consultar la documentación", status: NOT_FOUND, statusCode: NOT_FOUND.value()]
        }

        def documentos = verificacionRepository.getDocumentosVerificacion(idVerificacion)
        if (!documentos || documentos.isEmpty()) {
            return [error: true, message: "No se encontraron documentos asociados a la verificación", status: NOT_FOUND, statusCode: NOT_FOUND.value()]
        }
        return [documentos: documentos, message: "Documentos encontrados", status: OK, statusCode: OK.value()]
    }

    /**
     * Busca verificaciones en base a los filtros proporcionados.
     * @param parametros Map con los parámetros de búsqueda y paginación
     * @return Map con el resultado de la búsqueda, incluyendo estatus, código y lista de resultados
     */
    def buscarVerificaciones(Map parametros) {
        println("Parametros de búsqueda "+parametros)

        def sinFiltros = !(parametros?.numeroPlaca?.toString()?.trim()) &&
                !(parametros?.numeroSerie?.toString()?.trim()) &&
                !(parametros?.folio?.toString()?.trim())

        if (sinFiltros) {
            return [statusCode: BAD_REQUEST.value(), status: BAD_REQUEST, message: "No se agregaron filtros de búsqueda"]
        }

        def listaVerificaciones = criteria.list(VerificacionAuxiliar.class, parametros, { filters, verificacion, builder, query ->
            if (parametros?.numeroPlaca) {
                filters << builder.equal(verificacion.get("numeroPlaca"), parametros.numeroPlaca?.toString()?.trim())
            }
            verificacion.join('padronVehicular').with { padronVehicular ->

                if (parametros?.numeroSerie)
                    filters << builder.equal(padronVehicular.get('numeroSerie'), parametros.numeroSerie as String)

            }

            if (parametros.folio) {
                filters << builder.equal(verificacion.get("folio"), parametros?.folio?.toString()?.trim())
            }
            filters << builder.isNull(verificacion.get("fechaMonitoreo"))
            def hoy = new Date()
            def calendario = Calendar.getInstance()
            calendario.setTime(hoy)
            calendario.set(Calendar.HOUR_OF_DAY, 0)
            calendario.set(Calendar.MINUTE, 0)
            calendario.set(Calendar.SECOND, 0)
            calendario.set(Calendar.MILLISECOND, 0)
            def fechaInicio = calendario.getTime()
            calendario.add(Calendar.DATE, 1)
            def fechaFin = calendario.getTime()
            filters << builder.greaterThanOrEqualTo(verificacion.get("fechaInicioVerificacion"), fechaInicio)
            filters << builder.lessThan(verificacion.get("fechaInicioVerificacion"), fechaFin)

            filters << verificacion.get("estatusOperacion").in([1, 2, 4, 11])
            filters << builder.equal(verificacion.get("fase"), 1)

        }, { root, builder, query ->
            query.orderBy(builder.desc(root.get("id")))
        })

        if (!listaVerificaciones || listaVerificaciones.isEmpty()) {
            return [statusCode: NOT_FOUND.value(), status: NOT_FOUND, message: "No se encontraron verificaciones disponibles para supervisar con los filtros de búsqueda agregados."]
        }

        def respuestaVerificaciones = listaVerificaciones.collect { verificacion ->
            [
                    id                     : verificacion.id,
                    folio                  : verificacion.folio,
                    fechaInicioVerificacion: verificacion.fechaInicioVerificacion,
                    numeroPlaca: verificacion.numeroPlaca,
                    estatusOperacion: EstatusVerificacion.get(verificacion.estatusOperacion)?.descripcion,
                    numeroSerie: verificacion?.padronVehicular?.numeroSerie,
                    marca: verificacion?.padronVehicular?.marca?.descripcion,
                    submarca: verificacion?.padronVehicular?.marcaSubmarca?.descripcion,
                    modelo: verificacion?.padronVehicular?.modelo
            ]
        }
        return [statusCode: OK.value(), status: OK, verificaciones: respuestaVerificaciones, totalResultados: listaVerificaciones.totalCount]
    }


    /**
     * Busca verificaciones asociadas a un verificentro específico.
     * @param idVerificentro Integer identificador del verificentro
     * @param parametros Map con los parámetros de búsqueda y paginación
     * @return Map con el resultado de la búsqueda, incluyendo estatus, código y lista de resultados
     */
    def buscarVerificacionesVerificentro(Integer idVerificentro) {

        Optional<Verificentro> verificentro = verificentroRepository.findById(idVerificentro)
        if(!verificentro.isPresent()){
            return [error: true, message: "No se encontró el verificentro a supervisar", statusCode: NOT_FOUND.value(), status: NOT_FOUND]
        }

        def listaVerificacionesVerificentro = criteria.list(Verificacion.class, parametros, { filters, verificacion, builder, query ->

            filters << builder.equal(verificacion.get("verificentro"), verificentro?.get())

            filters << builder.isNull(verificacion.get("fechaMonitoreo"))

            filters << verificacion.get("estatusOperacion").in([1, 2, 4, 11])
            filters << builder.equal(verificacion.get("fase"), 1)

        }, { root, builder, query ->
            query.orderBy(builder.desc(root.get("id")))
        })

        if (!listaVerificacionesVerificentro || listaVerificacionesVerificentro.isEmpty()) {
            return [statusCode: NOT_FOUND.value(), status: NOT_FOUND, message: "No se encontraron verificaciones disponibles para supervisar con los filtros de búsqueda agregados."]
        }

        listaVerificacionesVerificentro.each { verificacion ->
            SupervisionVerificacionCentro supervisionVerificacionCentro = new SupervisionVerificacionCentro()
            supervisionVerificacionCentro.supervisionRemota = new SupervisionRemota()
            supervisionVerificacionCentro.verificacion = verificacion
            supervisionVerificacionCentro.estatus = 1

            supervisionVerificacionCentroRepository.save(supervisionVerificacionCentro)
        }

        return [statusCode: OK.value(), status: OK, verificaciones: listaVerificacionesVerificentro, totalResultados: listaVerificacionesVerificentro.totalCount]
    }

    /**
     * Consulta las supervisiones remotas basadas en los filtros proporcionados.
     * @param parametros Map con los parámetros de búsqueda y paginación
     * @return Map mapa con el resultado de la búsqueda, incluyendo estatus, código y lista de resultados
     */
    def consultaSupervisionesRemotas(Map parametros) {
        def listaSupervisiones = criteria.list(SupervisionRemota.class, parametros, { filters, supervision, builder, query ->

            supervision.join('verificentro').with { verificentro ->

                if (parametros.verificentro)
                    filters << builder.equal(verificentro.get('id'), parametros.verificentro as Integer)

            }

            if (parametros?.estatusSupervision) {
                filters << builder.equal(supervision.get("estatus"), parametros.estatusSupervision as Integer)
            } else if (!(parametros?.folio || parametros?.tipoSupervision || parametros?.fechaRegistroDesde || parametros?.fechaRegistroHasta)) {
                filters << builder.equal(supervision.get("estatus"), 0)
            }

            if (parametros?.folio) {
                filters << builder.equal(supervision.get("folio"), parametros.folio?.toString()?.trim())
            }

            if (parametros?.tipoSupervision) {
                filters << builder.equal(supervision.get("tipoSupervision"), parametros?.tipoSupervision as Integer)
            }

            if (parametros?.fechaRegistroDesde || parametros?.fechaRegistroHasta) {
                SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                Date fechaInicial = parametros?.fechaRegistroDesde ?
                        formato.parse("${parametros?.fechaRegistroDesde} 00:00:00") :
                        formato.parse("${parametros?.fechaRegistroHasta} 00:00:00")

                Date fechaFinal = parametros?.fechaRegistroHasta ?
                        formato.parse("${parametros?.fechaRegistroHasta} 23:59:59") :
                        formato.parse("${parametros?.fechaRegistroDesde} 23:59:59")

                filters << builder.between(
                        supervision.get("fechaRegistro"),
                        fechaInicial,
                        fechaFinal
                )
            }


            filters << builder.equal(supervision.get("usuario"), generalService?.usuarioSession)

        }, { root, builder, query ->
            query.orderBy(builder.desc(root.get("fechaRegistro")))
        })

        if (!listaSupervisiones || listaSupervisiones.isEmpty()) {
            return [statusCode: NOT_FOUND.value(), status: NOT_FOUND, message: "No se encontraron supervisiones remotas con los filtros de búsqueda agregados."]
        }

        def respuestaSupervisionesRemotas = listaSupervisiones.collect { supervision ->
            [
                    id                : supervision.id,
                    folio             : supervision.folio,
                    fechaRegistro     : supervision.fechaRegistro,
                    tipoSupervision   : descripcionTipoSupervision(supervision?.tipoSupervision),
                    estatusSupervision: supervision?.estatus,
                    verificentro      : [
                            id    : supervision.verificentro?.id,
                            nombre: supervision?.verificentro?.centroNombre + " " + supervision?.verificentro?.razonSocial
                    ],
                    usuario           : [
                            id    : supervision?.usuario?.id,
                            nombre: supervision?.usuario?.nombre + " " + supervision?.usuario?.apellidoPaterno + " " + supervision?.usuario?.apellidoMaterno
                    ]
            ]
        }



        return [statusCode: OK.value(), status: OK, supervisionesRemotas: respuestaSupervisionesRemotas, totalCount: listaSupervisiones.totalCount, tiposSupervision: tiposSupervision()]
    }

    /**
     * Retorna la descripción del tipo de supervisión basado en su identificador.
     * @param tipoSupervision Integer identificador del tipo de supervisión
     * @return String descripción del tipo de supervisión
     */
    static String descripcionTipoSupervision(Integer tipoSupervision) {
        switch (tipoSupervision) {
            case 1:
                return "Supervisión por verificentro"
                break
            case 2:
                return "Supervisión por verificación"
                break
            case 3:
                return "Dirigido y dirección"
                break
            default: return "Desconocido"
        }
    }

    /**
     * Retorna la lista de tipos de supervisión disponibles.
     * @return List lista de tipos de supervisión con su id y descripción.
     */
    static def tiposSupervision() {
        return [
                [id: 1, descripcion: "Supervisión por verificentro"],
                [id: 2, descripcion: "Supervisión por verificación"],
                [id: 3, descripcion: "Dirigido y dirección"]
        ]
    }

    /**
     * Consulta los detalles de una supervisión remota específica.
     * @param idSupervision Integer identificador de la supervisión remota
     * @return Map con el resultado de la consulta, incluyendo error, mensaje, estatus HTTP y datos de la supervisión
     */
    def consultaSupervisionRemota(Integer idSupervision) {
        if (!idSupervision) {
            return [error: true, message: "No se proporcionó el identificador de la supervisión remota", status: BAD_REQUEST, statusCode: BAD_REQUEST.value()]
        }
        def datosSupervisionRemota = supervisionRemotaRepository.findDetalleSupervisionById(idSupervision)
        def listaAnomalias = anomaliaSupervisionRemotaRepository.findAnomaliasBySupervisionRemota(idSupervision)

        if (!datosSupervisionRemota || datosSupervisionRemota.isEmpty()) {
            return [error: true, message: "No se encontró información de la supervisión remota solicitada", status: NOT_FOUND, statusCode: NOT_FOUND.value()]
        }

        Integer idVerificentro = datosSupervisionRemota?.idVerificentro as Integer ?: 0

        def datosVerificentro = datosVerificentroAleatorio(verificentroRepository.findVerificentroById(idVerificentro))

        return [error            : false, status: OK, statusCode: OK.value(), datosSupervisionRemota: datosSupervisionRemota,
                datosVerificentro: datosVerificentro,
                listaAnomalias   : listaAnomalias]
    }

    /**
     * Cierra una supervisión remota, actualizando su estatus y registrando la información de cierre.
     * @param parametros Map con los parámetros necesarios para cerrar la supervisión remota
     * @return Map con el resultado de la operación, incluyendo error, mensaje y estatus HTTP
     */
    def cerrarSupervisionRemota(Map parametros) {

        Integer idSupervision = parametros.get("idSupervision") as Integer
        SupervisionRemota supervisionRemota = supervisionRemotaRepository.findById(idSupervision)?.orElse(null)
        if (!supervisionRemota) {
            return [error: true, message: "No se encontró la supervisión remota a cerrar", status: NOT_FOUND, statusCode: NOT_FOUND.value()]
        }

        Calendar fecha = Calendar.getInstance()
        int anio = fecha.get(Calendar.YEAR)
        Integer idFolioPdf = supervisionRemotaRepository.getIdFolioPdf()
        String folioPdf = generaNumeroPdf(idFolioPdf) + "/" + anio

        supervisionRemota.observaciones = parametros.get("observaciones")
        supervisionRemota.estatus = 1
        supervisionRemota.fechaCierre = new Date()
        supervisionRemota.numeroExpediente = parametros.get("numeroExpediente") as Integer ?: 0
        supervisionRemota.numeroVideo = parametros.get("numeroVideo") as Integer ?: 0
        supervisionRemota.folioPdf = folioPdf
        def supervisionRemotaActualizada = supervisionRemotaRepository.save(supervisionRemota)
        if (!supervisionRemotaActualizada) {
            return [error: true, status: CONFLICT, statusCode: CONFLICT.value(), message: 'Ocurrió un problema al cerrar la supervisión remota']
        }
        return [error: false, status: OK, statusCode: OK.value(), message: 'La supervisión remota se ha cerrado correctamente']

    }

    /**
     * Genera un número de folio en formato PDF con ceros a la izquierda.
     * @param consecutivo Integer número consecutivo para generar el folio
     * @return String folio generado en formato PDF
     */
    String generaNumeroPdf(Integer consecutivo) {
        return String.format("%04d", consecutivo)
    }

    /**
     * Consulta las anomalías registradas en la bitácora de supervisión remota para una supervisión específica.
     * @param idSupervisionRemota Integer identificador de la supervisión remota
     * @return Map con el resultado de la consulta, incluyendo error, mensaje, estatus HTTP y lista de anomalías
     */
    def consultaAnomaliasSupervisionVerificentro(Integer idSupervisionRemota){
        def respuestaTiposAnomalia = consultaTiposAnomalia()
        if (respuestaTiposAnomalia?.error){
            return respuestaTiposAnomalia
        }
        def listaAnomalias = anomaliaSupervisionRemotaRepository.findAnomaliasBySupervisionRemota(idSupervisionRemota)
        def respuesta = [error: false, statusCode: OK.value(), anomalias: listaAnomalias]

        respuesta.putAll(respuestaTiposAnomalia)
        return respuesta
    }

    def consultaTiposAnomalia(){
        List<MotivoAnomalia> tiposAnomalia = motivoAnomaliaRepository.findAllByOrderByIdAsc()

        if (!tiposAnomalia || tiposAnomalia.isEmpty()) {
            return [error: true, message: "No se encontró la información necesaria para el registro de la anomalía", status: NOT_FOUND, statusCode: NOT_FOUND.value()]
        }
        return [tiposAnomalia: tiposAnomalia]
    }

    /**
     * Registra una anomalía en la bitácora de supervisión remota para un verificentro específico.
     * @param datosAnomalia Map con los datos necesarios para registrar la anomalía
     * @return Map con el resultado del registro, incluyendo error, mensaje y estatus HTTP
     */
    def registrarAnomaliaVerificentro(Map datosAnomalia){

        Integer idAnomalia = anomaliaSupervisionRemotaRepository.getIdAnomaliaSupervision()
        String folioSupervision = datosAnomalia?.get("folioSupervision")
        String idVerificentro = datosAnomalia?.get("idVerificentro")
        Integer tipoAnomalia = datosAnomalia.get("tipoAnomalia") as Integer
        String descripcionAnomalia = datosAnomalia.get("descripcionAnomalia")
        String folioAnomalia = idAnomalia + idVerificentro

        registrarAnomaliaSupervisionRemota(idAnomalia, folioSupervision, folioAnomalia, tipoAnomalia, descripcionAnomalia)
    }

}