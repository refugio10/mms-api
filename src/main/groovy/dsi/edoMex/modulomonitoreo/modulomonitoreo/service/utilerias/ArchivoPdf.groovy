package dsi.edoMex.modulomonitoreo.modulomonitoreo.service.utilerias

import com.itextpdf.text.Document
import com.itextpdf.text.Font
import com.itextpdf.text.PageSize
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.pdf.PdfGState
import com.itextpdf.text.pdf.PdfContentByte
import com.itextpdf.text.BaseColor
import com.itextpdf.text.Chunk
import com.itextpdf.text.Rectangle
import com.itextpdf.text.Element
import com.itextpdf.text.Image

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

import java.nio.file.Files

/**
 * Contiene los atributos y métodos genéricos para crear un archivo PDF con itext
 *
 * @author Refugio Rodriguez Bueno
 * @version 1.0 Agosto 2025
 */
class ArchivoPdf implements AutoCloseable {
    float alturaCelda = 18f
    float interlineado = 1.2f
    boolean colorCelda = false
    Document document
    ByteArrayOutputStream baos
    PdfWriter pdfWriter
    PdfPTable pdfPTable
    PdfPTable pdfPTableSecundario
    PdfPCell pdfPCell
    List<Font> fuentes
    Paragraph paragraph

    /**
     * Constructor de la clase
     *
     * @param orientacionVertical [true: orientación vertical, false: orientación horizontal]
     * @param encabezadoTitulo Cadena con el titulo que llevará el encabezado
     * @param piePdf [true: se agregará el pie de página, false: no se agrega el pie de página]
     */
    ArchivoPdf(boolean orientacionVertical = true, String encabezadoTitulo, boolean encabezadoPdf = true, boolean piePdf = true) {
        try {
            agregarFuentes()
            this.document = new Document(orientacionVertical ? PageSize.LETTER : PageSize.LETTER.rotate(), 35f, 35f, 80f, 80f)
            this.baos = new ByteArrayOutputStream()
            this.pdfWriter = PdfWriter.getInstance(this.document, this.baos)

            if (encabezadoPdf) pdfWriter.setPageEvent(new HeaderPdf(encabezadoTitulo, fuentes))
            if (piePdf) pdfWriter.setPageEvent(new FooterPdf(encabezadoTitulo, fuentes))

            this.document.open()
        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    /**
     * Inicializa las fuentes que se pueden usar en el pdf
     */
    void agregarFuentes() {
        this.fuentes = new ArrayList<>()
        fuentes.add(new Font(Font.FontFamily.HELVETICA, 14f, Font.BOLD))
        fuentes.add(new Font(Font.FontFamily.HELVETICA, 13f, Font.BOLD))
        fuentes.add(new Font(Font.FontFamily.HELVETICA, 12f, Font.BOLD))
        fuentes.add(new Font(Font.FontFamily.HELVETICA, 11f, Font.BOLD))
        fuentes.add(new Font(Font.FontFamily.HELVETICA, 10f, Font.BOLD))
        fuentes.add(new Font(Font.FontFamily.HELVETICA, 9f, Font.BOLD))
        fuentes.add(new Font(Font.FontFamily.HELVETICA, 12f, Font.NORMAL))
        fuentes.add(new Font(Font.FontFamily.HELVETICA, 11f, Font.NORMAL))
        fuentes.add(new Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL))
        fuentes.add(new Font(Font.FontFamily.HELVETICA, 9f, Font.NORMAL))
        fuentes.add(new Font(Font.FontFamily.HELVETICA, 8f, Font.NORMAL))
        fuentes.add(new Font(Font.FontFamily.HELVETICA, 10f, Font.FontStyle.BOLD.ordinal(), new BaseColor(129, 123, 129)))
    }

    /**
     * Crea una nueva instancia de PdfPTable
     *
     * @param tamano Arreglo con las columnas de la tabla
     * @param porcentaje Porcentaje del tamaño que ocupará en el documento
     */
    void creaTabla(float[] tamano, float porcentaje = 100f) {
        this.pdfPTable = new PdfPTable(tamano)
        pdfPTable.setWidthPercentage(porcentaje)
    }

    /**
     * Crea una nueva instancia de PdfPTable
     *
     * @param tamano Arreglo con las columnas de la tabla
     * @param porcentaje Porcentaje del tamaño que ocupará en el documento
     */
    void creaTablaSecundario(float[] tamano, float porcentaje = 100f) {
        this.pdfPTableSecundario = new PdfPTable(tamano)
        pdfPTableSecundario.setWidthPercentage(porcentaje)
    }

    /**
     * Agrega una nueva celda de texto a la tabla
     *
     * @param fuente Entero con la posición de la lista de fuentes
     * @param contenido Cadena con el texto o contenido de la celda
     * @param border [true: Agrega el borde a la celda, false: No agrega el borde a la celda]
     * @param alineacion Entero con la alineación de texto
     * @param colSpan Cantidad de columnas a agrupar
     * @param rowSpan Cantidad de filas a agrupar
     * @param enlace Cadena con la referencia de un link o enlace
     */
    void creaCelda(int fuente, Object contenido, boolean border = false, int alineacion, int colSpan = 0, int rowSpan = 0) {
        if (contenido instanceof Paragraph) {
            pdfPCell = new PdfPCell((Paragraph) contenido)
            this.paragraph = null
        } else if (contenido instanceof PdfPTable) {
            pdfPCell = new PdfPCell((PdfPTable) contenido)
        } else if (contenido instanceof Image) {
            pdfPCell = new PdfPCell(contenido)
        } else {
            pdfPCell = new PdfPCell(new Paragraph(new Chunk(contenido as String, fuentes.get(fuente))))
        }

        pdfPCell.setBorder(border ? Rectangle.BOX : 0)

        if (this.colorCelda) pdfPCell.setBackgroundColor(new BaseColor(200, 207, 203))
        if (colSpan > 0) pdfPCell.setColspan(colSpan)
        if (rowSpan > 0) pdfPCell.setRowspan(rowSpan)

        pdfPCell.setBorderColor(new BaseColor(135, 137, 135))
        pdfPCell.setHorizontalAlignment(alineacion)
        pdfPCell.setVerticalAlignment(Element.ALIGN_CENTER)
        pdfPCell.setMinimumHeight(alturaCelda)
        pdfPCell.setLeading(0f, interlineado)

        if (pdfPTableSecundario != null && !(contenido instanceof PdfPTable))
            pdfPTableSecundario.addCell(pdfPCell)
        else
            pdfPTable.addCell(pdfPCell)
    }

    /**
     * Agrega una nueva celda con imagen a la tabla
     *
     * @param imagen Cadena con el nombre del archivo
     * @param ancho Entero con el tamaño del ancho de la imagen
     * @param alto Entero con el tamaño del alto de la imagen
     * @param alineacion Entero con la alineación de la imagen
     * @param colSpan Entero con las columnas que se van a agrupar dentro de la tabla
     * @param rowSpan Entero con las filas que se van a agrupar dentro de la tabla
     * @param esInterno [true: pertenece al recurso del proyecto, false: pertenece al recurso que se encuentre en la NAS]
     */
    void creaCeldaImagen(String imagen, String extension = "", float ancho, float alto, boolean border = false,
                         int alineacion, int colSpan = 0, int rowSpan = 0, boolean esInterno = true) {
        try {
            byte[] imagenByte = null
            if (esInterno) {
                BufferedImage bufferedImage = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("imagen/" + imagen)))
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    ImageIO.write(bufferedImage, extension, baos)
                    imagenByte = baos.toByteArray()
                } catch (Exception e) {
                    e.printStackTrace()
                }
            } else {
                try {
                    File file = new File(imagen)
                    imagenByte = Files.readAllBytes(file.toPath())
                } catch (Exception e) {
                    e.printStackTrace()
                }
            }

            if (imagenByte == null) {
                creaCelda(2, "", border, alineacion, colSpan, rowSpan)
                return
            }

            Image image = Image.getInstance(imagenByte)
            image.scaleAbsolute(ancho, alto)
            image.setAlignment(Image.MIDDLE)

            creaCelda(0, image, border, alineacion, colSpan, rowSpan)
        } catch (Exception ex) {
            ex.printStackTrace()
        }
    }

    /**
     * Agrega contenido a un parrafo con diferentes fuentes
     *
     * @param contenido Cadena con el texto del parrafo
     * @param fuente Entero con la posición de la lista de fuentes
     */
    void generaParrafo(String contenido, int fuente) {
        if (this.paragraph == null)
            this.paragraph = new Paragraph()

        this.paragraph.add(new Chunk(contenido, fuentes.get(fuente)))
    }

    /**
     * Agrega una imagen como marca de agua al documento
     *
     * @param imagen Cadena con el nombre de la imagen
     * @param extension Cadena con la extensión de la imagen
     * @param ancho Entero con el ancho de la imagen
     * @param alto Entero con el alto de la imagen
     */
    void marcaAguaImagen(String imagen, String extension, int ancho, int alto, int posicionX, int posicionY, float rotacion) {
        try (BufferedImage bufferedImage = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("imagen/" + imagen)))) {
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                ImageIO.write(bufferedImage, extension, baos)

                Image image = Image.getInstance(baos.toByteArray())
                image.scaleAbsolute(ancho, alto)
                image.setAlignment(Image.MIDDLE)
                image.setAbsolutePosition(posicionX, posicionY)
                image.setRotation(rotacion)

                PdfGState state = new PdfGState()
                state.setFillOpacity(0.3f)

                PdfContentByte canvas = pdfWriter.getDirectContentUnder()
                canvas.setGState(state)
                canvas.addImage(image)
            } catch (Exception ex) {
                ex.printStackTrace()
            }
        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    void agregarEspacioEntreTablas(ArchivoPdf pdf, float espacioPuntos = 15f) {
        // Crear un párrafo vacío con espacio antes
        Paragraph espacio = new Paragraph(" ")
        espacio.setSpacingBefore(espacioPuntos)
        pdf.document.add(espacio)
    }

    /**
     * Permite cerrar el documento una vez haya terminado de usarse
     */
    @Override
    void close() throws Exception {
        this?.document?.close()
    }
}