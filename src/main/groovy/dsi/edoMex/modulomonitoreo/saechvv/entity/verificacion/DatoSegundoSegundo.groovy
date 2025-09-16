package dsi.edoMex.modulomonitoreo.saechvv.entity.verificacion

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

/**
 * Contiene la información de la tabla SS_TEMPORAL de la base de SAECHVV
 *
 * @author Refugio Rodríguez Bueno
 * @version 1.0 Agosto 2025
 */
@Entity
@Table(name = "SS_TEMPORAL")
@JsonIgnoreProperties(["hibernateLazyInitializer", "handler"])
class DatoSegundoSegundo {

    @Id
    @Column(name = "IDSSTEMPORAL")
    Integer id

    @Column(name = "IDVERIFICACION")
    Integer verificacion

    @Column(name = "SS")
    String datoSegundoSegundo

    @Column(name = "ESTADO")
    Integer estado

    @Column(name = "FECHA_REGISTRO")
    Date fechaRegistro

    @Column(name = "FECHA_MIGRADO")
    Date fechaMigrado
}