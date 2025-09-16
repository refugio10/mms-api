package dsi.edoMex.modulomonitoreo.modulomonitoreo.service.utilerias

import com.itextpdf.text.Font
import com.itextpdf.text.Image
import com.itextpdf.text.Rectangle
import com.itextpdf.text.Document
import com.itextpdf.text.Element
import com.itextpdf.text.Phrase
import com.itextpdf.text.pdf.ColumnText
import com.itextpdf.text.pdf.PdfContentByte
import com.itextpdf.text.pdf.PdfPageEventHelper
import com.itextpdf.text.pdf.PdfWriter

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

/**
 * Contiene funciones para el cierre del documento PDF
 *
 * @author lorenav
 * @version 1.1 20/12/2024
 */
class HeaderPdf extends PdfPageEventHelper {
    private int interlineado = 10
    private float posicionYTexto
    private def fuentes
    private String titulo

    /**
     * Constructor de la clase
     *
     * @param titulo Cadena con el título que llevará el encabezado de las hojas
     */
    HeaderPdf(String titulo, List<Font> fuentes) {
        this.titulo = titulo
        this.fuentes = fuentes
    }

    /**
     * Agrega el encabezado de las páginas antes de finalizar el documento
     *
     * @param writer La propiedad de un PDF para su escritura.
     * @param document El Documento PDF que ejecuta el evento.
     */
    void onEndPage(PdfWriter writer, Document document) {
        try {
            PdfContentByte contentByte = writer.getDirectContent()
            float posicionXInicial = document.left()
            float posicionYInicial = (document.top() + 10) as float
            this.posicionYTexto = (posicionYInicial + 45) as float
            float alineacionCentrar = ((document.right() - document.left() + 170) / 2 + document.leftMargin()) as float

            BufferedImage bufferedImage = ImageIO.read(Objects.requireNonNull(getClass().getClassLoader().getResource("imagen/escudoGobierno.png")))
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                ImageIO.write(bufferedImage, "png", baos)
                Image image = Image.getInstance(baos.toByteArray())
                image.scaleAbsolute(170f, 45f)
                image.setAbsolutePosition(posicionXInicial, posicionYInicial)
                contentByte.addImage(image)
            } catch (Exception e) {
                e.printStackTrace()
            }

            agregarTitulo(contentByte, "Secretaría del Medio Ambiente", this.fuentes.get(5), alineacionCentrar)
            agregarTitulo(contentByte, "Dirección General de Prevención y Control de la Contaminación Atmosférica", this.fuentes.get(5), alineacionCentrar)
            agregarTitulo(contentByte, "Dirección de fuentes moviles", this.fuentes.get(5), alineacionCentrar)
            agregarTitulo(contentByte, titulo, this.fuentes.get(5), alineacionCentrar)

            Rectangle rectangle = new Rectangle(document.left(), document.top(), document.right(), (document.top() + document.topMargin() - document.left()) as float)
            rectangle.setBorder(Rectangle.BOTTOM)
            rectangle.setBorderWidth(2)
            contentByte.rectangle(rectangle)
        } catch (Exception e) {
            e.printStackTrace()
        }
    }

    /**
     * Agrega un texto al encabezado
     * @param contentByte objeto de clase PdfContentByte que contiene el byte de documeto
     * @param titulo Cadena que se le agregará al encabezado
     * @param fuente Fuente que se le agregara al título
     * @param posicionX Posición inicial en el eje x
     */
    void agregarTitulo(PdfContentByte contentByte, String titulo, fuente, float posicionX) {
        posicionYTexto = (posicionYTexto - interlineado) as float
        ColumnText.showTextAligned(contentByte, Element.ALIGN_CENTER, new Phrase(titulo, fuente), posicionX, posicionYTexto, 0)
    }
}