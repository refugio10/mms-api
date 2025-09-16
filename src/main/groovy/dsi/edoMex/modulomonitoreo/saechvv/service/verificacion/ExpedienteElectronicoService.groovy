package dsi.edoMex.modulomonitoreo.saechvv.service.verificacion

import dsi.edoMex.modulomonitoreo.modulomonitoreo.service.utilerias.ArchivoXls
import dsi.edoMex.modulomonitoreo.saechvv.entity.verificacion.DatoSegundoSegundo
import dsi.edoMex.modulomonitoreo.saechvv.entity.verificacion.SegundoSegundoHumo
import dsi.edoMex.modulomonitoreo.saechvv.entity.catalogo.Verificacion
import dsi.edoMex.modulomonitoreo.saechvv.entity.enums.EstatusVerificacion
import dsi.edoMex.modulomonitoreo.saechvv.repository.catalogo.VerificacionRepository
import dsi.edoMex.modulomonitoreo.saechvv.repository.catalogo.VerificentroRepository
import dsi.edoMex.modulomonitoreo.saechvv.repository.verificacion.DatoSegundoSegundoRepository
import dsi.edoMex.modulomonitoreo.saechvv.repository.verificacion.SegundoSegundoHumoRepository
import groovy.json.JsonSlurper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Contiene funciones necesarias para generar el expediente electrónico de una verificación en formato excel.
 * @author Refugio Rodríguez Bueno
 * @version 1.0 Agosto 2025
 */
@Service
class ExpedienteElectronicoService {

    @Autowired
    SegundoSegundoHumoRepository segundoSegundoHumoRepository

    @Autowired
    DatoSegundoSegundoRepository datoSegundoSegundoRepository

    @Autowired
    VerificacionRepository verificacionRepository

    @Autowired
    VerificentroRepository verificentroRepository

    @Autowired
    VerificacionOBDService verificacionOBDService

    /**
     * Genera un archivo Excel con el detalle de segundo a segundo de una verificación
     * para las tres etapas de una verificación específica.
     * @return byte[] Arreglo de bytes que representa el archivo Excel generado
     */
    byte[] generarExcelDetalleSegundo(Map parametros) {

        Integer idVerificacion = parametros.get("idVerificacion") as Integer
        String tipoPrueba = parametros.get("tipoPrueba")
        String serie = parametros.get("serie")

        def datosVerificacion = verificentroRepository.findDatosVerificacionById(idVerificacion)
        Integer combustible = datosVerificacion?.get("combustible") as Integer ?: 1
        Integer idTipoHolograma = datosVerificacion.get("IDTIPOHOLOGRAMA") as Integer
        Integer pruebaOBD = datosVerificacion.get("pruebaOBD") as Integer


        // NFORMACIÓN GENERAL
        ArchivoXls archivo = new ArchivoXls("Información General")
        generarHojaInformacionGeneral(archivo, datosVerificacion)

        if (combustible == 2) {
            // Para DIESEL: Mostrar tablas de aceleraciones
            generarHojasAceleracionesDiesel(archivo, idVerificacion)
        } else {
            if(pruebaOBD == 1 || pruebaOBD == 2 ) {
                archivo.agregaHoja("Prueba OBD")
                generarHojaInformacionPruebaOBD(archivo, idVerificacion, idTipoHolograma)
            }

            // DETALLE SEGUNDO A SEGUNDO HUMOS
            archivo.agregaHoja("Datos de Humos")
            generarHojaHumos(archivo, idVerificacion, serie, tipoPrueba)

            // DETALLE SEGUNDO A SEGUNDO
            archivo.agregaHoja("Detalle Emisiones")
            generarHojaSegundoSegundo(archivo, idVerificacion, tipoPrueba, serie)

            // DETALLE DE DINAMÓMETROS
            archivo.agregaHoja("Carga dinamómetros")
            generarHojaDinamometros(archivo, idVerificacion, serie, tipoPrueba)
        }

        return convertirAByteArray(archivo)
    }

    /**
     * Genera las hojas para aceleraciones diesel
     */
    void generarHojasAceleracionesDiesel(ArchivoXls archivo, Integer idVerificacion) {

        def escapes = obtenerAceleracionesDiesel(idVerificacion)

        if (escapes && !escapes.isEmpty()) {

            escapes.each { escape ->
                String nombreHoja = "Escape ${escape?.noescape}"
                if (nombreHoja.length() > 31) {
                    nombreHoja = nombreHoja.substring(0, 31)
                }
                archivo.agregaHoja(nombreHoja)
                generarHojaAceleracionesEscape(archivo, escape)
            }
        } else {
            archivo.agregaHoja("Aceleraciones Diesel")
            archivo.agregaFila()
            archivo.agregaColumna("No se encontraron datos de aceleraciones para diesel", "estilo_contenido_normal", 5, 0)
        }
    }

    /**
     * Genera la hoja de aceleraciones para un escape específico
     */
    void generarHojaAceleracionesEscape(ArchivoXls archivo, def escape) {

        int[] anchoColumnas = [2, 3, 3, 3, 3, 3]
        archivo.modificaAnchoCeldas(anchoColumnas)

        archivo.agregaFila()
        archivo.agregaColumna("ACELERACIONES DIESEL - ESCAPE ${escape.noescape}", "estilo_titulo", 5, 0)


        archivo.agregaFila()
        archivo.agregaColumna("", null, 5, 0)

        // Generar tabla para cada aceleración
        escape.aceleraciones.each { aceleracion ->
            // Encabezado de la aceleración
            archivo.agregaFila()
            archivo.agregaColumna("ACELERACIÓN ${aceleracion.noaceleracion}", "estilo_subtitulo", 5, 0)

            // Espacio
            archivo.agregaFila()
            archivo.agregaColumna("En banda: ${aceleracion.enbanda == 1 ? 'SI' : 'NO'}", "estilo_contenido_normal", 5, 0)

            // Espacio
            archivo.agregaFila()


            def encabezados = ["Segundo", "Cal", "Opacidad", "RPM", "Temp. Motor", "Temp. Cam. Gas"]
            archivo.agregaFila()
            encabezados.each { encabezado ->
                archivo.agregaColumna(encabezado, "estilo_subtitulo")
            }

            // Datos de los segundos
            aceleracion.segundos.each { segundo ->
                archivo.agregaFila()
                archivo.agregaColumna(segundo.nosegundo.toString(), "estilo_contenido_centrado")
                archivo.agregaColumna(formatoCadena(segundo.cal?.toString()), "estilo_contenido_centrado")
                archivo.agregaColumna(formatoCadena(segundo.opacidad?.toString()), "estilo_contenido_centrado")
                archivo.agregaColumna(formatoCadena(segundo.rpm?.toString()), "estilo_contenido_centrado")
                archivo.agregaColumna(formatoCadena(segundo.tempmot?.toString()), "estilo_contenido_centrado")
                archivo.agregaColumna(formatoCadena(segundo.tempcamgas?.toString()), "estilo_contenido_centrado")
            }

            // Espacio entre aceleraciones
            archivo.agregaFila()
            archivo.agregaColumna("", null, 5, 0)
            archivo.agregaFila()
            archivo.agregaColumna("", null, 5, 0)
        }
    }

    /**
     * Obtiene las aceleraciones diesel desde la base de datos
     */
    List<Object> obtenerAceleracionesDiesel(Integer idVerificacion) {
        List<Object> escapes = []

        try {
            // Obtener el registro de ss_temporal
            Optional<DatoSegundoSegundo> optionalDato = datoSegundoSegundoRepository.findFirstByVerificacionOrderByIdDesc(idVerificacion)

            if (optionalDato.isPresent()) {
                DatoSegundoSegundo detalleSegundoSegundo = optionalDato.get()
                String datoJson = detalleSegundoSegundo?.datoSegundoSegundo
                if (datoJson != null && !datoJson.trim().isEmpty()) {
                    def jsonSlurper = new JsonSlurper()
                    escapes = jsonSlurper.parseText(datoJson) as List<Object>
                }
            }
        } catch (Exception e) {
            e.printStackTrace()
        }

        return escapes
    }

    /**
     * Genera la tabla con la información general de la verificación
     */
    static void generarHojaInformacionGeneral(ArchivoXls archivo, Map datosVerificacion) {

        int[] anchos = [3, 6]
        archivo.modificaAnchoCeldas(anchos)

        if (datosVerificacion != null) {

            archivo.agregaFila()
            archivo.agregaColumna("INFORMACIÓN GENERAL DE LA VERIFICACIÓN", "estilo_titulo", 1, 0)

            archivo.agregaFila()
            archivo.agregaColumna("", null, 1, 0)

            archivo.agregaFila()
            archivo.agregaColumna("VERIFICENTRO:", "estilo_subtitulo_izquierda")
            archivo.agregaColumna((datosVerificacion?.get("nombreCentro") ?: "") + " " + (datosVerificacion?.get("razonSocial") ?: ""), "estilo_contenido_normal")

            archivo.agregaFila()
            archivo.agregaColumna("NÚMERO DE LÍNEA:", "estilo_subtitulo_izquierda")
            archivo.agregaColumna(datosVerificacion?.get("noLinea") ?: "N/A", "estilo_contenido_normal")

            archivo.agregaFila()
            archivo.agregaColumna("TIPO DE LÍNEA:", "estilo_subtitulo_izquierda")
            archivo.agregaColumna(datosVerificacion?.get("tipoLinea") ?: "N/A", "estilo_contenido_normal")

            archivo.agregaFila()
            archivo.agregaColumna("PLACA:", "estilo_subtitulo_izquierda")
            archivo.agregaColumna(datosVerificacion?.get("numeroPlaca") ?: "N/A", "estilo_contenido_normal")

            archivo.agregaFila()
            archivo.agregaColumna("SERIE:", "estilo_subtitulo_izquierda")
            archivo.agregaColumna(datosVerificacion?.get("numeroSerie") ?: "N/A", "estilo_contenido_normal")

            archivo.agregaFila()
            archivo.agregaColumna("MARCA:", "estilo_subtitulo_izquierda")
            archivo.agregaColumna(datosVerificacion?.get("marca") ?: "N/A", "estilo_contenido_normal")

            archivo.agregaFila()
            archivo.agregaColumna("SUBMARCA:", "estilo_subtitulo_izquierda")
            archivo.agregaColumna(datosVerificacion?.get("submarca") ?: "N/A", "estilo_contenido_normal")

            archivo.agregaFila()
            archivo.agregaColumna("MODELO:", "estilo_subtitulo_izquierda")
            archivo.agregaColumna(datosVerificacion?.get("modelo") ?: " ", "estilo_contenido_normal")

            archivo.agregaFila()
            archivo.agregaColumna("ESTATUS:", "estilo_subtitulo_izquierda")
            archivo.agregaColumna(EstatusVerificacion.get(datosVerificacion?.get("estatusVerif") as Integer) ?: " ", "estilo_contenido_normal")

            archivo.agregaFila()
            archivo.agregaColumna("FECHA VERIFICACIÓN:", "estilo_subtitulo_izquierda")
            archivo.agregaColumna(datosVerificacion?.get("fechaVerificacion") ?: " ", "estilo_contenido_normal")

            archivo.agregaFila()
            archivo.agregaColumna("RESULTADO:", "estilo_subtitulo_izquierda")
            archivo.agregaColumna(datosVerificacion?.get("descripcionResultado") ?: " ", "estilo_contenido_normal")

            if(datosVerificacion?.get("resultado") == -1){
                archivo.agregaFila()
                archivo.agregaColumna("RECHAZO:", "estilo_subtitulo_izquierda")
                archivo.agregaColumna(datosVerificacion?.get("tipoRechazo") ?: "N/A", "estilo_contenido_normal")
            }

        } else {
            // Si no se encuentra la información de la verificación
            archivo.agregaFila()
            archivo.agregaColumna("No se encontró información de la verificación", "estilo_subtitulo", 1, 0)
        }
    }

    /**
     * Genera la hoja de datos con la información de segundo a segundo humos
     */
    void generarHojaHumos(ArchivoXls archivo, Integer idVerificacion, String serie, String tipoPrueba) {

        List<String[]> datosSegundoHumos = obtenerDetalleSegundoHumos(idVerificacion, 1)

        int[] anchosColumnasHumos = [3, 3, 3, 3, 3, 3, 3]
        archivo.modificaAnchoCeldas(anchosColumnasHumos)

        archivo.agregaFila()
        archivo.agregaColumna("DATOS DE HUMOS - SEGUNDO A SEGUNDO", "estilo_titulo", 6, 0)

        // Espacio
        archivo.agregaFila()
        archivo.agregaColumna("", null, 6, 0)

        archivo.agregaFila()
        archivo.agregaColumna("CARGA DE HUMOS", "estilo_subtitulo", 6, 0)

        archivo.agregaFila()
        def encabezadosColumnasHumos = ["Seg.", "Fuerza", "Par Torsional", "Velocidad Angular", "Velocidad Lineal", "Potencia al Freno BHP", "Potencia al Freno W"]
        encabezadosColumnasHumos.each { encabezado ->
            archivo.agregaColumna(encabezado, "estilo_encabezado_rojo")
        }

        int limiteDatosValidosEtapa1 = 0
        int limiteDatosValidosEtapa2 = 0
        int promedioSegundos = 10
        int tiempoBaseEtapa1 = 30
        int tiempoBaseEtapa2 = 90
        int rangoInicioEtapa1 = 0
        int rangoFinEtapa1 = 0
        int rangoInicioEtapa2 = 0
        int rangoFinEtapa2 = 0

        try {
            if (serie != null && !serie.equals("-1") && serie.contains("|")) {
                try {
                    String[] datosSerie = serie.split("\\|")
                    rangoInicioEtapa1 = Integer.parseInt(datosSerie[1])
                    rangoFinEtapa1 = Integer.parseInt(datosSerie[2])
                    rangoInicioEtapa2 = Integer.parseInt(datosSerie[3])
                    rangoFinEtapa2 = Integer.parseInt(datosSerie[4])
                } catch (Exception e) {
                    rangoInicioEtapa1 = 0
                    rangoFinEtapa1 = 0
                    rangoInicioEtapa2 = 0
                    rangoFinEtapa2 = 0
                }
            }

            if (tipoPrueba.equals("0")) {
                promedioSegundos = 5
                tiempoBaseEtapa2 = 60
            } else {
                promedioSegundos = 10
                tiempoBaseEtapa2 = 90
            }

            if (!datosSegundoHumos.get(0)[6].equals("---")) {
                for (int indice = 0; indice < datosSegundoHumos.size(); indice++) {
                    if (datosSegundoHumos.get(indice)[6].equals("---")) {
                        limiteDatosValidosEtapa1 = indice
                        break
                    }
                }
                if (limiteDatosValidosEtapa1 == 0) {
                    limiteDatosValidosEtapa1 = datosSegundoHumos.size()
                }

                if ((rangoInicioEtapa1 == 0 && rangoFinEtapa1 == 0) || (rangoFinEtapa1 - rangoInicioEtapa1 > promedioSegundos)) {
                    rangoInicioEtapa1 = limiteDatosValidosEtapa1 - promedioSegundos
                    rangoFinEtapa1 = limiteDatosValidosEtapa1
                }

                if ((rangoInicioEtapa2 == 0 && rangoFinEtapa2 == 0) || (rangoFinEtapa2 - rangoInicioEtapa2 > promedioSegundos)) {
                    rangoInicioEtapa2 = limiteDatosValidosEtapa2 - promedioSegundos
                    rangoFinEtapa2 = limiteDatosValidosEtapa2
                }

                String estiloFilaNormal = "estilo_contenido_centrado"

                for (int numeroSegundo = 0; numeroSegundo < 60; numeroSegundo++) {
                    int tiempoAjustadoEtapa1 = 0
                    if (!datosSegundoHumos.get(numeroSegundo)[1].equals("---")) {
                        tiempoAjustadoEtapa1 = Integer.parseInt(datosSegundoHumos.get(numeroSegundo)[1]) - tiempoBaseEtapa1
                    }
                    String estiloFilaActual = (tiempoAjustadoEtapa1 > rangoInicioEtapa1 && tiempoAjustadoEtapa1 <= rangoFinEtapa1) ?
                            "estilo_resaltado_rojo" : estiloFilaNormal

                    archivo.agregaFila()
                    archivo.agregaColumna((numeroSegundo + 1) as String, estiloFilaActual)
                    archivo.agregaColumna(evaluaCadena(datosSegundoHumos.get(numeroSegundo)[9]), estiloFilaActual) // Fuerza
                    archivo.agregaColumna(evaluaCadena(datosSegundoHumos.get(numeroSegundo)[10]), estiloFilaActual) // Par Torsional
                    archivo.agregaColumna(evaluaCadena(datosSegundoHumos.get(numeroSegundo)[7]), estiloFilaActual) // Vel. Angular
                    archivo.agregaColumna(evaluaCadena(datosSegundoHumos.get(numeroSegundo)[6]), estiloFilaActual) // Vel. Lineal
                    archivo.agregaColumna(evaluaCadena(datosSegundoHumos.get(numeroSegundo)[8]), estiloFilaActual) // Potencia BHP
                    archivo.agregaColumna(evaluaCadena(datosSegundoHumos.get(numeroSegundo)[11]), estiloFilaActual) // Potencia W
                }
            }
        } catch (Exception excepcion) {
            excepcion.printStackTrace()
        }
    }

    /**
     * Genera la hoja de datos con la información de segundo a segundo de la carga de dinamómetros
     */
    void generarHojaDinamometros(ArchivoXls archivo, Integer idVerificacion, String serie, String tipoPrueba) {

        List<String[]> datosDinamometroPas5024 = obtenerDetalleSegundoHumos(idVerificacion, 2)
        List<String[]> datosDinamometroPas2540 = obtenerDetalleSegundoHumos(idVerificacion, 3)

        int[] anchosColumnasTablas = [1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1]
        archivo.modificaAnchoCeldas(anchosColumnasTablas)

        archivo.agregaFila()
        archivo.agregaColumna("CARGA DINAMÓMETROS", "estilo_titulo", 14, 0)

        archivo.agregaFila()
        archivo.agregaColumna("PAS 5024", "estilo_encabezado_rojo", 6, 0)
        archivo.agregaColumna("", null, 0, 0)
        archivo.agregaColumna("PAS 2540", "estilo_encabezado_verde", 6, 0)

        // Encabezados de columnas para ambas tablas
        archivo.agregaFila()
        def encabezadosColumnasDinamometro = ["Seg", "F", "P.T", "V.A", "V.L", "P.F", "P.F. W"]

        encabezadosColumnasDinamometro.each { encabezado ->
            archivo.agregaColumna(encabezado, "estilo_encabezado_rojo")
        }
        archivo.agregaColumna("", null)

        encabezadosColumnasDinamometro.each { encabezado ->
            archivo.agregaColumna(encabezado, "estilo_encabezado_verde")
        }

        int limiteDatosValidosPas5024 = 0
        int limiteDatosValidosPas2540 = 0
        int promedioSegundos = 10
        int tiempoBasePas5024 = 30
        int tiempoBasePas2540 = 90

        int rangoInicialPas5024 = 0
        int rangoFinalPas5024 = 0
        int rangoInicialPas2540 = 0
        int rangoFinalPas2540 = 0

        try {
            if (serie != null && !serie.equals("-1") && serie.contains("|")) {
                try {
                    String[] datosSerie = serie.split("\\|")
                    rangoInicialPas5024 = Integer.parseInt(datosSerie[1])
                    rangoFinalPas5024 = Integer.parseInt(datosSerie[2])
                    rangoInicialPas2540 = Integer.parseInt(datosSerie[3])
                    rangoFinalPas2540 = Integer.parseInt(datosSerie[4])
                } catch (Exception e) {
                    rangoInicialPas5024 = 0
                    rangoFinalPas5024 = 0
                    rangoInicialPas2540 = 0
                    rangoFinalPas2540 = 0
                }
            }

            if (tipoPrueba.equals("0")) {
                promedioSegundos = 5
                tiempoBasePas2540 = 60
            } else {
                promedioSegundos = 10
                tiempoBasePas2540 = 90
            }

            if (!datosDinamometroPas5024.get(0)[6].equals("---") || !datosDinamometroPas2540.get(0)[6].equals("---")) {

                // Buscar límite de datos válidos para PAS 5024
                for (int i = 0; i < datosDinamometroPas5024.size(); i++) {
                    if (datosDinamometroPas5024.get(i)[6].equals("---")) {
                        limiteDatosValidosPas5024 = i
                        break
                    }
                }

                // Buscar límite de datos válidos para PAS 2540
                for (int i = 0; i < datosDinamometroPas2540.size(); i++) {
                    if (datosDinamometroPas2540.get(i)[6].equals("---")) {
                        limiteDatosValidosPas2540 = i
                        break
                    }
                }

                if (limiteDatosValidosPas5024 == 0) {
                    limiteDatosValidosPas5024 = datosDinamometroPas5024.size()
                }
                if (limiteDatosValidosPas2540 == 0) {
                    limiteDatosValidosPas2540 = datosDinamometroPas2540.size()
                }

                if ((rangoInicialPas5024 == 0 && rangoFinalPas5024 == 0) || (rangoFinalPas5024 - rangoInicialPas5024 > promedioSegundos)) {
                    rangoInicialPas5024 = limiteDatosValidosPas5024 - promedioSegundos
                    rangoFinalPas5024 = limiteDatosValidosPas5024
                }
                if ((rangoInicialPas2540 == 0 && rangoFinalPas2540 == 0) || (rangoFinalPas2540 - rangoInicialPas2540 > promedioSegundos)) {
                    rangoInicialPas2540 = limiteDatosValidosPas2540 - promedioSegundos
                    rangoFinalPas2540 = limiteDatosValidosPas2540
                }

                String estiloFilaNormal = "estilo_contenido_centrado"
                String estiloFilaPas5024, estiloFilaPas2540

                for (int numeroSegundo = 0; numeroSegundo < 60; numeroSegundo++) {

                    int tiempoAjustadoPas5024 = 0
                    if (!datosDinamometroPas5024.get(numeroSegundo)[1].equals("---")) {
                        tiempoAjustadoPas5024 = Integer.parseInt(datosDinamometroPas5024.get(numeroSegundo)[1]) - tiempoBasePas5024
                    }
                    estiloFilaPas5024 = (tiempoAjustadoPas5024 > rangoInicialPas5024 && tiempoAjustadoPas5024 <= rangoFinalPas5024) ?
                            "estilo_resaltado_rojo" : estiloFilaNormal

                    int tiempoAjustadoPas2540 = 0
                    if (!datosDinamometroPas2540.get(numeroSegundo)[1].equals("---")) {
                        tiempoAjustadoPas2540 = Integer.parseInt(datosDinamometroPas2540.get(numeroSegundo)[1]) - tiempoBasePas2540
                    }
                    estiloFilaPas2540 = (tiempoAjustadoPas2540 > rangoInicialPas2540 && tiempoAjustadoPas2540 <= rangoFinalPas2540) ?
                            "estilo_resaltado_verde" : estiloFilaNormal

                    archivo.agregaFila()
                    // Datos tabla PAS 5024
                    archivo.agregaColumna((numeroSegundo + 1) as String, estiloFilaPas5024)
                    archivo.agregaColumna(evaluaCadena(datosDinamometroPas5024.get(numeroSegundo)[9]), estiloFilaPas5024)
                    archivo.agregaColumna(evaluaCadena(datosDinamometroPas5024.get(numeroSegundo)[10]), estiloFilaPas5024)
                    archivo.agregaColumna(evaluaCadena(datosDinamometroPas5024.get(numeroSegundo)[7]), estiloFilaPas5024)
                    archivo.agregaColumna(evaluaCadena(datosDinamometroPas5024.get(numeroSegundo)[6]), estiloFilaPas5024)
                    archivo.agregaColumna(evaluaCadena(datosDinamometroPas5024.get(numeroSegundo)[8]), estiloFilaPas5024)
                    archivo.agregaColumna(evaluaCadena(datosDinamometroPas5024.get(numeroSegundo)[11]), estiloFilaPas5024)

                    // Espacio entre tablas
                    archivo.agregaColumna("", null)

                    // Datos tabla PAS 2540
                    archivo.agregaColumna((numeroSegundo + 1) as String, estiloFilaPas2540)
                    archivo.agregaColumna(evaluaCadena(datosDinamometroPas2540.get(numeroSegundo)[9]), estiloFilaPas2540)
                    archivo.agregaColumna(evaluaCadena(datosDinamometroPas2540.get(numeroSegundo)[10]), estiloFilaPas2540)
                    archivo.agregaColumna(evaluaCadena(datosDinamometroPas2540.get(numeroSegundo)[7]), estiloFilaPas2540)
                    archivo.agregaColumna(evaluaCadena(datosDinamometroPas2540.get(numeroSegundo)[6]), estiloFilaPas2540)
                    archivo.agregaColumna(evaluaCadena(datosDinamometroPas2540.get(numeroSegundo)[8]), estiloFilaPas2540)
                    archivo.agregaColumna(evaluaCadena(datosDinamometroPas2540.get(numeroSegundo)[11]), estiloFilaPas2540)
                }
            }
        } catch (Exception excepcion) {
            excepcion.printStackTrace()
        }
    }

    /**
     * Convierte un objeto de ArchivoXls a un arreglo de bytes para su descarga.
     * @param archivo ArchivoXls objeto que contiene el Excel a convertir
     * @return byte[] arreglo de bytes que representa el archivo Excel, o null en caso de error
     */
    byte[] convertirAByteArray(ArchivoXls archivo) {
        try (ByteArrayOutputStream arregloBytes = new ByteArrayOutputStream()) {
            archivo.workbook.write(arregloBytes)
            archivo.workbook.close()
            return arregloBytes.toByteArray()
        } catch (IOException excepcion) {
            excepcion.printStackTrace()
            return null
        }
    }

    /**
     * Genera la hoja con los datos de segundo a segundo de la prueba OBD
     * @param archivo ArchivoXls donde se agregará la hoja de segundo a segundo
     * @param idVerificacion Integer identificador de la verificación
     * @param tipoPrueba String tipo de prueba realizada
     * @param serie String serie del vehículo
     */
    void generarHojaSegundoSegundo(ArchivoXls archivo, Integer idVerificacion, String tipoPrueba, String serie) {
        int[] anchosColumnasSegundoSegundo = [1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1]
        archivo.modificaAnchoCeldas(anchosColumnasSegundoSegundo)

        List<String[]> datosSegundoSegundoPrimeraEtapa = obtenerDetalleSegundo(idVerificacion, 1)
        List<String[]> datosSegundoSegundoSegundaEtapa = obtenerDetalleSegundo(idVerificacion, 2)

        int limiteDatosValidosPrimeraEtapa = 0
        int limiteDatosValidosSegundaEtapa = 0
        int promedioSegundosAnalisis = tipoPrueba.equals("0") ? 5 : 10
        int rangoInicialPrimeraEtapa = 0, rangoFinalPrimeraEtapa = 0, rangoInicialSegundaEtapa = 0, rangoFinalSegundaEtapa = 0

        if (serie != null && !serie.equals("-1") && serie.contains("|")) {
            try {
                String[] datosSerie = serie.split("\\|")
                rangoInicialPrimeraEtapa = Integer.parseInt(datosSerie[1])
                rangoFinalPrimeraEtapa = Integer.parseInt(datosSerie[2])
                rangoInicialSegundaEtapa = Integer.parseInt(datosSerie[3])
                rangoFinalSegundaEtapa = Integer.parseInt(datosSerie[4])
            } catch (Exception e) {
                rangoInicialPrimeraEtapa = rangoFinalPrimeraEtapa = rangoInicialSegundaEtapa = rangoFinalSegundaEtapa = 0
            }
        }

        // Determinar límites
        if (!datosSegundoSegundoPrimeraEtapa.get(0)[6].equals("---") || !datosSegundoSegundoSegundaEtapa.get(0)[6].equals("---")) {
            limiteDatosValidosPrimeraEtapa = determinarLimite(datosSegundoSegundoPrimeraEtapa)
            limiteDatosValidosSegundaEtapa = determinarLimite(datosSegundoSegundoSegundaEtapa)

            if ((rangoInicialPrimeraEtapa == 0 && rangoFinalPrimeraEtapa == 0) || (rangoFinalPrimeraEtapa - rangoInicialPrimeraEtapa > promedioSegundosAnalisis)) {
                rangoInicialPrimeraEtapa = limiteDatosValidosPrimeraEtapa - promedioSegundosAnalisis
                rangoFinalPrimeraEtapa = limiteDatosValidosPrimeraEtapa
            }

            if ((rangoInicialSegundaEtapa == 0 && rangoFinalSegundaEtapa == 0) || (rangoFinalSegundaEtapa - rangoInicialSegundaEtapa > promedioSegundosAnalisis)) {
                rangoInicialSegundaEtapa = limiteDatosValidosSegundaEtapa - promedioSegundosAnalisis
                rangoFinalSegundaEtapa = limiteDatosValidosSegundaEtapa
            }

            // Generar encabezados
            generarEncabezados(archivo)

            generarFilasDatos(archivo, datosSegundoSegundoPrimeraEtapa, datosSegundoSegundoSegundaEtapa,
                    rangoInicialPrimeraEtapa, rangoFinalPrimeraEtapa, rangoInicialSegundaEtapa, rangoFinalSegundaEtapa)
        }
    }

    /**
     * Determina el límite de datos válidos en una lista de datos segundo a segundo.
     * @param datos Lista de arreglos de cadenas que representan los datos segundo a segundo
     * @return int índice del primer dato inválido o el tamaño de la lista si todos son válidos
     */
    static int determinarLimite(List<String[]> datos) {
        for (int indice = 0; indice < datos.size(); indice++) {
            if (datos.get(indice)[6].equals("---")) {
                return indice
            }
        }
        return datos.size()
    }

    /**
     * Genera los encabezados para las tablas de segundo a segundo
     */
    static void generarEncabezados(ArchivoXls archivo) {
        archivo.agregaFila()
        archivo.agregaColumna("DETALLE DE EMISIONES SEGUNDO A SEGUNDO", "estilo_titulo", 18, 0)

        archivo.agregaFila()
        archivo.agregaColumna("PAS 5024", "estilo_encabezado_rojo", 8, 0)
        archivo.agregaColumna("", null, 0, 0)
        archivo.agregaColumna("PAS 2540", "estilo_encabezado_verde", 8, 0)

        // Encabezados de columnas para ambas tablas
        archivo.agregaFila()
        def columnas = ["Seg.", "HC", "CO", "CO2", "O2", "NOX", "H.R.", "TEMP", "P.A."]

        columnas.each { columna ->
            archivo.agregaColumna(columna, "estilo_encabezado_rojo")
        }
        archivo.agregaColumna("", null)

        columnas.each { columna ->
            archivo.agregaColumna(columna, "estilo_encabezado_verde")
        }
    }

    /**
     * Genera las filas de datos para las tablas de segundo a segundo
     */
    static void generarFilasDatos(ArchivoXls archivo, List<String[]> datosSegundoSegundoPrimeraEtapa, List<String[]> datosSegundoSegundoSegundaEtapa, int num1, int num2, int num3, int num4) {
        for (int indice = 0; indice < 60; indice++) {
            archivo.agregaFila()

            // Determinar estilos basados en el rango
            String estiloNormal = "estilo_contenido_centrado"
            boolean resaltar1 = false
            boolean resaltar2 = false

            try {
                if (!datosSegundoSegundoPrimeraEtapa.get(indice)[4].equals("---")) {
                    int tx = Integer.parseInt(datosSegundoSegundoPrimeraEtapa.get(indice)[4])
                    resaltar1 = (tx > num1 && tx <= num2)
                }
                if (!datosSegundoSegundoSegundaEtapa.get(indice)[4].equals("---")) {
                    int tx = Integer.parseInt(datosSegundoSegundoSegundaEtapa.get(indice)[4])
                    resaltar2 = (tx > num3 && tx <= num4)
                }
            } catch (Exception e) {

            }

            String estilo1 = resaltar1 ? "estilo_resaltado_rojo" : estiloNormal
            String estilo2 = resaltar2 ? "estilo_resaltado_verde" : estiloNormal

            // Datos tabla 1
            archivo.agregaColumna((indice + 1) as String, estilo1)
            archivo.agregaColumna(evaluaCadena(datosSegundoSegundoPrimeraEtapa.get(indice)[6]), estilo1)
            archivo.agregaColumna(evaluaCadena(datosSegundoSegundoPrimeraEtapa.get(indice)[7]), estilo1)
            archivo.agregaColumna(evaluaCadena(datosSegundoSegundoPrimeraEtapa.get(indice)[8]), estilo1)
            archivo.agregaColumna(evaluaCadena(datosSegundoSegundoPrimeraEtapa.get(indice)[9]), estilo1)
            archivo.agregaColumna(evaluaCadena(datosSegundoSegundoPrimeraEtapa.get(indice)[10]), estilo1)
            archivo.agregaColumna(evaluaCadena(datosSegundoSegundoPrimeraEtapa.get(indice)[28]), estilo1)

            String hr1 = !datosSegundoSegundoPrimeraEtapa.get(indice)[27].equals("---") ?
                    String.valueOf(redondearDecimales(Double.parseDouble(evaluaCadena(datosSegundoSegundoPrimeraEtapa.get(indice)[27])), 2)) : "---"
            archivo.agregaColumna(hr1, estilo1)

            String pa1 = !datosSegundoSegundoPrimeraEtapa.get(indice)[29].equals("---") ?
                    String.valueOf(redondearDecimales(Double.parseDouble(evaluaCadena(datosSegundoSegundoPrimeraEtapa.get(indice)[29])), 2)) : "---"
            archivo.agregaColumna(pa1, estilo1)

            // Espacio entre tablas
            archivo.agregaColumna("", null)

            // Datos tabla 2
            archivo.agregaColumna((indice + 1) as String, estilo2)
            archivo.agregaColumna(evaluaCadena(datosSegundoSegundoSegundaEtapa.get(indice)[6]), estilo2)
            archivo.agregaColumna(evaluaCadena(datosSegundoSegundoSegundaEtapa.get(indice)[7]), estilo2)
            archivo.agregaColumna(evaluaCadena(datosSegundoSegundoSegundaEtapa.get(indice)[8]), estilo2)
            archivo.agregaColumna(evaluaCadena(datosSegundoSegundoSegundaEtapa.get(indice)[9]), estilo2)
            archivo.agregaColumna(evaluaCadena(datosSegundoSegundoSegundaEtapa.get(indice)[10]), estilo2)
            archivo.agregaColumna(evaluaCadena(datosSegundoSegundoSegundaEtapa.get(indice)[28]), estilo2)

            String hr2 = !datosSegundoSegundoSegundaEtapa.get(indice)[27].equals("---") ?
                    String.valueOf(redondearDecimales(Double.parseDouble(evaluaCadena(datosSegundoSegundoSegundaEtapa.get(indice)[27])), 2)) : "---"
            archivo.agregaColumna(hr2, estilo2)

            String pa2 = !datosSegundoSegundoSegundaEtapa.get(indice)[29].equals("---") ?
                    String.valueOf(redondearDecimales(Double.parseDouble(evaluaCadena(datosSegundoSegundoSegundaEtapa.get(indice)[29])), 2)) : "---"
            archivo.agregaColumna(pa2, estilo2)
        }
    }

    /**
     * Obtiene los datos de segundo a segundo para una verificación y etapa específica
     * @param idVerificacion ID de la verificación
     * @param etapa Etapa (1 o 2)
     * @return Lista de arrays con los datos
     */
    List<String[]> obtenerDetalleSegundo(Integer idVerificacion, Integer etapa) {
        List<String[]> resultado = []

        try {
            // Obtener el último registro de la verificación
            Optional<DatoSegundoSegundo> optionalDato = datoSegundoSegundoRepository.findFirstByVerificacionOrderByIdDesc(idVerificacion)

            if (optionalDato.isPresent()) {
                DatoSegundoSegundo dato = optionalDato.get()
                String detalleSegundo = dato?.datoSegundoSegundo

                if (detalleSegundo != null && !detalleSegundo.trim().isEmpty()) {
                    String[] tmpDetalle = detalleSegundo.split("&")

                    for (String detalle : tmpDetalle) {
                        String[] tmpSegundo = detalle.split("\\|")

                        if (tmpSegundo.length > 0 && Integer.parseInt(tmpSegundo[0]) == etapa) {
                            String[] campos = new String[33]
                            campos[0] = "---"
                            campos[1] = "---"
                            campos[2] = dato.verificacion?.toString() ?: " "
                            campos[3] = tmpSegundo[0] ?: " " // FASE
                            campos[4] = tmpSegundo[1] ?: " " // TX
                            campos[5] = tmpSegundo[2] ?: " " // COMANDO
                            campos[6] = tmpSegundo[3] ?: " " // HC
                            campos[7] = tmpSegundo[4] ?: " " // CO
                            campos[8] = tmpSegundo[5] ?: " " // CO2
                            campos[9] = tmpSegundo[6] ?: " " // O2
                            campos[10] = tmpSegundo[7] ?: " " // NOX
                            campos[11] = tmpSegundo[8] ?: " " // RPM
                            campos[12] = tmpSegundo[9] ?: " " // OIL_TEMP
                            campos[13] = tmpSegundo[10] ?: " " // AMBIENT_TEMP
                            campos[14] = tmpSegundo[11] ?: " " // PRESSURE
                            campos[15] = tmpSegundo[12] ?: " " // RH
                            campos[16] = tmpSegundo[13] ?: " " // EXTERNAL_TEMP_RH
                            campos[17] = tmpSegundo[14] ?: " " // LAMP_TEMP
                            campos[18] = tmpSegundo[15] ?: " " // DETECTOR_TEMP
                            campos[19] = tmpSegundo[16] ?: " " // EXTERNAL_TEMP
                            campos[20] = tmpSegundo[17] ?: " " // UNDEFINED1
                            campos[21] = tmpSegundo[18] ?: " " // UNDEFINED2
                            campos[22] = tmpSegundo[19] ?: " " // UNDEFINED3
                            campos[23] = tmpSegundo[20] ?: " " // UNDEFINED4
                            campos[24] = tmpSegundo[21] ?: " " // UNDEFINED5
                            campos[25] = tmpSegundo[22] ?: " " // VELOCIDAD
                            campos[26] = tmpSegundo[23] ?: " " // TEMP_DINA
                            campos[27] = tmpSegundo[24] ?: " " // Temperatura
                            campos[28] = tmpSegundo[25] ?: " " // Humedad Relativa
                            campos[29] = recuperarValor(tmpSegundo, 26) ?: "0" // Presion Atmosferica
                            campos[30] = recuperarValor(tmpSegundo, 27) ?: "0" // Velocidad lineal
                            campos[31] = recuperarValor(tmpSegundo, 28) ?: "0" // Velocidad angular
                            campos[32] = recuperarValor(tmpSegundo, 29) ?: "0" // Carga

                            resultado.add(campos)
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace()
        }

        while (resultado.size() < 60) {
            resultado.add(new String[]{"---", "---", "---", "---", "---", "---", "---", "---", "---", "---", "---",
                    "---", "---", "---", "---", "---", "---", "---", "---", "---", "---", "---", "---", "---",
                    "---", "---", "---", "---", "---", "---", "---", "---", "---"})
        }

        return resultado
    }

    /**
     * Obtiene los datos de segundo a segundo para una verificación y etapa específica (humos o dinamómetros)
     * @param idVerificacion ID de la verificación
     * @param etapa Etapa (1 para humos, 2 para dinamómetro PAS 5024, 3 para dinamómetro PAS 2540)
     * @return Lista de arrays con los datos
     */
    List<String[]> obtenerDetalleSegundoHumos(Integer idVerificacion, Integer etapa) {
        List<String[]> resultado = []

        try {
            Optional<Verificacion> optionalVerificacion = verificacionRepository.findById(idVerificacion)

            if (optionalVerificacion.isPresent()) {
                Verificacion verificacion = optionalVerificacion.get()

                Optional<SegundoSegundoHumo> optionalDato = segundoSegundoHumoRepository.findFirstByVerificacionOrderByIdDesc(verificacion)

                if (optionalDato.isPresent()) {
                    SegundoSegundoHumo dato = optionalDato.get()
                    String detalleSegundo = dato?.datoSegundoSegundo

                    if (detalleSegundo != null && !detalleSegundo.trim().isEmpty()) {
                        String[] tmpDetalle = detalleSegundo.split("&")

                        for (String detalle : tmpDetalle) {
                            String[] tmpSegundo = detalle.split("\\|")

                            if (tmpSegundo.length > 2 && Integer.parseInt(tmpSegundo[2]) == etapa) {
                                String[] campos = new String[12]
                                campos[0] = tmpSegundo[0] ?: " " // Valor
                                campos[1] = tmpSegundo[1] ?: " " // Segundo
                                campos[2] = tmpSegundo[2] ?: " " // Etapa
                                campos[3] = tmpSegundo[3] ?: " " // Velocidad
                                campos[4] = tmpSegundo[4] ?: " " // rpm
                                campos[5] = tmpSegundo[5] ?: " " // tmpDinamometro
                                campos[6] = formatoCadena(tmpSegundo[6]) // Velocidad Lineal
                                campos[7] = formatoCadena(tmpSegundo[7]) // Velocidad Angular
                                campos[8] = formatoCadena(tmpSegundo[8]) // Potencia
                                campos[9] = formatoCadena(tmpSegundo[9]) // Fuerza
                                campos[10] = formatoCadena(tmpSegundo[10]) // Par Torcional
                                campos[11] = formatoCadena(tmpSegundo[11]) // Potencia W

                                resultado.add(campos)
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace()
        }

        // Asegurar que siempre haya 60 registros
        while (resultado.size() < 60) {
            resultado.add(new String[]{"---", "---", "---", "---", "---", "---", "---", "---", "---", "---", "---", "---"})
        }

        return resultado
    }

    /**
     * Recupera el valor de un índice específico en un arreglo, devolviendo null si el índice no es válido
     * @param arreglo String[] Arreglo del cual se desea recuperar el valor
     * @param indice int Índice del valor a recuperar
     * @return String Valor en el índice especificado o null si no es válido
     */
    static String recuperarValor(String[] arreglo, int indice) {
        return (arreglo != null && arreglo.length > indice && arreglo[indice] != null) ? arreglo[indice] : null
    }

    /**
     * Evalúa una cadena y devuelve un valor predeterminado si es nula o vacía
     * @param valor String Cadena a evaluar
     * @return String Cadena evaluada o valor predeterminado "---"
     */
    static String evaluaCadena(String valor) {
        return (valor == null || valor.equals(" ")) ? "---" : valor
    }

    /**
     * Redondea un valor decimal a un número específico de decimales
     * @param valor double Valor decimal a redondear
     * @param decimales int Número de decimales a los que redondear
     * @return double valor redondeado
     */
    static double redondearDecimales(double valor, int decimales) {
        double factor = Math.pow(10, decimales)
        return Math.round(valor * factor) / factor
    }

    /**
     * Formatea una cadena numérica con 2 decimales
     */
    static String formatoCadena(String cadena) {
        try {
            if (cadena != null && cadena.contains(".")) {
                return String.format("%.2f", Double.parseDouble(cadena)).replace(",", ".")
            } else {
                return cadena ?: "---"
            }
        } catch (Exception e) {
            return cadena ?: "---"
        }
    }

    /**
     * Genera una hoja en el excel con la información completa de la prueba OBD.
     * Datos generales de la prueba OBD, resultado de monitores obligatorios y resultado de monitores no obligatorios
     * @param archivo Objeto ArchivoXls donde se agregará la hoja con la información.
     * @param idVerificacion Identificador de la verificación vehicular. Se utiliza para consultar los datos OBD específicos.
     * @param idTipoHolograma Identificador del tipo de holograma
     */
    void generarHojaInformacionPruebaOBD(ArchivoXls archivo, Integer idVerificacion, Integer idTipoHolograma) {
        int[] anchoColumnas = [10, 3]
        archivo.modificaAnchoCeldas(anchoColumnas)
        Map datosVerificacionOBD = verificacionRepository.findVerificacionObdByVerificacion(idVerificacion)

        if(datosVerificacionOBD && !datosVerificacionOBD.isEmpty()){
            def respuestaVerificacionOBD = verificacionOBDService.procesarRespuestaPruebaOBD(datosVerificacionOBD, idTipoHolograma)

            datosGeneralesPruebaOBD(archivo, respuestaVerificacionOBD)
            monitoresObligatoriosOBD(archivo, respuestaVerificacionOBD)
            monitoresNoObligatoriosOBD(archivo, respuestaVerificacionOBD)
        } else{
            archivo.agregaFila()
            archivo.agregaColumna("No se encontró información de la prueba obd", "estilo_subtitulo", 1, 0)
        }
    }

    /**
     * Genera la tabla de resultado de los monitores no obligatorios de la prueba obd
     * @param archivo Objeto ArchivoXls donde se agregará la tabla con la información.
     * @param datosVerificacionOBD Map mapa con la información de la prueba obd
     */
    static void monitoresNoObligatoriosOBD(ArchivoXls archivo, Map datosVerificacionOBD) {

        if (datosVerificacionOBD != null) {
            archivo.agregaFila()
            archivo.agregaColumna("", null, 1, 0)

            archivo.agregaFila()
            archivo.agregaColumna("RESULTADO DE MONITORES NO OBLIGATORIOS", "estilo_titulo", 1, 0)

            archivo.agregaFila()
            archivo.agregaColumna("Sistema de Calentamiento de Convertidor Catalítico:", "estilo_subtitulo_izquierda")
            archivo.agregaColumna(datosVerificacionOBD.get("catalizadorSC") ?: " ", "estilo_contenido_centrado")

            archivo.agregaFila()
            archivo.agregaColumna("Sistema Evaporativo:", "estilo_subtitulo_izquierda")
            archivo.agregaColumna(datosVerificacionOBD.get("sistemaEvaporativo") ?: " ", "estilo_contenido_centrado")

            archivo.agregaFila()
            archivo.agregaColumna("Sistema Secundario de Aire:", "estilo_subtitulo_izquierda")
            archivo.agregaColumna(datosVerificacionOBD.get("sistemaSecundario") ?: " ", "estilo_contenido_centrado")

            archivo.agregaFila()
            archivo.agregaColumna("Sistema de Fugas de Aire Acondicionado: ", "estilo_subtitulo_izquierda")
            archivo.agregaColumna(datosVerificacionOBD.get("refrigeranteAC") ?: " ", "estilo_contenido_centrado")

            archivo.agregaFila()
            archivo.agregaColumna("Sistema de Calentamiento del Sensor de Oxigeno: ", "estilo_subtitulo_izquierda")
            archivo.agregaColumna(datosVerificacionOBD.get("sensorOxigenoSC") ?: " ", "estilo_contenido_centrado")

            archivo.agregaFila()
            archivo.agregaColumna("Sistema de Recirculación de los Gases de Escape (EGR): ", "estilo_subtitulo_izquierda")
            archivo.agregaColumna(datosVerificacionOBD.get("sistemaEGR") ?: " ", "estilo_contenido_centrado")

        }
    }

    /**
     * Genera la tabla de resultado de los monitores obligatorios de la prueba obd
     * @param archivo Objeto ArchivoXls donde se agregará la tabla con la información.
     * @param datosVerificacionOBD Map mapa con la información de la prueba obd
     */
    static void monitoresObligatoriosOBD(ArchivoXls archivo, Map datosVerificacionOBD) {

        if (datosVerificacionOBD != null) {

            archivo.agregaFila()
            archivo.agregaColumna("", null, 1, 0)

            archivo.agregaFila()
            archivo.agregaColumna("RESULTADO DE MONITORES OBLIGATORIOS", "estilo_titulo", 1, 0)
            archivo.agregaFila()
            archivo.agregaColumna("Sistema de Detección de Condiciones Inadecuadas de Ignición en Cilindros:", "estilo_subtitulo_izquierda")
            archivo.agregaColumna(datosVerificacionOBD.get("falloEncendido") ?: " ", "estilo_contenido_centrado")

            archivo.agregaFila()
            archivo.agregaColumna("Sistema de Eficiencia del Convertidor Catalítico:", "estilo_subtitulo_izquierda")
            archivo.agregaColumna(datosVerificacionOBD.get("catalizador") ?: " ", "estilo_contenido_centrado")

            archivo.agregaFila()
            archivo.agregaColumna("Sistema de Sensores de Oxigeno:", "estilo_subtitulo_izquierda")
            archivo.agregaColumna(datosVerificacionOBD.get("sensorOxigeno") ?: " ", "estilo_contenido_centrado")

            archivo.agregaFila()
            archivo.agregaColumna("Sistema de Componentes Integrales:", "estilo_subtitulo_izquierda")
            archivo.agregaColumna(datosVerificacionOBD.get("componentes") ?: " ", "estilo_contenido_centrado")

            archivo.agregaFila()
            archivo.agregaColumna("Sistema de Combustible:", "estilo_subtitulo_izquierda")
            archivo.agregaColumna(datosVerificacionOBD.get("sistemaCombustible"), "estilo_contenido_centrado")
        }
    }

    /**
     * Genera la tabla de los datos generales de la prueba OBD
     */
    /**
     * Genera la tabla de resultado de los datos generales de la prueba obd
     * @param archivo Objeto ArchivoXls donde se agregará la tabla con la información.
     * @param datosVerificacionOBD Map mapa con la información de la prueba obd
     */
    static void datosGeneralesPruebaOBD(ArchivoXls archivo, Map datosVerificacionOBD){

        if (datosVerificacionOBD != null) {
            archivo.agregaFila()
            archivo.agregaColumna("", null, 1, 0)

            archivo.agregaFila()
            archivo.agregaColumna("DATOS GENERALES DE LA PRUEBA OBD", "estilo_titulo",1, 0)
            archivo.agregaFila()
            archivo.agregaColumna("Folio de Prueba SDB:", "estilo_subtitulo_izquierda")
            archivo.agregaColumna(datosVerificacionOBD.get("folio") ?: "", "estilo_contenido_centrado")

            archivo.agregaFila()
            archivo.agregaColumna("Estado de la luz Check Engine:", "estilo_subtitulo_izquierda")
            archivo.agregaColumna(datosVerificacionOBD.get("mil") ?: " ", "estilo_contenido_centrado")

            archivo.agregaFila()
            archivo.agregaColumna("No. de DTC's:", "estilo_subtitulo_izquierda")
            archivo.agregaColumna(datosVerificacionOBD.get("dtc"), "estilo_contenido_centrado")

            archivo.agregaFila()
            archivo.agregaColumna("Tipo de OBD:", "estilo_subtitulo_izquierda")
            archivo.agregaColumna(datosVerificacionOBD.get("tipoOBD") ?: " ", "estilo_contenido_centrado")

            archivo.agregaFila()
            archivo.agregaColumna("Resultado:", "estilo_subtitulo_izquierda")
            archivo.agregaColumna(datosVerificacionOBD.get("descripcionResultado") ?: " ", "estilo_contenido_centrado")

            archivo.agregaFila()
            archivo.agregaColumna("Motivo:", "estilo_subtitulo_izquierda")
            archivo.agregaColumna(datosVerificacionOBD.get("resultadoOBD")  ?: " ", "estilo_contenido_centrado")
        }
    }
}