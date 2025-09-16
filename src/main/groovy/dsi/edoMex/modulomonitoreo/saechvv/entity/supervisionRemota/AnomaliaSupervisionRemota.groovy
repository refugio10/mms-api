package dsi.edoMex.modulomonitoreo.saechvv.entity.supervisionRemota

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import dsi.edoMex.modulomonitoreo.saechvv.entity.administracion.Usuario
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

/**
 * Contiene la información de la tabla ANOMALIASSR de la base de SAECHVV
 *
 * @author Refugio Rodríguez Bueno
 * @version 1.0 Julio 2025
 */
@Entity
@Table(name="ANOMALIASSR")
@JsonIgnoreProperties(["hibernateLazyInitializer", "handler"])
class AnomaliaSupervisionRemota {

    @Id
    @Column(name = "IDANOMALIASSR")
    Integer id

    @Column(name = "FECHA_REGISTRO")
    Date fechaRegistro

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "IDSUPERREMOTA")
    SupervisionRemota supervisionRemota

    @Column(name = "FOLIO")
    String folio

    @Column(name = "TIPO_ANOMALIA")
    String tipoAnomalia

    @Column(name = "OBSERVACION")
    String observacion

    @Column(name = "ACTIVO")
    Integer activo

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "IDUSUARIO")
    Usuario usuario

    @Column(name = "FECHACREACION")
    Date fechaCreacion

    @Column(name = "RUTA")
    String ruta

}