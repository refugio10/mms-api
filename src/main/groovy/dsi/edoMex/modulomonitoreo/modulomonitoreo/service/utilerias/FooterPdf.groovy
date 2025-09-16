package dsi.edoMex.modulomonitoreo.modulomonitoreo.service.utilerias

import com.itextpdf.text.Chunk
import com.itextpdf.text.Document
import com.itextpdf.text.BaseColor
import com.itextpdf.text.Element
import com.itextpdf.text.Font
import com.itextpdf.text.Paragraph
import com.itextpdf.text.Rectangle
import com.itextpdf.text.pdf.ColumnText
import com.itextpdf.text.pdf.PdfContentByte
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfPageEventHelper
import com.itextpdf.text.pdf.PdfTemplate
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.pdf.BaseFont

import java.text.SimpleDateFormat

/**
 * Contiene funciones para el cierre del documento PDF
 *
 * @author lorenav
 * @version 1.0 06/01/2023
 */
class FooterPdf extends PdfPageEventHelper {
    PdfTemplate pdfTemplate
    String titulo
    PdfPTable pdfPTable
    PdfPCell pdfPCell
    private def fuentes

    FooterPdf(String titulo, List<Font> fuentes) {
        this.titulo = titulo
        this.fuentes = fuentes
    }

    /**
     * Inicializa el template cuando se abre el documento
     *
     * @param writer La propiedad de un PDF para su escritura.
     * @param document El Documento PDF que ejecuta el evento.
     */
    void onOpenDocument(PdfWriter writer, Document document) {
        pdfTemplate = writer.getDirectContent().createTemplate(100, 100)
        pdfTemplate.setBoundingBox(new Rectangle(-20, -20, 100, 100))
    }

    /**
     * Agrega la numeración de las páginas mientras se van escribiendo
     *
     * @param writer La propiedad de un PDF para su escritura.
     * @param document El Documento PDF que ejecuta el evento.
     */
    void onEndPage(PdfWriter writer, Document document) {
        try {
            pdfPTable = new PdfPTable(new float[]{33f, 34f, 33f})
            pdfPTable.setWidthPercentage(100f)

            creaCelda(9, "", Element.ALIGN_CENTER)
            creaCelda(9, "GOBIERNO DEL ESTADO DE MÉXICO", Element.ALIGN_CENTER)
            creaCelda(9, "Fecha de impresión " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()), Element.ALIGN_RIGHT)

            creaCelda(9, titulo, Element.ALIGN_CENTER)
            creaCelda(9, "Dirección General de Prevención y Control de la Contaminación Atmosférica", Element.ALIGN_CENTER)
            creaCelda(9, "", Element.ALIGN_CENTER)

            creaCelda(9, "Secretaría del Medio Ambiente", Element.ALIGN_CENTER, 3)

            PdfContentByte contentByte = writer.getDirectContent()
            Rectangle rectangle = new Rectangle(document.left(), 15, document.right(), document.bottom())
            rectangle.setBorder(Rectangle.TOP)
            rectangle.setBorderWidth(1)
            contentByte.rectangle(rectangle)

            ColumnText columnText = new ColumnText(contentByte)
            columnText.setSimpleColumn(rectangle)
            columnText.addElement(pdfPTable)
            columnText.go()

            // Está sección es para la paginación
            BaseFont baseFont = BaseFont.createFont("Helvetica", BaseFont.WINANSI, false)

            String text = "Página: " + writer.getPageNumber() + " de "
            float textBase = document.bottom() - 30
            float textSize = baseFont.getWidthPoint(text, 9)
            float adjust = baseFont.getWidthPoint("0000", 9)

            contentByte.beginText()
            contentByte.setFontAndSize(baseFont, 9)
            contentByte.setColorFill(new BaseColor(0, 0, 0))
            contentByte.setTextMatrix((document.right() - textSize - adjust) as float, textBase)
            contentByte.showText(text)
            contentByte.endText()
            contentByte.addTemplate(pdfTemplate, document.right() - adjust, textBase)
            contentByte.beginText()
            contentByte.setFontAndSize(baseFont, 10)
            contentByte.setTextMatrix(document.left(), textBase)
            contentByte.endText()
        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    /**
     * Agrega una nueva celda de texto a la tabla
     *
     * @param fuente Entero con la posición de la lista de fuentes
     * @param contenido Cadena con el texto o contenido de la celda
     * @param alineacion Entero con la alineación de texto
     * @param colSpan Cantidad de columnas a agrupar
     */
    void creaCelda(int fuente, String contenido, int alineacion, int colSpan = 0) {
        pdfPCell = new PdfPCell(new Paragraph(new Chunk(contenido, fuentes.get(fuente))))

        if (colSpan > 0) pdfPCell.setColspan(colSpan)

        pdfPCell.setHorizontalAlignment(alineacion)
        pdfPCell.setVerticalAlignment(Element.ALIGN_CENTER)
        pdfPCell.setMinimumHeight(15f)
        pdfPCell.setBorder(0)
        pdfPTable.addCell(pdfPCell)
    }

    /**
     * Agrega el total de páginas cuando se cierre el documento
     *
     * @param writer La propiedad de un PDF para su escritura.
     * @param document El Documento PDF que ejecuta el evento.
     */
    void onCloseDocument(PdfWriter writer, Document document) {
        try {
            BaseFont baseFont = BaseFont.createFont("Helvetica", BaseFont.WINANSI, false)
            pdfTemplate.beginText()
            pdfTemplate.setFontAndSize(baseFont, 9)
            pdfTemplate.setTextMatrix(0, 0)
            pdfTemplate.showText(String.valueOf(writer.getPageNumber()))
            pdfTemplate.endText()
        } catch (Exception e) {
            e.printStackTrace()
        }
    }
}