package dsi.edoMex.modulomonitoreo.saechvv.entity.supervisionRemota

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

/**
 * Contiene la información de la tabla INCIDENCIAS_OCR de la base de SAECHVV
 *
 * @author Refugio Rodríguez Bueno
 * @version 1.0 Agosto 2025
 */
@Entity
@Table(name = "INCIDENCIAS_OCR")
@JsonIgnoreProperties(["hibernateLazyInitializer", "handler"])
class IncidenciaOCR {
    @Id
    @Column(name = "IDINCIDENCIAOCR")
    Integer id

    @Column(name = "IDVERIFICACION")
    Integer verificacion

    @Column(name = "ID_ADJUNTO")
    Integer adjunto

    @Column(name = "TIPO_ELEMENTO")
    Integer tipoElemento

    @Column(name = "VALOR_ESPERADO")
    String valorEsperado

    @Column(name = "VALOR_ENCONTRADO")
    String valorEncontrado

    @Column(name = "FECHA_REGISTRO")
    Date fechaRegistro

    @Column(name = "ESTATUS")
    Integer estatus

    @Column(name = "FECHA_VALIDACION")
    Date fechaValidacion

    @Column(name = "IDUSUARIO_VALIDA")
    Integer usuarioValida
}