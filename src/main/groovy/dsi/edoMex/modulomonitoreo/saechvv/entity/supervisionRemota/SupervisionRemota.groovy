package dsi.edoMex.modulomonitoreo.saechvv.entity.supervisionRemota

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

/**
 * Contiene la información de la tabla SUPER_REMOTA de la base de SAECHVV
 *
 * @author Refugio Rodríguez Bueno
 * @version 1.0 Julio 2025
 */
@Entity
@Table(name = "SUPER_REMOTA")
@JsonIgnoreProperties(["hibernateLazyInitializer", "handler"])
class SupervisionRemota {
    @Id
    @Column(name = "IDSUPERREMOTA")
    Integer id

    @Column(name = "IDUSUARIO")
    Integer usuario

    @Column(name = "IDVERIFICENTRO")
    Integer verificentro

    @Column(name = "FOLIO")
    String folio

    @Column(name = "ESTATUS")
    int estatus

    @Column(name = "FECHA_REGISTRO")
    Date fechaRegistro

    @Column(name = "FECHA_CIERRE")
    Date fechaCierre

    @Column(name = "OBSERVACIONES")
    String observaciones

    @Column(name = "TIPO_SUPERVICION")
    Integer tipoSupervision

    @Column(name = "PREGUARDADO")
    Integer preguardado

    @Column(name = "FOLIOINI")
    Integer folioIni

    @Column(name = "FOLIOFIN")
    Integer folioFin

    @Column(name = "CONANOMALIAS")
    Integer conAnomalias

    @Column(name = "RUTA_ZIP")
    String rutaZip

    @Column(name = "NO_EXPEDIENTE")
    int numeroExpediente

    @Column(name = "NO_VIDEO")
    int numeroVideo

    @Column(name = "FOLIO_PDF")
    String folioPdf
}