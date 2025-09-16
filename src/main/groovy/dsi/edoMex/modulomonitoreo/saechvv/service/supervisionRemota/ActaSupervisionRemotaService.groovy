package dsi.edoMex.modulomonitoreo.saechvv.service.supervisionRemota

import com.itextpdf.text.Chunk

import com.itextpdf.text.Element
import com.itextpdf.text.Font
import com.itextpdf.text.Image
import com.itextpdf.text.Paragraph

import dsi.edoMex.modulomonitoreo.modulomonitoreo.service.utilerias.ArchivoPdf
import dsi.edoMex.modulomonitoreo.modulomonitoreo.service.utilerias.GeneralService
import dsi.edoMex.modulomonitoreo.saechvv.entity.enums.EstatusVerificacion
import dsi.edoMex.modulomonitoreo.saechvv.repository.catalogo.VerificacionRepository
import dsi.edoMex.modulomonitoreo.saechvv.repository.supervisionRemota.AnomaliaMesaControlRepository
import dsi.edoMex.modulomonitoreo.saechvv.repository.supervisionRemota.SupervisionRemotaRepository
import dsi.edoMex.modulomonitoreo.saechvv.service.administracion.BitacoraService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Contiene funciones para la generación del acta de supervisión remota en formato PDF
 *
 * @author Refugio Rodríguez Bueno
 * @version 1.0 Agosto 2025
 */
@Transactional
@Service
class ActaSupervisionRemotaService {

    @Autowired
    VerificacionRepository verificacionRepository

    @Autowired
    AnomaliaMesaControlRepository anomaliaMesaControlRepository

    @Autowired
    SupervisionRemotaRepository supervisionRemotaRepository

    @Autowired
    BitacoraService bitacoraService

    @Autowired
    GeneralService generalService


    /**
     * Genera el acta de una supervisión remota con anomalías en formato pdf
     * @param claveAnomalia String clave de la anomalía registrada en la supervisión remota
     * @return ByteArrayOutputStream arreglo de bytes del documento pdf generado
     */
    def generarPdfSupervisionRemota(String claveAnomalia) {

        Map informacionVerificacion = verificacionRepository.findDatosActaSupervisionByClave(claveAnomalia)
        Integer idUsuario = informacionVerificacion?.IDUSUARIO as Integer ?: 0
        Integer idRegistro = informacionVerificacion?.IDVERIFICACION as Integer ?: 0
        String numeroPlaca = informacionVerificacion?.NO_PLACA ?: " "
        String numeroSerie = informacionVerificacion?.NO_SERIE ?: " "

        String referenciaDocumento = bitacoraService.registraBitacoraDocumento(idUsuario, 136, 4, "VERIFICACION", idRegistro, "-1", "2", "MSACTR", "Se genera reporte de Mesa de Control "+numeroPlaca, numeroPlaca, numeroSerie)

        try (ArchivoPdf pdf = new ArchivoPdf(true, "Acta de Supervisión Remota", true, true, referenciaDocumento)) {

            pdf.document.setMargins(20f, 20f, 10f, 20f)
            pdf.alturaCelda = 20f
            pdf.interlineado = 1.2f

            // PRIMERA PÁGINA - ENCABEZADO Y DATOS PRINCIPALES
            pdf.creaTabla([100f] as float[], 100f)

            pdf.creaCelda(8, "", false, Element.ALIGN_CENTER, 1)

            // INFORMACIÓN DEL CENTRO
            pdf.creaTablaSecundario([100f] as float[])
            String nomCentro = informacionVerificacion?.centro_nom ?: "N/A"
            pdf.creaCelda(2, nomCentro, false, Element.ALIGN_CENTER, 1)
            String razonSocial = informacionVerificacion?.razon_social ?: "N/A"
            pdf.creaCelda(3, razonSocial, false, Element.ALIGN_CENTER, 1)
            pdf.creaCelda(0, pdf.pdfPTableSecundario, true, Element.ALIGN_LEFT, 1)
            pdf.pdfPTableSecundario = null
            pdf.creaCelda(8, "", false, Element.ALIGN_CENTER, 1)

            // TABLA DE DATOS GNERALES
            pdf.creaTablaSecundario([25f, 25f, 25f, 25f] as float[])
            def datosVehiculo = [
                    ["No. de Línea:", informacionVerificacion?.no_linea, "No. de Placa:", informacionVerificacion?.no_placa],
                    ["Modelo:", informacionVerificacion?.modelo, "No. de Serie:", informacionVerificacion?.no_serie],
                    ["Tarjeta Circulación:", informacionVerificacion?.tarjeta, "Tipo de Servicio:", informacionVerificacion?.servicio],
                    ["Tipo de Anomalía:", informacionVerificacion?.tipoanomal ?: 'Sin anomalía', "Medida a Tomar:", informacionVerificacion?.tipo ?: 'Sin medida'],
                    ["Supervisor:", informacionVerificacion?.nomsupervisor ?: "", "", ""]
            ]
            def alternarColor = false
            datosVehiculo.each { fila ->
                if (fila[0] == "" && fila[2] == "") {
                    pdf.creaCelda(8, "-".repeat(12), false, Element.ALIGN_CENTER, 4)
                } else {
                    if (alternarColor) { pdf.colorCelda = true }
                    pdf.creaCelda(4, fila[0], false, Element.ALIGN_LEFT)
                    pdf.creaCelda(8, fila[1] ?: '', false, Element.ALIGN_LEFT)
                    pdf.colorCelda = false
                    if (alternarColor) { pdf.colorCelda = true }
                    pdf.creaCelda(4, fila[2], false, Element.ALIGN_LEFT)
                    pdf.creaCelda(8, fila[3] ?: '', false, Element.ALIGN_LEFT)
                    pdf.colorCelda = false
                    alternarColor = !alternarColor
                }
            }
            pdf.creaCelda(0, pdf.pdfPTableSecundario, true, Element.ALIGN_LEFT, 1)
            pdf.pdfPTableSecundario = null
            pdf.creaCelda(8, "", false, Element.ALIGN_CENTER, 1)

            // SUPERVISIÓN REMOTA
            pdf.creaTablaSecundario([100f] as float[])
            pdf.creaCelda(2, "S U P E R V I S I Ó N   R E M O T A", true, Element.ALIGN_CENTER, 1)
            pdf.creaCelda(1, informacionVerificacion?.CLAVE ?: "N/A", true, Element.ALIGN_CENTER, 1)
            pdf.creaCelda(0, pdf.pdfPTableSecundario, true, Element.ALIGN_LEFT, 1)
            pdf.pdfPTableSecundario = null
            pdf.creaCelda(8, "", false, Element.ALIGN_CENTER, 1)

            // DESCRIPCIÓN DE ANOMALÍA
            pdf.creaTablaSecundario([100f] as float[])
            pdf.creaCelda(4, "Descripción de Anomalía:", true, Element.ALIGN_LEFT, 1)
            String descripcionAnomalia = informacionVerificacion?.descripcion ?: 'Sin descripción de la anomalía'
            pdf.creaCelda(8, descripcionAnomalia, false, Element.ALIGN_JUSTIFIED, 1)
            pdf.creaCelda(0, pdf.pdfPTableSecundario, true, Element.ALIGN_LEFT, 1)
            pdf.pdfPTableSecundario = null
            pdf.document.add(pdf.pdfPTable)
            pdf.document.newPage()

            // DATOS DEl VERIFICENTRO
            pdf.creaTabla([100f] as float[], 100f)
            pdf.agregarEspacioEntreTablas(pdf, 10f)
            pdf.creaTablaSecundario([25f, 25f, 25f, 25f] as float[])
            pdf.creaCelda(3, "DATOS DEL VERIFICENTRO", true, Element.ALIGN_CENTER, 4)
            def estatus

            def datosVerificentro = [
                    ["Verificentro:", "${informacionVerificacion?.centro_nom ?: 'N/A'} ${informacionVerificacion?.razon_social ?: 'N/A'}", "Equipo de Verificación:", informacionVerificacion?.equipover ?: 'N/A'],
                    ["No. Línea:", informacionVerificacion?.no_linea ?: 'N/A', "Tipo Línea:", informacionVerificacion?.tipolinea ?: 'N/A'],
                    ["Técnico:", informacionVerificacion?.nomtecnico ?: 'N/A', "", ""]
            ]
            alternarColor = false
            datosVerificentro.each { fila ->
                if (alternarColor) { pdf.colorCelda = true }
                pdf.creaCelda(4, fila[0], false, Element.ALIGN_LEFT)
                pdf.creaCelda(8, fila[1], false, Element.ALIGN_LEFT)
                pdf.colorCelda = false
                if (alternarColor) { pdf.colorCelda = true }
                pdf.creaCelda(4, fila[2], false, Element.ALIGN_LEFT)
                pdf.creaCelda(8, fila[3], false, Element.ALIGN_LEFT)
                pdf.colorCelda = false
                alternarColor = !alternarColor
            }
            pdf.creaCelda(0, pdf.pdfPTableSecundario, true, Element.ALIGN_LEFT, 1)
            pdf.pdfPTableSecundario = null
            pdf.creaCelda(8, "", false, Element.ALIGN_CENTER, 1)

            // DATOS DEL VEHÍCULO
            pdf.creaTablaSecundario([25f, 25f, 25f, 25f] as float[])
            pdf.creaCelda(3, "DATOS DEL VEHÍCULO", true, Element.ALIGN_CENTER, 4)
            def datosVehiculoDetallado = [
                    ["No. Placa:", informacionVerificacion?.no_placa ?: 'N/A', "No. Serie:", informacionVerificacion?.no_serie ?: 'N/A'],
                    ["Marca:", informacionVerificacion?.marca ?: 'N/A', "Submarca:", informacionVerificacion?.submarca ?: 'N/A'],
                    ["Modelo:", informacionVerificacion?.modelo ?: 'N/A', "Servicio:", informacionVerificacion?.servicio ?: 'N/A'],
                    ["Tarjeta de Circulación:", informacionVerificacion?.tarjeta ?: 'N/A', "Fecha de Factura:", informacionVerificacion?.fechafac ?: 'N/A'],
                    ["No. de Motor:", informacionVerificacion?.no_motor ?: 'N/A', "Entidad Placas:", informacionVerificacion?.entidad ?: 'N/A'],
            ]
            alternarColor = false
            datosVehiculoDetallado.each { fila ->
                if (alternarColor) { pdf.colorCelda = true }
                pdf.creaCelda(4, fila[0], false, Element.ALIGN_LEFT)
                pdf.creaCelda(8, fila[1] ?: 'N/A', false, Element.ALIGN_LEFT)
                pdf.colorCelda = false
                if (alternarColor) { pdf.colorCelda = true }
                pdf.creaCelda(4, fila[2], false, Element.ALIGN_LEFT)
                pdf.creaCelda(8, fila[3] ?: 'N/A', false, Element.ALIGN_LEFT)
                pdf.colorCelda = false
                alternarColor = !alternarColor
            }
            pdf.creaCelda(0, pdf.pdfPTableSecundario, true, Element.ALIGN_LEFT, 1)
            pdf.pdfPTableSecundario = null
            pdf.creaCelda(8, "", false, Element.ALIGN_CENTER, 1)

            // DATOS DE LA VERIFICACIÓN
            pdf.creaTablaSecundario([25f, 25f, 25f, 25f] as float[])
            pdf.creaCelda(3, "DATOS DE LA VERIFICACIÓN", true, Element.ALIGN_CENTER, 4)
            def datosVerificacion = [
                    ["No. Verificación:", informacionVerificacion?.idverificacion ?: 'N/A', "Estatus:", EstatusVerificacion.get(informacionVerificacion?.ESTATUS as Integer) ?: 'N/A'],
                    ["Folio Anterior:", informacionVerificacion?.folio_anterior ?: 'N/A', "Folio Multa:", informacionVerificacion?.folio_multa ?: 'N/A'],
                    ["Fecha Inicio:", informacionVerificacion?.fecha_registro ?: 'N/A', "Fecha Término:", informacionVerificacion?.fechafin ?: 'N/A'],
                    ["Fecha Ini. Prueba:", informacionVerificacion?.fecha_ini_prueba ?: 'N/A', "Fecha Fin. Prueba:", informacionVerificacion?.fecha_fin_prueba ?: 'N/A'],
                    ["Holograma Solicitado:", informacionVerificacion?.tipoholoini ?: 'N/A', "Holograma Obtenido:", informacionVerificacion?.tipoholofin ?: 'N/A'],
                    ["Motivo Verificación:", informacionVerificacion?.motivo ?: 'N/A', "Folio Obtenido:", informacionVerificacion?.folio ?: 'N/A'],
                    ["Resultado:", informacionVerificacion?.resultado ?: 'N/A', "Tipo Rechazo:", informacionVerificacion?.tipo_rechazo ?: 'N/A']
            ]
            alternarColor = false
            datosVerificacion.each { fila ->
                if (alternarColor) { pdf.colorCelda = true }
                pdf.creaCelda(4, fila[0], false, Element.ALIGN_LEFT)
                pdf.creaCelda(8, fila[1] ?: 'N/A', false, Element.ALIGN_LEFT)
                pdf.colorCelda = false
                if (alternarColor) { pdf.colorCelda = true }
                pdf.creaCelda(4, fila[2], false, Element.ALIGN_LEFT)
                pdf.creaCelda(8, fila[3] ?: 'N/A', false, Element.ALIGN_LEFT)
                pdf.colorCelda = false
                alternarColor = !alternarColor
            }
            pdf.creaCelda(0, pdf.pdfPTableSecundario, true, Element.ALIGN_LEFT, 1)
            pdf.pdfPTableSecundario = null
            pdf.document.add(pdf.pdfPTable)
            pdf.document.newPage()

            if (informacionVerificacion?.COMBUSTIBLE as Integer == 2) {
                generarDetalleDiesel(pdf, informacionVerificacion)

            } else {
                generarDetalleGasolina(pdf, informacionVerificacion)
            }
            pdf.agregarEspacioEntreTablas(pdf, 15f)
            generarTablaAdicionales(pdf, informacionVerificacion)

            pdf.agregarEspacioEntreTablas(pdf, 15f)
            generarDocumentacion(pdf, informacionVerificacion?.idverificacion as Integer)
            return pdf.baos
        } catch (Exception e) {
            e.printStackTrace()
            return null
        }
    }

    /**
     * Genera la tabla con los datos de emisón de una verificación de tipo diésel
     * @param pdf ArchivPdf donde se agregará la tabla
     * @param datosVerificacion Map mapa con los datos de la verificación
     */
    void generarDetalleDiesel(ArchivoPdf pdf, Map datosVerificacion) {
        // DETALLE DE LA VERIFICACIÓN - DIESEL
        int col = 3
        pdf.creaTabla([40f, 30f, 30f] as float[], 90f)
        pdf.creaCelda(3, "DETALLE DE LA VERIFICACIÓN - DIESEL", false, Element.ALIGN_CENTER, col)

        pdf.colorCelda = true
        pdf.creaCelda(3, "Factor", true, Element.ALIGN_CENTER)
        pdf.creaCelda(3, "Emisión", true, Element.ALIGN_CENTER)
        pdf.creaCelda(3, "Límite", true, Element.ALIGN_CENTER)
        pdf.colorCelda = false

        def parametrosDiesel = [
                ["CAL", datosVerificacion?.CAL, "<= ${formatoNumero(datosVerificacion?.EVALCAL as String)}"],
                ["Temperatura", datosVerificacion?.TEM_CAM, "-"],
                ["Opacidad", datosVerificacion?.OPACIDAD, "-"],
                ["RPM", datosVerificacion?.POTMAX_RPM, "-"],
        ]
        parametrosDiesel.each { param ->
            pdf.creaCelda(3, param[0], true, Element.ALIGN_LEFT)
            pdf.creaCelda(8, formatoNumero(param[1] as String), true, Element.ALIGN_CENTER)
            pdf.creaCelda(8, param[2], true, Element.ALIGN_CENTER)
        }
        pdf.document.add(pdf.pdfPTable)
    }

    /**
     * Genera la tabla con los datos de emisón de una verificación de tipo gasolina
     * @param pdf ArchivoPdf donde se agregará la tabla
     * @param datosVerificacion Map mapa con los datos de la verificación
     */
    void generarDetalleGasolina(ArchivoPdf pdf, Map datosVerificacion) {
        pdf.agregarEspacioEntreTablas(pdf, 10f)
        // DETALLE DE LA VERIFICACIÓN - GASOLINA
        int columna = 4
        if (datosVerificacion?.TIPOPRUEBA != "1") { // TIPOPRUEBA
            columna++
        }
        if (datosVerificacion?.ID_PARAM_PIREC != 0) { // ID_PARAM_PIREC
            columna++
        }

        float[] anchoColumnas
        if (columna == 5) {
            anchoColumnas = [24f, 17f, 17f, 23f, 19f] as float[]
        } else if (columna == 6) {
            anchoColumnas = [23f, 14f, 14f, 20f, 15f, 14f] as float[]
        } else {
            anchoColumnas = [30f, 22f, 22f, 26f] as float[]
        }
        pdf.creaTabla(anchoColumnas, 90f)

        // Encabezados
        pdf.colorCelda = true
        pdf.creaCelda(3, "Parámetros", true, Element.ALIGN_CENTER)
        pdf.creaCelda(3, "Relentí", true, Element.ALIGN_CENTER)
        pdf.creaCelda(3, "Crucero", true, Element.ALIGN_CENTER)
        pdf.creaCelda(3, "Límites", true, Element.ALIGN_CENTER)

        if (columna > 4) {
            pdf.creaCelda(3, "P. Estática", true, Element.ALIGN_CENTER)
        }
        if (columna > 5) {
            pdf.creaCelda(3, "P. Pirec", true, Element.ALIGN_CENTER)
        }
        pdf.colorCelda = false

        pdf.creaCelda(3, "HC (ppm):", true, Element.ALIGN_LEFT)
        pdf.creaCelda(8, formatoValor(datosVerificacion?.HC_5024), true, Element.ALIGN_CENTER)
        pdf.creaCelda(8, formatoValor(datosVerificacion?.HC_2540), true, Element.ALIGN_CENTER)
        pdf.creaCelda(8, "<= ${formatoValor(datosVerificacion?.EVALHC)}", true, Element.ALIGN_CENTER)

        if (columna > 4) {
            pdf.creaCelda(8, "", true, Element.ALIGN_CENTER)
        }
        if (columna > 5) {
            pdf.creaCelda(8, "", true, Element.ALIGN_CENTER)
        }

        pdf.creaCelda(3, "CO %:", true, Element.ALIGN_LEFT)
        pdf.creaCelda(8, formatoValor(datosVerificacion?.CO2_5024), true, Element.ALIGN_CENTER) //En el original tenía así
        pdf.creaCelda(8, formatoValor(datosVerificacion?.CO_2540), true, Element.ALIGN_CENTER)
        pdf.creaCelda(8, "<= ${formatoValor(datosVerificacion?.EVALCO)}", true, Element.ALIGN_CENTER)

        if (columna > 4) {
            pdf.creaCelda(8, "", true, Element.ALIGN_CENTER)
        }
        if (columna > 5) {
            pdf.creaCelda(8, "<= ${formatoValor(datosVerificacion?.EVALPIRCO)}", true, Element.ALIGN_CENTER)
        }

        pdf.creaCelda(3, "CO2 %:", true, Element.ALIGN_LEFT)
        pdf.creaCelda(8, formatoValor(datosVerificacion?.CO2_5024_B), true, Element.ALIGN_CENTER)
        pdf.creaCelda(8, formatoValor(datosVerificacion?.CO2_2540_B), true, Element.ALIGN_CENTER)
        pdf.creaCelda(8, "", true, Element.ALIGN_CENTER)

        if (columna > 4) {
            pdf.creaCelda(8, "", true, Element.ALIGN_CENTER)
        }
        if (columna > 5) {
            pdf.creaCelda(8, ">= ${formatoValor(datosVerificacion?.EVALPIRCO2)}", true, Element.ALIGN_CENTER)
        }

        pdf.creaCelda(3, "CO + CO2 %:", true, Element.ALIGN_LEFT)
        pdf.creaCelda(8, formatoValor(datosVerificacion?.COCO2_5024), true, Element.ALIGN_CENTER)
        pdf.creaCelda(8, formatoValor(datosVerificacion?.COCO2_2540), true, Element.ALIGN_CENTER)
        pdf.creaCelda(8, "Min: >= ${formatoValor(datosVerificacion?.EVALCOCO2INI)}    Máx: <= ${formatoValor(datosVerificacion?.EVALCOCO2FIN)}",
                true, Element.ALIGN_CENTER)

        if (columna > 4) {
            pdf.creaCelda(8, "", true, Element.ALIGN_CENTER)
        }
        if (columna > 5) {
            pdf.creaCelda(8, "", true, Element.ALIGN_CENTER)
        }

        pdf.creaCelda(3, "O2 %:", true, Element.ALIGN_LEFT)
        pdf.creaCelda(8, formatoValor(datosVerificacion?.O2_5024_B), true, Element.ALIGN_CENTER)
        pdf.creaCelda(8, formatoValor(datosVerificacion?.O2_2540_B), true, Element.ALIGN_CENTER)
        pdf.creaCelda(8, "<= ${formatoValor(datosVerificacion?.EVALO2)}", true, Element.ALIGN_CENTER)

        if (columna > 4) {
            pdf.creaCelda(8, "<= ${formatoValor(datosVerificacion?.EVALO2EST)}", true, Element.ALIGN_CENTER)
        }
        if (columna > 5) {
            pdf.creaCelda(8, "< ${formatoValor(datosVerificacion?.EVALPIRO2)}", true, Element.ALIGN_CENTER)
        }

        pdf.creaCelda(3, "NO (ppm):", true, Element.ALIGN_LEFT)
        pdf.creaCelda(8, formatoValor(datosVerificacion?.NO_5024), true, Element.ALIGN_CENTER)
        pdf.creaCelda(8, formatoValor(datosVerificacion?.NO_2540), true, Element.ALIGN_CENTER)
        pdf.creaCelda(8, "<= ${formatoValor(datosVerificacion?.EVALNOX)}", true, Element.ALIGN_CENTER)

        if (columna > 4) {
            pdf.creaCelda(8, "", true, Element.ALIGN_CENTER)
        }
        if (columna > 5) {
            pdf.creaCelda(8, "", true, Element.ALIGN_CENTER)
        }

        pdf.creaCelda(3, "LAMBDA:", true, Element.ALIGN_LEFT)
        pdf.creaCelda(8, formatoValor(datosVerificacion?.LAMDA_5024), true, Element.ALIGN_CENTER)
        pdf.creaCelda(8, formatoValor(datosVerificacion?.LAMDA_2540), true, Element.ALIGN_CENTER)
        pdf.creaCelda(8, "<= ${formatoValor(datosVerificacion?.EVAL_LAMDA)}", true, Element.ALIGN_CENTER)

        if (columna > 4) {
            pdf.creaCelda(8, "<= ${formatoValor(datosVerificacion?.EVAL_LAMDAEST)}", true, Element.ALIGN_CENTER)
        }
        if (columna > 5) {
            pdf.creaCelda(8, "", false, Element.ALIGN_CENTER)
        }

        double HC_5024 = (datosVerificacion?.HC_5024_B as Double ?: 0.0) * 0.0006
        double CO_5024 = datosVerificacion?.CO_5024_B as Double ?: 0.0
        double NO_5024 = (datosVerificacion?.NOX_5024_B as Double ?: 0.0) / 10000
        double CO2_5024 = datosVerificacion?.CO2_5024_B as Double ?: 0.0
        double O2_5024 = datosVerificacion?.O2_5024_B as Double ?: 0.0

        double aux1_5024 = CO2_5024 + (CO_5024 / 2) + (NO_5024 / 2) + O2_5024
        double aux2_5024 = CO2_5024 + CO_5024
        double aux3_5024 = (CO2_5024 != 0) ? (3.5 / (3.5 + (CO_5024 / CO2_5024))) * 0.45425 : 0.0
        aux2_5024 = aux3_5024 * aux2_5024
        aux1_5024 = aux1_5024 + aux2_5024
        double aux4_5024 = 1.45425 * (CO2_5024 + CO_5024 + HC_5024)
        double lamda_c1 = (aux4_5024 != 0) ? aux1_5024 / aux4_5024 : 0.0

        double HC_2540 = (datosVerificacion?.HC_2540_B as Double ?: 0.0) * 0.0006
        double CO_2540 = datosVerificacion?.CO_2540_B as Double ?: 0.0
        double NO_2540 = (datosVerificacion?.NOX_2540_B as Double ?: 0.0) / 10000
        double CO2_2540 = datosVerificacion?.CO2_2540_B as Double ?: 0.0
        double O2_2540 = datosVerificacion?.O2_2540_B as Double ?: 0.0

        double aux1_2540 = CO2_2540 + (CO_2540 / 2) + (NO_2540 / 2) + O2_2540
        double aux2_2540 = CO2_2540 + CO_2540
        double aux3_2540 = (CO2_2540 != 0) ? (3.5 / (3.5 + (CO_2540 / CO2_2540))) * 0.45425 : 0.0
        aux2_2540 = aux3_2540 * aux2_2540
        aux1_2540 = aux1_2540 + aux2_2540
        double aux4_2540 = 1.45425 * (CO2_2540 + CO_2540 + HC_2540)
        double lamda_c2 = (aux4_2540 != 0) ? aux1_2540 / aux4_2540 : 0.0

        pdf.creaCelda(3, "CALCULO LAMBDA:", true, Element.ALIGN_LEFT)
        pdf.creaCelda(8, formatoValor(String.valueOf(lamda_c2).take(7)), true, Element.ALIGN_CENTER)
        pdf.creaCelda(8, formatoValor(String.valueOf(lamda_c1).take(7)), true, Element.ALIGN_CENTER)
        pdf.creaCelda(8, "", true, Element.ALIGN_CENTER)

        if (columna > 4) {
            pdf.creaCelda(8, "", true, Element.ALIGN_CENTER)
        }
        if (columna > 5) {
            pdf.creaCelda(8, "", true, Element.ALIGN_CENTER)
        }

        pdf.creaCelda(3, "K/H:", true, Element.ALIGN_LEFT)
        pdf.creaCelda(8, formatoValor(datosVerificacion?.KPH_5024), true, Element.ALIGN_CENTER)
        pdf.creaCelda(8, formatoValor(datosVerificacion?.KPH_2540), true, Element.ALIGN_CENTER)
        pdf.creaCelda(8, "", false, Element.ALIGN_CENTER)

        if (columna > 4) {
            pdf.creaCelda(8, "", true, Element.ALIGN_CENTER)
        }
        if (columna > 5) {
            pdf.creaCelda(8, "", true, Element.ALIGN_CENTER)
        }

        pdf.creaCelda(3, "Carga HP:", true, Element.ALIGN_LEFT)
        pdf.creaCelda(8, formatoValor(datosVerificacion?.POT_5024), true, Element.ALIGN_CENTER)
        pdf.creaCelda(8, formatoValor(datosVerificacion?.POT_2540), true, Element.ALIGN_CENTER)
        pdf.creaCelda(8, "", true, Element.ALIGN_CENTER)

        if (columna > 4) {
            pdf.creaCelda(8, "", true, Element.ALIGN_CENTER)
        }
        if (columna > 5) {
            pdf.creaCelda(8, "", true, Element.ALIGN_CENTER)
        }
        pdf.creaCelda(3, "\n\n\n ", false, Element.ALIGN_CENTER)
        pdf.document.add(pdf.pdfPTable)

        pdf.agregarEspacioEntreTablas(pdf, 15f)
        generarDetalleEmisiones(pdf, datosVerificacion)

        pdf.agregarEspacioEntreTablas(pdf, 15f)
        generarFactoresEmisiones(pdf, datosVerificacion)

        pdf.agregarEspacioEntreTablas(pdf, 15f)

    }

    /**
     * Genera una tabla en el PDF con el detalle de emisiones de gases para una verificación.
     *
     * @param pdf instancia de {@link ArchivoPdf} donde se agregará la tabla
     * @param datosVerificacion Map mapa con los valores de emisiones de la verificación
     */
    void generarDetalleEmisiones(ArchivoPdf pdf, Map datosVerificacion) {
        pdf.creaTabla([20f, 20f, 20f, 20f, 20f] as float[], 90f)

        // Encabezados
        pdf.colorCelda = true
        pdf.creaCelda(3, "", true, Element.ALIGN_CENTER)
        pdf.creaCelda(3, "Emisiones 5024", true, Element.ALIGN_CENTER, 2)
        pdf.creaCelda(3, "Emisiones 2540", true, Element.ALIGN_CENTER, 2)
        pdf.colorCelda = false

        pdf.colorCelda = true
        pdf.creaCelda(3, "Emisión de Gases", true, Element.ALIGN_CENTER)
        pdf.creaCelda(3, "Valores Bruto", true, Element.ALIGN_CENTER)
        pdf.creaCelda(3, "Valores Corregidos", true, Element.ALIGN_CENTER)
        pdf.creaCelda(3, "Valores Bruto", true, Element.ALIGN_CENTER)
        pdf.creaCelda(3, "Valores Corregidos", true, Element.ALIGN_CENTER)
        pdf.colorCelda = false

        def emisiones = [
                ["HC ppm", "HC_5024_B", "HC_2540_B", "HC_5024","HC_2540" ],
                ["CO %", "CO_5024_B", "CO_2540_B",  "CO_5024", "CO_2540" ],
                ["CO2 %", "CO2_5024_B","CO2_2540_B" , "CO2_5024", "CO2_2540"],
                ["O2 %","O2_5024_B" ,"O2_2540_B" ,"O2_5024" , "O2_2540"],
                ["NO ppm", "NOX_5024_B", "NOX_2540_B", "NO_5024", "NO_2540"]
        ]

        emisiones.each { emision ->
            pdf.creaCelda(3, emision[0] + ":", true, Element.ALIGN_LEFT)
            pdf.creaCelda(8, formatoValor(datosVerificacion[emision[1]]), true, Element.ALIGN_CENTER)
            pdf.creaCelda(8, formatoValor(datosVerificacion[emision[2]]), true, Element.ALIGN_CENTER)
            pdf.creaCelda(8, formatoValor(datosVerificacion[emision[3]]), true, Element.ALIGN_CENTER)
            pdf.creaCelda(8, formatoValor(datosVerificacion[emision[4]]), true, Element.ALIGN_CENTER)
        }
        pdf.document.add(pdf.pdfPTable)
    }

    /**
     * Genera una tabla en el PDF con los factores de emisión para una verificación.
     *
     * @param pdf instancia de {@link ArchivoPdf} donde se agregará la tabla
     * @param datos Map mapa con los valores de factores de emisión de la verificación
     */
    void generarFactoresEmisiones(ArchivoPdf pdf, Map datos) {
        pdf.creaCelda(3, "\n", false, Element.ALIGN_CENTER)
        pdf.creaTabla([50f, 25f, 25f] as float[], 90f)
        // Encabezados
        pdf.colorCelda = true
        pdf.creaCelda(3, "Factor", true, Element.ALIGN_CENTER)
        pdf.creaCelda(3, "Emisiones 5024", true, Element.ALIGN_CENTER)
        pdf.creaCelda(3, "Emisiones 2540", true, Element.ALIGN_CENTER)
        pdf.colorCelda = false

        def factores = [
                ["Lambda:", "LAMDA_5024", "LAMDA_2540"],
                ["CO + CO2 %:", "COCO2_5024", "COCO2_2540"],
                ["Presión (PSI):", "PSI_5024", "PSI_2540"],
                ["Revoluciones por Minuto (RPM):", "RPM_5024", "RPM_2540"],
                ["Kilometros sobre Hora (K/H):", "KPH_5024", "KPH_2540"],
                ["Volts:", "VOLTS_5024", "VOLTS_2540"],
                ["Humedad Relativa:", "HR_5024", "HR_2540"],
                ["Temperatura:", "TEMP_5024", "TEMP_2540"],
                ["Factor de Correción NO:", "FCNOX_5024", "FCNOX_2540"],
                ["Factor de Correción Dilución:", "FCDIL_5024", "FCDIL_2540"],
                ["Eficiencia del Convertidor Catalítico:", "EFIC_5024", "EFIC_2540"]
        ]

        factores.each { factor ->
            pdf.creaCelda(3, factor[0], true, Element.ALIGN_LEFT)
            pdf.creaCelda(8, formatoValor(datos[factor[1]]), true, Element.ALIGN_CENTER)
            pdf.creaCelda(8, formatoValor(datos[factor[2]]), true, Element.ALIGN_CENTER)
        }
        pdf.creaCelda(3, "\n\n\n", false, Element.ALIGN_CENTER)
        pdf.document.add(pdf.pdfPTable)
    }

    /**
     * Formatea un valor numérico para asegurarse de que tenga un formato adecuado.
     * Si el valor es nulo o una cadena vacía, devuelve "0.0".
     * @param valor String valor a formatear
     * @return String el valor formateado como cadena
     */
    static String formatoValor(String valor) {
        if (valor == null || valor == "") return "0.0"
        def valorFormateado = valor.toString()
        if (valorFormateado.length() > 1 && valorFormateado.charAt(0) == '.') {
            return "0" + valorFormateado
        }
        return valorFormateado
    }


    /**
     * Genera una tabla en el PDF con datos adicionales de la verificación.
     *
     * @param pdf instancia de {@link ArchivoPdf} donde se agregará la tabla
     * @param datos Map mapa con los valores adicionales de la verificación
     */
    void generarTablaAdicionales(ArchivoPdf pdf, Map datos) {
        pdf.creaCelda(3, "\n\n", false, Element.ALIGN_CENTER)
        pdf.creaTabla([26f, 7f, 26f, 8f, 26f, 7f] as float[], 90f)
        pdf.creaCelda(3, "Temperatura de Motor", true, Element.ALIGN_LEFT)
        pdf.creaCelda(8, formatoNumero(datos?.TEMP_MOT as String), true, Element.ALIGN_CENTER)
        pdf.creaCelda(3, "Temperatura de Cámara", true, Element.ALIGN_LEFT)
        pdf.creaCelda(8, formatoNumero(datos?.TEM_CAM as String), true, Element.ALIGN_CENTER)
        pdf.creaCelda(3, "Temperatura de Gas", true, Element.ALIGN_LEFT)
        pdf.creaCelda(8, formatoNumero(datos?.TEM_GAS as String), true, Element.ALIGN_CENTER)
        pdf.creaCelda(3, "Velocidad del Gobernador", true, Element.ALIGN_LEFT)
        pdf.creaCelda(8, formatoNumero(datos?.VEL_GOB as String), true, Element.ALIGN_CENTER)
        pdf.creaCelda(3, "Corte de Gobernador", true, Element.ALIGN_LEFT)
        pdf.creaCelda(8, formatoNumero(datos?.GOBERNADOR as String), true, Element.ALIGN_CENTER)
        pdf.creaCelda(3, "Presión del Gas", true, Element.ALIGN_LEFT)
        pdf.creaCelda(8, formatoNumero(datos?.PRES_GAS as String), true, Element.ALIGN_CENTER)
        pdf.document.add(pdf.pdfPTable)
    }

    /**
     * Genera la sección de documentación en el PDF, incluyendo imágenes y descripciones.
     *
     * @param pdf instancia de {@link ArchivoPdf} donde se agregará la sección de documentación
     * @param idVerificacion Integer ID de la verificación para obtener los documentos asociados
     */
    void generarDocumentacion(ArchivoPdf pdf, Integer idVerificacion) {
        def documentosVerificacion = verificacionRepository.getDocumentosVerificacion(idVerificacion)
        if (!documentosVerificacion) {
            pdf.creaTabla([100f] as float[], 100f)
            pdf.creaCelda(3, "DOCUMENTACIÓN", false, Element.ALIGN_CENTER, 1)
            pdf.creaCelda(8, "No hay documentación disponible", false, Element.ALIGN_CENTER, 1)
            pdf.document.add(pdf.pdfPTable)
            return
        }
        pdf.creaTabla([50f, 50f] as float[], 100f)
        pdf.creaCelda(3, "DOCUMENTACIÓN", false, Element.ALIGN_CENTER, 2)
        int contador = 0
        for (Map datosDocumento : documentosVerificacion) {
            try {
                String rutaImagen = datosDocumento.get("ruta") as String
                String descripcion = datosDocumento.get("descripcion") as String
                if (rutaImagen == null || rutaImagen == "-1" || rutaImagen.trim().isEmpty()) {
                    crearCeldaImagenNoEcontrada(pdf, descripcion)
                } else {
                    pdf.creaTablaSecundario([100f] as float[])
                    try {
                        Image imagen = Image.getInstance(rutaImagen)
                        imagen.scaleAbsolute(200f, 150f)
                        imagen.setAlignment(Image.MIDDLE)
                        pdf.creaCelda(0, imagen, false, Element.ALIGN_CENTER, 1)
                        pdf.creaCelda(8, descripcion, false, Element.ALIGN_CENTER, 1)
                    } catch (Exception e) {
                        crearCeldaImagenNoEcontrada(pdf, descripcion)
                    }
                    pdf.creaCelda(0, pdf.pdfPTableSecundario, false, Element.ALIGN_CENTER, 1)
                    pdf.pdfPTableSecundario = null
                }
                contador++
                if (contador % 2 == 0) {
                    pdf.creaCelda(4, "", false, Element.ALIGN_CENTER, 2)
                }
            } catch (Exception e) {
                e.printStackTrace()
                crearCeldaImagenNoEcontrada(pdf, "Error al cargar imagen")
            }
        }
        if (contador % 2 != 0) {
            pdf.creaCelda(0, new Paragraph(""), false, Element.ALIGN_CENTER, 1)
        }
        pdf.document.add(pdf.pdfPTable)
    }

    /**
     * Crea una celda en el PDF indicando que la imagen no está disponible.
     *
     * @param pdf instancia de {@link ArchivoPdf} donde se agregará la celda
     * @param descripcion String descripción asociada a la imagen no encontrada
     */
    static void crearCeldaImagenNoEcontrada(ArchivoPdf pdf, String descripcion) {
        pdf.creaTablaSecundario([100f] as float[])
        Paragraph placeholder = new Paragraph()
        placeholder.add(new Chunk("Imagen no disponible\n", new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC)))
        placeholder.add(new Chunk(descripcion, new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL)))
        pdf.creaCelda(0, placeholder, true, Element.ALIGN_CENTER, 1)
        pdf.creaCelda(0, pdf.pdfPTableSecundario, false, Element.ALIGN_CENTER, 1)
        pdf.pdfPTableSecundario = null
    }

    /**
     * Formatea un número representado como cadena a un formato con dos decimales.
     * Si la conversión falla, devuelve el valor original como cadena.
     *
     * @param valor String valor numérico a formatear
     * @return String valor formateado con dos decimales o el valor original en caso de error
     */
    static String formatoNumero(String valor) {
        if (valor == null) return "0.00"
        try {
            def numero = valor as double
            return String.format("%.2f", numero)
        } catch (Exception excepcion) {
            return valor.toString()
        }
    }
}