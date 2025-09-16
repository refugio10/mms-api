package dsi.edoMex.modulomonitoreo.modulomonitoreo.service.utilerias

import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.Font
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.RichTextString
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.util.CellRangeAddress

/**
 * Contiene los atributos y métodos genéricos para crear un archivo EXCEL
 *
 * @author lorenav
 * @version 06/04/2022
 */
class ArchivoXls {
    Workbook workbook
    int fila = -1
    int columna = 0
    int incrementoFila = 1
    int posicionInicialColumna = 0
    Map<String, CellStyle> listaEstilos = null
    Sheet sheet = null

    /**
     * Constructor de clase
     *
     * @param nombreHoja Cadena con el nombre de la hoja excel
     */
    ArchivoXls(String nombreHoja) {
        this.workbook = new HSSFWorkbook()
        crearEstilos()
        agregaHoja(nombreHoja)
    }

    /**
     * Agrega una nueva hoja al archivo excel
     *
     * @param nombreHoja Cadena con el nombre de la hoja excel
     */
    void agregaHoja(String nombreHoja) {
        this.sheet = this.workbook.createSheet(nombreHoja)
        this.fila = -1
        agregaFila()
    }

    /**
     * Agrega una nueva fila al archivo excel
     */
    void agregaFila() {
        this.fila += this.incrementoFila
        this.columna = posicionInicialColumna
        this.sheet.createRow(this.fila)
    }

    /**
     * Agrega una nueva celda al archivo excel
     *
     * @param contenido Contenido de la celda (espera un objeto de diferente clase)
     * @param estilo Cadena con el nombre del estilo
     * @param rangoColumna Entero con el rango de columnas a juntar
     * @param rangoFila Entero con el rango de filas a juntar
     */
    void agregaColumna(Object contenido, String estilo, int rangoColumna = 0, int rangoFila = 0) {
        try {
            Cell celda = this.sheet.getRow(fila).createCell(columna)

            if (rangoColumna > 0 | rangoFila > 0)
                sheet.addMergedRegion(new CellRangeAddress(fila, (fila + rangoFila), columna, (columna + rangoColumna)))

            columna = columna + rangoColumna + 1

            if (estilo != null)
                celda.setCellStyle(recuperaEstilo(estilo))

            if (contenido instanceof RichTextString) {
                celda.setCellValue((RichTextString) contenido)
            } else {
                celda.setCellValue(contenido as String)
            }
        } catch (Exception exception) {
            exception.printStackTrace()
        }
    }

    /**
     * Modifica el ancho de la celda
     *
     * @param anchoCeldas Arreglo de enteros con el tamaño a incrementar
     */
    void modificaAnchoCeldas(int[] anchoCeldas) {
        for (int i = 0; i < anchoCeldas.length; i++)
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) * anchoCeldas[i])
    }

    /**
     * Obtiene un estilo que se a agregado en la lista de estilos
     *
     * @param nombreEstilo Cadena con el nombre del estilo
     * @return Objeto de clase CellStyle
     */
    private CellStyle recuperaEstilo(String nombreEstilo) {
        CellStyle cellStyle = null
        for (Map.Entry<String, CellStyle> estiloMap : this.listaEstilos.entrySet()) {
            if (estiloMap.getKey().contains(nombreEstilo)) {
                cellStyle = estiloMap.getValue()
                break
            }
        }
        return cellStyle
    }

    /**
     * Crea una lista de estilos para el archivo excel
     */
    private void crearEstilos() {
        this.listaEstilos = new HashMap<String, CellStyle>()

        Font fuenteNormal = this.workbook.createFont()
        fuenteNormal.setFontHeightInPoints((short) 8)
        fuenteNormal.setColor(IndexedColors.BLACK.getIndex())
        fuenteNormal.setBold(false)

        Font fuenteSubtitulo = this.workbook.createFont()
        fuenteSubtitulo.setFontHeightInPoints((short) 10)
        fuenteSubtitulo.setColor(IndexedColors.BLACK.getIndex())
        fuenteSubtitulo.setBold(true)

        Font fuenteTitulo = this.workbook.createFont()
        fuenteTitulo.setFontHeightInPoints((short) 11)
        fuenteTitulo.setColor(IndexedColors.BLACK.getIndex())
        fuenteTitulo.setBold(true)

        Font funteRoja = this.workbook.createFont()
        funteRoja.setFontHeightInPoints((short) 8)
        funteRoja.setColor(IndexedColors.RED.getIndex())
        funteRoja.setBold(true)

        CellStyle cellStyle = crearBordeCelda()

        // Estilo título
        cellStyle.setAlignment(HorizontalAlignment.CENTER)
        cellStyle.setWrapText(true)
        cellStyle.setFont(fuenteTitulo)
        cellStyle.setFillBackgroundColor(IndexedColors.GREY_25_PERCENT.getIndex())
        cellStyle.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.index)
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND)
        this.listaEstilos.put("estilo_titulo", cellStyle)

        // Estilo subtitulo
        cellStyle = crearBordeCelda()
        cellStyle.setAlignment(HorizontalAlignment.CENTER)
        cellStyle.setWrapText(true)
        cellStyle.setFont(fuenteSubtitulo)
        cellStyle.setFillBackgroundColor(IndexedColors.GREY_25_PERCENT.getIndex())
        cellStyle.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.index)
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND)
        this.listaEstilos.put("estilo_subtitulo", cellStyle)

        // Estilo contenido normal
        cellStyle = crearBordeCelda()
        cellStyle.setAlignment(HorizontalAlignment.LEFT)
        cellStyle.setWrapText(false)
        cellStyle.setFont(fuenteNormal)
        cellStyle.setBorderLeft(BorderStyle.THIN)
        this.listaEstilos.put("estilo_contenido_normal", cellStyle)

        // Estilo contenido alineado a la derecha
        cellStyle = crearBordeCelda()
        cellStyle.setAlignment(HorizontalAlignment.RIGHT)
        cellStyle.setWrapText(false)
        cellStyle.setFont(fuenteNormal)
        cellStyle.setBorderLeft(BorderStyle.THIN)
        this.listaEstilos.put("estilo_contenido_derecha", cellStyle)

        // Estilo contenido centrado
        cellStyle = crearBordeCelda()
        cellStyle.setAlignment(HorizontalAlignment.CENTER)
        cellStyle.setWrapText(false)
        cellStyle.setFont(fuenteNormal)
        cellStyle.setBorderLeft(BorderStyle.THIN)
        this.listaEstilos.put("estilo_contenido_centrado", cellStyle)

        // Estilo contenido rojo
        cellStyle = crearBordeCelda()
        cellStyle.setAlignment(HorizontalAlignment.CENTER)
        cellStyle.setWrapText(false)
        cellStyle.setFont(funteRoja)
        cellStyle.setBorderLeft(BorderStyle.THIN)
        this.listaEstilos.put("estilo_contenido_rojo", cellStyle)
    }

    /**
     * Crea los bordes para el estilo
     *
     * @return Objeto de clase CellStyle
     */
    private CellStyle crearBordeCelda() {
        CellStyle style = this.workbook.createCellStyle()
        style.setBorderRight(BorderStyle.THIN)
        style.setRightBorderColor(IndexedColors.BLACK.getIndex())

        style.setBorderBottom(BorderStyle.THIN)
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex())

        style.setBorderLeft(BorderStyle.THIN)
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex())

        style.setBorderTop(BorderStyle.THIN)
        style.setTopBorderColor(IndexedColors.BLACK.getIndex())
        return style
    }
}